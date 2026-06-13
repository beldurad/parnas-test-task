package ru.parnas.it.testtask;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ru.parnas.it.testtask.database.model.OrderEntity;
import ru.parnas.it.testtask.database.repository.OrderRepository;
import ru.parnas.it.testtask.dictionary.OrderStatus;
import ru.parnas.it.testtask.domain.model.Order;
import ru.parnas.it.testtask.domain.service.OrderService;
import ru.parnas.it.testtask.mapper.OrderMapper;
import ru.parnas.it.testtask.web.dto.CreateOrderRequest;
import ru.parnas.it.testtask.web.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_ShouldSaveToDbAndSendNotification() {
        // GIVEN (Подготовка данных и определение поведения моков)
        CreateOrderRequest request = new CreateOrderRequest("Иван Иванов", List.of());

        Order domainOrder = new Order();
        domainOrder.setCustomerName("Иван Иванов");
        domainOrder.setItems(List.of());

        OrderEntity entityToSave = new OrderEntity();
        OrderEntity savedEntity = new OrderEntity();
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setCustomerName("Иван Иванов");

        Order savedDomain = new Order();
        savedDomain.setId(savedEntity.getId());
        savedDomain.setCustomerName("Иван Иванов");

        OrderResponse expectedResponse = new OrderResponse(savedEntity.getId(), "Иван Иванов", null, OrderStatus.CREATED, List.of());

        when(orderMapper.toOrderEntity(any(Order.class))).thenReturn(entityToSave);
        when(orderRepository.save(entityToSave)).thenReturn(savedEntity);
        when(orderMapper.toOrder(savedEntity)).thenReturn(savedDomain);
        when(orderMapper.toResponse(savedDomain)).thenReturn(expectedResponse);

        OrderResponse actualResponse = orderMapper.toResponse(orderService.createOrder(domainOrder));

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(savedEntity.getId());
        assertThat(actualResponse.customerName()).isEqualTo("Иван Иванов");

        verify(orderRepository, times(1)).save(entityToSave);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("order.exchange"),
                eq("order.created.routing-key"),
                any(Object.class)
        );
    }
}
