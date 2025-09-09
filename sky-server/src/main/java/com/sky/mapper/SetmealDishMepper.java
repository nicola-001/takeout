package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

// select setmeal_id from setmeal_dish where dish_id in (?, ?, ?)
@Mapper
public interface SetmealDishMepper extends BaseMapper<SetmealDish> {
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
