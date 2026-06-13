package ru.parnas.it.testtask.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private Long id;

    private String productName;

    private Integer quantity;

    private BigDecimal price;

    private Order order;
}