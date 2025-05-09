package com.zarta.order.mapper;

import com.zarta.order.dto.OrderDto;
import com.zarta.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Order toEntity(OrderDto orderDto);
    OrderDto toDto(Order order);
}
