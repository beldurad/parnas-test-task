package ru.parnas.it.testtask.web.controller;

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
public class OrderController {

    private final OrderService service;
    private final OrderMapper mapper;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse createdOrder = mapper.toResponse(service.createOrder(mapper.toOrder(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20, sort = "orderDate") Pageable pageable) {

        Page<OrderResponse> orders = service.getOrders(status, pageable).map(mapper::toResponse);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        OrderResponse order = mapper.toResponse(service.getOrderById(id));
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status) {

        service.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
