package ru.parnas.it.testtask.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.parnas.it.testtask.dictionary.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private UUID id;

    private String customerName;

    private LocalDateTime orderDate;

    private OrderStatus status;

    private List<OrderItem> items;
}