package ru.parnas.it.testtask.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.parnas.it.testtask.database.repository.OrderRepository;
import ru.parnas.it.testtask.dictionary.OrderStatus;
import ru.parnas.it.testtask.domain.model.Order;
import ru.parnas.it.testtask.exception.OrderNotFoundException;
import ru.parnas.it.testtask.mapper.OrderMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public Order getOrderById(UUID id) {
        return orderMapper.toOrder(
                orderRepository.findByIdWithItems(id)
                        .orElseThrow(() -> new OrderNotFoundException("Заказ с ID " + id + " не найден"))
        );
    }

    @Transactional
    public void changeStatus(UUID id, OrderStatus newStatus) {
        int updatedRows = orderRepository.updateStatus(id, newStatus);
        if (updatedRows == 0) {
            throw new OrderNotFoundException("Заказ не найден для обновления статуса");
        }
    }
}
