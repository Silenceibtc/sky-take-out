package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {

    @AutoFill(OperationType.INSERT)
    void save(Dish dish);

    Page<DishVO> page();
}
