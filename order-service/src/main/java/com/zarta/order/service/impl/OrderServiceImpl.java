package com.zarta.order.service.impl;

import com.zarta.domain.dto.ItemAvro;
import com.zarta.domain.dto.OrderAvro;
import com.zarta.order.dto.ItemDto;
import com.zarta.order.dto.OrderDto;
import com.zarta.order.mapper.OrderMapper;
import com.zarta.order.model.Order;
import com.zarta.order.repository.OrderRepository;
import com.zarta.order.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderServiceImpl implements OrderService {

    @Value("${topic.name}")
    private String topicName;

    private final KafkaTemplate<String, OrderAvro> template;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(KafkaTemplate<String, OrderAvro> template, OrderRepository orderRepository) {
        this.template = template;
        this.orderRepository = orderRepository;
    }

    @Override
    public void sendOrder(OrderDto orderRequest) {

        Order order = OrderMapper.INSTANCE.toEntity(orderRequest);
        order.getItems().forEach(e -> {
            e.setOrder(order);
        });
        Order savedOrder = orderRepository.save(order);

        OrderDto orderDto = OrderMapper.INSTANCE.toDto(savedOrder);
        OrderAvro avro = dtoToAvro(orderDto);
        CompletableFuture<SendResult<String, OrderAvro>> future = template
                .send(topicName, UUID.randomUUID().toString(), avro);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Sent message=[" + avro +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message=[" +
                        avro + "] due to : " + ex.getMessage());
            }
        });
    }

    private OrderAvro dtoToAvro(OrderDto orderDto) {
        OrderAvro avro = new OrderAvro();
        avro.setId(orderDto.getId().intValue());
        avro.setEmail(orderDto.getEmail());
        avro.setUserName(orderDto.getUserName());
        avro.setItems(listToAvro(orderDto.getItems()));
        return avro;
    }

    private List<ItemAvro> listToAvro(List<ItemDto> itemDtos) {
        List<ItemAvro> itemAvroList = new ArrayList<>();
        itemDtos.forEach(e -> {
            itemAvroList.add(itemDtoToAvro(e));
        });
        return itemAvroList;
    }

    private ItemAvro itemDtoToAvro(ItemDto itemDto) {
        ItemAvro itemAvro = new ItemAvro();
        itemAvro.setKode(itemDto.getKode());
        itemAvro.setQty(itemDto.getQty());
        itemAvro.setPrice(itemDto.getPrice());
        return itemAvro;
    }
}
