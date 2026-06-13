package ru.parnas.it.testtask.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Информация о конкретной позиции внутри заказа (данные ответа)")
public record OrderItemResponse(
        @Schema(description = "Идентификатор позиции товара", example = "42")
        Long id,

        @Schema(description = "Название приобретаемого товара/услуги", example = "Беспроводные наушники")
        String productName,

        @Schema(description = "Количество выкупленных единиц товара", example = "2")
        Integer quantity,

        @Schema(description = "Цена за одну единицу товара", example = "4500.00")
        BigDecimal price
) {}