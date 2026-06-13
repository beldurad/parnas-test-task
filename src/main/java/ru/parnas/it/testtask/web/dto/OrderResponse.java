package ru.parnas.it.testtask.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.parnas.it.testtask.dictionary.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Информация о заказе (данные ответа)")
public record OrderResponse(
        @Schema(description = "Уникальный идентификатор заказа", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Имя покупателя", example = "Иван Иванов")
        String customerName,

        @Schema(description = "Дата и время создания заказа", example = "2026-06-13T10:15:30")
        LocalDateTime orderDate,

        @Schema(description = "Текущий статус выполнения заказа")
        OrderStatus status,

        @Schema(description = "Список позиций, включенных в заказ")
        List<OrderItemResponse> items
) {}
