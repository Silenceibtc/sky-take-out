package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 自动处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        log.info("开始定时检查超时订单...");
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(15);
        List<Orders> orders = orderMapper.selectByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, localDateTime);
        if (orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                order.setStatus(Orders.CANCELLED);
                orderMapper.update(order);
            }
        }
    }

    /**
     * 自动处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    private void processDeliveryOrder() {
        log.info("开始定时检查一直处于派送中的订单...");
        LocalDateTime localDateTime = LocalDateTime.now().minusHours(1);
        List<Orders> orders = orderMapper.selectByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, localDateTime);
        if (orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }
}
