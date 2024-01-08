package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 查询菜品是否关联套餐
     * @param ids
     * @return
     */
    Integer selectCountByDishId(List<Long> ids);

    /**
     * 新增某个套餐关联的所有菜品
     * @param setmealDishes
     */
    void save(List<SetmealDish> setmealDishes);

    /**
     * 查询某个套餐关联的所有菜品
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> selectBySetmealId(Long setmealId);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 删除套餐
     * @param setmealIds
     */
    void delete(List<Long> setmealIds);
}
