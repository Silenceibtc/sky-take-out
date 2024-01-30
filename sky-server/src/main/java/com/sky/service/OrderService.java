package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    void payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 统计各状态订单数量
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);

    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     */
    void reject(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 接受订单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    Orders detail(Long id);

    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 订单分页查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult page(int page, int pageSize, Integer status);
//
//    /**
//     * 支付成功，修改订单状态
//     * @param outTradeNo
//     */
//    void paySuccess(String outTradeNo);
}
