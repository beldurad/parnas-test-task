package ru.parnas.it.testtask.mq.listener;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.parnas.it.testtask.database.repository.OrderRepository;
import ru.parnas.it.testtask.dictionary.OrderStatus;
import ru.parnas.it.testtask.mq.RabbitConfig;
import ru.parnas.it.testtask.mq.dto.OrderCreatedEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = RabbitConfig.ORDER_CREATED_QUEUE)
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Получено сообщение из очереди '{}'. Обработка заказа ID: {}, Клиент: {}, Сумма: {}",
                RabbitConfig.ORDER_CREATED_QUEUE, event.orderId(), event.customerName(), event.totalAmount());

        try {
            int updatedRows = orderRepository.updateStatus(event.orderId(), OrderStatus.PROCESSING);

            if (updatedRows > 0) {
                log.info("Статус заказа ID: {} успешно изменен на PROCESSING", event.orderId());
            } else {
                log.warn("Заказ с ID: {} не найден в БД для перевода в PROCESSING", event.orderId());
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке события заказа ID: {}", event.orderId(), e);
            throw e;
        }
    }
}
