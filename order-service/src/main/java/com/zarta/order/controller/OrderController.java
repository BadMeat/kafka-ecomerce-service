package com.zarta.order.controller;

import com.zarta.order.dto.OrderDto;
import com.zarta.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/send-order")
    public ResponseEntity<Object> sendOrder(@RequestBody OrderDto orderDto) {
        orderService.sendOrder(orderDto);
        return ResponseEntity.ok("Success Send");
    }

}
