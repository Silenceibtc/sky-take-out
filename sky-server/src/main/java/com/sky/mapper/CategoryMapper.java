package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 修改分类
     * @param category
     */
    void update(Category category);

    /**
     * 新增分类
     * @param category
     */
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) values " +
            "(#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void save(Category category);

    /**
     * 分类分页查询
     * @return
     */
    Page<Category> page();

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @Select("select * from category where type = #{type}")
    List<Category> selectByType(Integer type);

    @Delete("delete from category where id = #{id}")
    void delete(Integer id);
}
