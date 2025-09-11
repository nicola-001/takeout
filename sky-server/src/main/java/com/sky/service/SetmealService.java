package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService extends IService<Setmeal> {
    //条件查询
    List<Setmeal> myList(Setmeal setmeal);

    List<DishItemVO> getItemById(Long id);

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);


    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
}
