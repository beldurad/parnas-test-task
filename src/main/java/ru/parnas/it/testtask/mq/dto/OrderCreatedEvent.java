package ru.parnas.it.testtask.mq.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        String customerName,
        BigDecimal totalAmount
) {}
