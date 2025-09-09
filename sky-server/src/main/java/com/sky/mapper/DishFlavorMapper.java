package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
    void insertBash(List<DishFlavor> flavors);

    //根据菜品id批量删除菜品数据
    void deleteByDishId(Long dishId);

    //根据菜品id批量删除口味数据
    void deleteByDishIds(List<Long> ids);

    List<DishFlavor> listByDishId(Long dishId);
}
