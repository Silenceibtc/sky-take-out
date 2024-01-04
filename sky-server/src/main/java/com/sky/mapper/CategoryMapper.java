package com.sky.mapper;

import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    void update(Category category);
}
