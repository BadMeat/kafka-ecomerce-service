package com.zarta.order.service;

import com.zarta.order.dto.OrderDto;

public interface OrderService {
    void sendOrder(OrderDto orderRequest);
}
