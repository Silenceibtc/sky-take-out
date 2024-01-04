package com.sky.service;

import com.sky.dto.CategoryDTO;
import org.springframework.stereotype.Service;

/**
 * 分类管理
 */
public interface CategoryService {

    /**
     * 修改分类
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);
}
