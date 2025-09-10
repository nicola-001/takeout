package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService extends IService<Setmeal> {
    //条件查询
    List<Setmeal> myList(Setmeal setmeal);

    List<DishItemVO> getItemById(Long id);
}
