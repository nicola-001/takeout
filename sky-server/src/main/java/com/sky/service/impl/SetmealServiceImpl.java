package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    /*
    * 根据分类id查询套餐接口
    * */
    @Override
    public List<Setmeal> myList(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.myList(setmeal);
        return list;
    }

    @Override
    public List<DishItemVO> getItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
