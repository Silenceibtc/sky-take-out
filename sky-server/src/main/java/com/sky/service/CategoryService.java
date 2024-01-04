package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

/**
 * 分类管理
 */
public interface CategoryService {

    /**
     * 修改分类
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 新增分类
     * @param categoryDTO
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> selectByType(Integer type);

    /**
     * 设置分类状态
     *
     * @param status
     * @param id
     */
    void setStatus(Integer status, Long id);

    /**
     * 根据id删除分类
     * @param id
     */
    void delete(Integer id);
}
