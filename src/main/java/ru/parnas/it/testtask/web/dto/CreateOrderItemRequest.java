package ru.parnas.it.testtask.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Модель данных для создания позиции внутри заказа (данные запроса)")
public record CreateOrderItemRequest(
        @NotBlank(message = "Название товара не должно быть пустым")
        @Schema(description = "Наименование товара", example = "Механическая клавиатура", requiredMode = Schema.RequiredMode.REQUIRED)
        String productName,

        @NotNull(message = "Количество должно быть указано")
        @Min(value = 1, message = "Количество товара должно быть не менее 1")
        @Schema(description = "Количество единиц товара", example = "1", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer quantity,

        @NotNull(message = "Цена должна быть указана")
        @Positive(message = "Цена должна быть больше нуля")
        @Schema(description = "Стоимость одной единицы товара", example = "8990.50", minimum = "0.01", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal price
) {}
