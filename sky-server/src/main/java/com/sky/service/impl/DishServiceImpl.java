package com.sky.service.impl;

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
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper  setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional //声明为事务，具有原子性，所有操作要么全部成功，要么全部失败
    public void saveWithFlavors(DishDTO dishDTO) {
        //存菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.save(dish);
        //存菜品对应的口味，存在的话
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //获取菜品主键id存入口味表
        Long dishId = dish.getId();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            dishFlavorMapper.save(flavors);
        }
    }

    /**、
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        //分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.page();
        //获取返回值
        long total = page.getTotal();
        List<DishVO> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    public void batchDelete(List<Long> ids) {
        //判断要删的菜品是否为起售状态，是则不能删除
        List<Dish> dishes = dishMapper.selectByIds(ids);
        for (Dish dish : dishes) {
            if (dish.getStatus() == StatusConstant.ENABLE)
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        //判断要删的菜品是否有关联套餐，有则不能删除
        Integer count = setmealDishMapper.selectCountByDishId(ids);
        if (count > 0)
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);

        //删除菜品
        dishMapper.batchDelete(ids);

        //删除菜品关联口味
        dishFlavorMapper.deleteFlavorsByDishIds(ids);
    }
}
