package ru.parnas.it.testtask.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.parnas.it.testtask.database.model.OrderEntity;
import ru.parnas.it.testtask.database.repository.OrderRepository;
import ru.parnas.it.testtask.dictionary.OrderStatus;
import ru.parnas.it.testtask.domain.model.Order;
import ru.parnas.it.testtask.exception.OrderNotFoundException;
import ru.parnas.it.testtask.mapper.OrderMapper;
import ru.parnas.it.testtask.mq.RabbitConfig;
import ru.parnas.it.testtask.mq.dto.OrderCreatedEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Order createOrder(Order order) {

        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);

        BigDecimal totalAmount = order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order savedOrder = orderMapper.toOrder(
                orderRepository.save(orderMapper.toOrderEntity(order))
        );

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerName(),
                totalAmount
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ORDER_CREATED_ROUTING_KEY,
                event
        );
        return savedOrder;
    }

    @Transactional
    public Page<Order> getOrders(OrderStatus status, Pageable pageable) {

        Page<OrderEntity> entityPage = (status != null)
                ? orderRepository.findByStatus(status, pageable)
                : orderRepository.findAll(pageable);

        return entityPage
                .map(orderMapper::toOrder);
    }

    @Transactional
    public Order getOrderById(UUID id) {
        OrderEntity entity = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new OrderNotFoundException("Заказ с ID " + id + " не найден"));

        return orderMapper.toOrder(entity);
    }

    @Transactional
    public void updateOrderStatus(UUID id, OrderStatus status) {
        int updatedRows = orderRepository.updateStatus(id, status);

        if (updatedRows == 0) {
            throw new OrderNotFoundException("Заказ с ID " + id + " не найден для обновления статуса");
        }
    }
}
