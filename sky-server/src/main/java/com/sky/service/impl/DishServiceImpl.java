package com.sky.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMepper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMepper setmealDishMepper;

    /*
     * 新增菜品
     * */
    @Transactional//开启事务 保证数据一致性
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        // 拷贝对象属性，必须保持对象属性名一致性
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入一条数据
        dishMapper.insert(dish);
        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                //设置菜品id
                dishFlavor.setDishId(dish.getId());
            });
            dishFlavorMapper.insertBash(flavors);
        }
    }

    //菜品分页查询
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /*
     * 批量删除菜品
     * */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否在售
        for (Long id : ids) {
            Dish dish = dishMapper.selectById(id);
            if (dish == null) {
                throw new DeletionNotAllowedException(MessageConstant.ID_NOT_FOUND);
            }
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //是否被某个套餐关联
        List<Long> setmealIdsByDishIds = setmealDishMepper.getSetmealIdsByDishIds(ids);
        if (setmealIdsByDishIds != null && setmealIdsByDishIds.size() > 0) {
            //当前菜品被某个套餐关联，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中的数据
//        for (Long id : ids) {
//            System.out.println(id + "删除菜品表数据");
//            dishMapper.deleteById(id);
//            //删除口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }

        // 根据菜品id批量删除菜品数据
        dishMapper.deleteByIds(ids);
        // 根据菜品id批量删除口味数据
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /*
     * 根据id查询菜品和口味数据
     * */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 查询菜品数据
        Dish dish = dishMapper.selectById(id);
        // 查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.listByDishId(id);
        // 组装数据并返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /*
     * 修改菜品
     * */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品信息

        //创建菜品对象
        Dish dish = new Dish();
        //将前端传递过来的数据拷贝到dish对象中，属性名一致
        BeanUtils.copyProperties(dishDTO, dish);
        //修改菜品表
        dishMapper.updateById(dish);

        //修改口味信息
        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBash(flavors);
        }
    }

    /*
     * 查询分类
     * */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        // 查询菜品数据
        List<Dish> dishList = dishMapper.list(dish);
        // 创建DishVO对象列表
        List<DishVO> dishVOList = new ArrayList<>();
        // 遍历菜品数据，将每个Dish对象转换成DishVO对象
        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> dishFlavors = dishFlavorMapper.listByDishId(d.getId());
            dishVO.setFlavors(dishFlavors);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }

    //启售停售菜品
    @Override
    public void startOrStop(Integer status, Long id) {
        dishMapper.startOrStop(status, id);
    }


}
