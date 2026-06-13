package ru.parnas.it.testtask.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Модель данных для оформления нового заказа (данные запроса)")
public record CreateOrderRequest(
        @NotBlank(message = "Имя клиента не должно быть пустым")
        @Size(max = 255, message = "Имя клиента не должно превышать 255 символов")
        @Schema(description = "Имя и фамилия заказчика", example = "Петр Петров", requiredMode = Schema.RequiredMode.REQUIRED)
        String customerName,

        @NotEmpty(message = "Заказ должен содержать хотя бы одну позицию")
        @Valid
        @Schema(description = "Массив заказываемых товаров (минимум 1 позиция)", requiredMode = Schema.RequiredMode.REQUIRED)
        List<CreateOrderItemRequest> items
) {}
