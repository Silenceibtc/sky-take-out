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
import java.util.Map;

@Mapper
public interface DishMapper {

    @AutoFill(OperationType.INSERT)
    void save(Dish dish);

    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    void batchDelete(List<Long> ids);

    List<Dish> selectByIds(List<Long> ids);

    @Select("select * from dish where id = #{dishId}")
    Dish selectById(Long dishId);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    @Select("select * from dish where category_id = #{categoryId} and status = 1")
    List<Dish> selctByCategoryId(Long categoryId);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
