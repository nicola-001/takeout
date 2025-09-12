package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
    void insertBatch(ArrayList<OrderDetail> orderDetailList);
}
