package ru.parnas.it.testtask.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.parnas.it.testtask.database.model.OrderEntity;
import ru.parnas.it.testtask.database.model.OrderItemEntity;
import ru.parnas.it.testtask.domain.model.Order;
import ru.parnas.it.testtask.domain.model.OrderItem;
import ru.parnas.it.testtask.web.dto.CreateOrderItemRequest;
import ru.parnas.it.testtask.web.dto.CreateOrderRequest;
import ru.parnas.it.testtask.web.dto.OrderItemResponse;
import ru.parnas.it.testtask.web.dto.OrderResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    Order toOrder(OrderEntity entity);

    OrderItem toOrderItem(OrderItemEntity entity);

    OrderEntity toOrderEntity(Order order);

    OrderItemEntity toOrderItemEntity(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    Order toOrder(CreateOrderRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItem(CreateOrderItemRequest requestItem);

    OrderResponse toResponse(Order order);

    OrderItemResponse toResponseItem(OrderItem item);
}
