package ru.parnas.it.testtask.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.parnas.it.testtask.database.model.OrderEntity;
import ru.parnas.it.testtask.database.model.OrderItemEntity;
import ru.parnas.it.testtask.domain.model.Order;
import ru.parnas.it.testtask.domain.model.OrderItem;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    Order toOrder(OrderEntity entity);

    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItem(OrderItemEntity entity);
}
