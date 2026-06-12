package ru.parnas.it.testtask.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "Имя клиента не должно быть пустым")
        @Size(max = 255, message = "Имя клиента не должно превышать 255 символов")
        String customerName,

        @NotEmpty(message = "Заказ должен содержать хотя бы одну позицию")
        @Valid
        List<CreateOrderItemRequest> items
) {}
