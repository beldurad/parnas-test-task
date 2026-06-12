package ru.parnas.it.testtask.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderItemRequest(
        @NotBlank(message = "Название товара не должно быть пустым")
        String productName,

        @NotNull(message = "Количество должно быть указано")
        @Min(value = 1, message = "Количество товара должно быть не менее 1")
        Integer quantity,

        @NotNull(message = "Цена должна быть указана")
        @Positive(message = "Цена должна быть больше нуля")
        BigDecimal price
) {}
