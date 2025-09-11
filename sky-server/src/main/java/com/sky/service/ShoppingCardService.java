package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

public interface ShoppingCardService extends IService<ShoppingCart> {
    /*
     * 添加购物车
     * */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
