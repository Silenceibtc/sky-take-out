package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 更新订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 订单条件分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> page(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(*) from orders where status = #{status}")
    Integer statisticsByStatus(Integer status);
}