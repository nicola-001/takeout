package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.websocket.WebSocketServer;
import io.swagger.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Value("${app.pay.mock-enabled:false}")
    private boolean mockEnabled; // 是否启用模拟支付
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;

    @Transactional// 开启事务注解
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        //处理各种异常（地址是否为空，购物车是否为空）
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        AddressBook addressDetail = addressBookMapper.getById(addressBookId);
        if (addressDetail == null) {
            //地址为空则抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.myList(shoppingCart);
        if (list == null || list.size() == 0) {
            //购物车为空则抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        //下单时间
        orders.setOrderTime(LocalDateTime.now());
        //支付状态
        orders.setPayStatus(Orders.UN_PAID);
        //订单状态
        orders.setStatus(Orders.PENDING_PAYMENT);
        //下单数量
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        //手机号
        orders.setPhone(addressDetail.getPhone());
        //收货人
        orders.setConsignee(addressDetail.getConsignee());
        //属于哪个用户
        orders.setUserId(userId);
        orderMapper.insert(orders);
        //向订单明细表插入多条数据

        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();//订单明细
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());//设置当前订单明细关联的id
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);
        //删除购物车数据
        shoppingCartMapper.deleteByUserId(userId);
        //封装Vo值返回
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.selectById(userId);

        // 如果启用了模拟支付，直接构造模拟数据，跳过微信调用
        if (mockEnabled) {
            System.out.println("模拟支付成功");
        }
//
//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));
//
//        return vo;
        // 👇 下面这 6 行，就是你全部需要的“模拟支付”逻辑！
        OrderPaymentVO vo = new OrderPaymentVO();
        vo.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));
        vo.setNonceStr(RandomStringUtils.randomAlphanumeric(32));
        vo.setPackageStr("prepay_id=mock_" + System.currentTimeMillis());
        vo.setSignType("RSA");
        vo.setPaySign("mock_pay_sign_2025"); // 任意字符串，前端不验签

        log.info(" 模拟支付成功，已返回伪造数据: {}", vo);
        return vo;
    }


    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        //通过webscket向客户端浏览器推送消息 type orderId content
        Map map = new HashMap<>();
        map.put("type", 1);//1 来单提醒  2客户催单
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号"+outTradeNo+"支付成功");

        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

}
