package ru.parnas.it.testtask.web.dto;

import ru.parnas.it.testtask.dictionary.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerName,
        LocalDateTime orderDate,
        OrderStatus status,
        List<OrderItemResponse> items
) {}
