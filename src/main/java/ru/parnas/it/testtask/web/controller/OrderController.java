package ru.parnas.it.testtask.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.parnas.it.testtask.dictionary.OrderStatus;
import ru.parnas.it.testtask.domain.service.OrderService;
import ru.parnas.it.testtask.mapper.OrderMapper;
import ru.parnas.it.testtask.web.dto.CreateOrderRequest;
import ru.parnas.it.testtask.web.dto.OrderResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Заказы", description = "API для управления заказами и их позициями")
public class OrderController {

    private final OrderService service;
    private final OrderMapper mapper;

    @PostMapping
    @Operation(summary = "Создать новый заказ", description = "Принимает имя клиента и список позиций. Возвращает созданный заказ с ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные (ошибка валидации)")
    })
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse createdOrder = mapper.toResponse(service.createOrder(mapper.toOrder(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping
    @Operation(summary = "Получить список заказов", description = "Возвращает пагинированный список заказов. Доступна фильтрация по статусу.")
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @Parameter(description = "Фильтр по статусу заказа") @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20, sort = "orderDate") Pageable pageable) {

        Page<OrderResponse> orders = service.getOrders(status, pageable).map(mapper::toResponse);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по ID", description = "Возвращает полную информацию о заказе вместе со всеми его позициями (Items).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ найден"),
            @ApiResponse(responseCode = "404", description = "Заказ с указанным ID не найден")
    })
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "Уникальный UUID заказа", required = true) @PathVariable UUID id
    ) {
        OrderResponse order = mapper.toResponse(service.getOrderById(id));
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Обновить статус заказа", description = "Прямое изменение статуса заказа по его идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public ResponseEntity<Void> updateStatus(
            @Parameter(description = "UUID заказа", required = true) @PathVariable UUID id,
            @Parameter(description = "Новый статус заказа", required = true) @RequestParam OrderStatus status) {

        service.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
