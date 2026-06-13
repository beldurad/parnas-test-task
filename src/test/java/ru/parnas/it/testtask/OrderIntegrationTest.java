package ru.parnas.it.testtask;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.parnas.it.testtask.database.model.OrderEntity;
import ru.parnas.it.testtask.database.repository.OrderRepository;
import ru.parnas.it.testtask.dictionary.OrderStatus;
import ru.parnas.it.testtask.web.dto.CreateOrderItemRequest;
import ru.parnas.it.testtask.web.dto.CreateOrderRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbit::getAdminPassword);
    }

    @Test
    void shouldCreateOrder_SaveToDb_AndProcessAsyncMessage() throws Exception {
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest("Клавиатура", 1, new BigDecimal("5000.00"));
        CreateOrderRequest orderRequest = new CreateOrderRequest("Алексей", List.of(itemRequest));

        String responseJson = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID createdOrderId = UUID.fromString(objectMapper.readTree(responseJson).get("id").asText());

        OrderEntity savedEntity = orderRepository.findByIdWithItems(createdOrderId).orElse(null);
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getCustomerName()).isEqualTo("Алексей");
        assertThat(savedEntity.getItems()).hasSize(1);

        assertThat(savedEntity.getStatus()).isEqualTo(OrderStatus.CREATED);

        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    OrderEntity processedOrder = orderRepository.findById(createdOrderId).orElseThrow();
                    assertThat(processedOrder.getStatus()).isEqualTo(OrderStatus.PROCESSING);
                });
    }
}
