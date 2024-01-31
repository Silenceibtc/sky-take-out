package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * 查询某个状态的订单数量
     * @param status 订单状态
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    Integer statisticsByStatus(Integer status);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders selectById(Long id);

    /**
     * 根据状态和时间查询订单
     * @param status
     * @param localDateTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{localDateTime}")
    List<Orders> selectByStatusAndOrderTimeLT(Integer status, LocalDateTime localDateTime);

    /**
     * 统计当日营业额
     *
     * @param map
     * @return
     */
    Double turnover(Map map);

    /**
     * 订单统计
     * @param map
     * @return
     */
    Integer statisticsOrderByMap(Map<Object, Object> map);
}
