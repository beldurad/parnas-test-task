package ru.parnas.it.testtask.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.parnas.it.testtask.database.model.OrderEntity;
import ru.parnas.it.testtask.database.repository.OrderRepository;
import ru.parnas.it.testtask.dictionary.OrderStatus;
import ru.parnas.it.testtask.domain.model.Order;
import ru.parnas.it.testtask.exception.OrderNotFoundException;
import ru.parnas.it.testtask.mapper.OrderMapper;
import ru.parnas.it.testtask.web.dto.OrderResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public Order createOrder(Order order) {

        order.setId(UUID.randomUUID());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);

        return orderMapper.toOrder(
                orderRepository.save(orderMapper.toOrderEntity(order))
        );
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
