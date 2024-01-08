package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    @AutoFill(OperationType.INSERT)
    void save(Dish dish);

    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    void batchDelete(List<Long> ids);

    List<Dish> selectByIds(List<Long> ids);

    @Select("select * from dish where id = #{dishId}")
    DishVO selectById(Long dishId);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selctByCategoryId(Long categoryId);
}
