# TestTask — Управление заказами

REST-сервис для управления заказами на Spring Boot 4.1.0 (Java 17).  
PostgreSQL — хранение данных, RabbitMQ — асинхронная обработка событий.

---

## Запуск (Docker Compose)

```bash
# 1. Собрать приложение
.\mvnw.cmd clean package -DskipTests

# 2. Запустить PostgreSQL, RabbitMQ и само приложение
docker compose up -d

# 3. Проверить логи
docker compose logs -f app

# 4. Остановить
docker compose down
```

Сервис будет доступен на `http://localhost:8080`.  
Swagger UI: `http://localhost:8080/swagger-ui.html`  
RabbitMQ Management: `http://localhost:15672` (логин: `rabbit_user`, пароль: `rabbit_pass`)

---

## Примеры curl-запросов

### Создать заказ

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Петр Петров",
    "items": [
      {"productName": "Механическая клавиатура", "quantity": 1, "price": 8990.50},
      {"productName": "Коврик для мыши", "quantity": 2, "price": 1500.00}
    ]
  }'
```

Ответ: `201 Created`

```json
{
  "id": "3a1b2c3d-...",
  "customerName": "Петр Петров",
  "orderDate": "2026-06-13T12:00:00",
  "status": "CREATED",
  "items": [
    {"id": 1, "productName": "Механическая клавиатура", "quantity": 1, "price": 8990.50},
    {"id": 2, "productName": "Коврик для мыши", "quantity": 2, "price": 1500.00}
  ]
}
```

### Получить список заказов

```bash
# Все заказы
curl http://localhost:8080/api/orders

# Фильтр по статусу + пагинация
curl "http://localhost:8080/api/orders?status=CREATED&page=0&size=10&sort=orderDate,desc"
```

### Получить заказ по ID

```bash
curl http://localhost:8080/api/orders/3a1b2c3d-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

### Обновить статус заказа

```bash
curl -X PUT "http://localhost:8080/api/orders/3a1b2c3d-xxxx-xxxx-xxxx-xxxxxxxxxxxx/status?status=COMPLETED"
```

---

## Архитектура

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────┐
│  Клиент      │────▶│  OrderController│────▶│ OrderService│
│  (curl/UI)   │     │  /api/orders    │     │  (бизнес-   │
└─────────────┘     └──────────────────┘     │   логика)   │
                      │  ▲                   │     │       │
                      ▼  │                   ▼     ▼       │
                 ┌──────────┐          ┌──────────┐        │
                 │  Request │          │ OrderRepo│◀───────┘
                 │   DTO    │          │ (JPA)    │
                 └──────────┘          └────┬─────┘
                                            │
                                    ┌───────▼───────┐
                                    │   PostgreSQL  │
                                    └───────────────┘
┌──────────────────────┐
│  OrderService         │
│  ─────────────────    │
│  После сохранения     │
│  отправляет событие   │
│  в RabbitMQ           │────▶  RabbitMQ  ────▶  OrderEventListener
└──────────────────────┘       (order.exchange)   (обновляет статус
                                                   на PROCESSING)
```

**Слои:**

| Слой | Компоненты | Назначение |
|------|-----------|------------|
| **Web** | `OrderController`, `CreateOrderRequest`, `OrderResponse` | REST-эндпоинты, валидация, DTO |
| **Domain** | `OrderService`, `Order`, `OrderItem` | Бизнес-логика, доменные модели |
| **Database** | `OrderEntity`, `OrderItemEntity`, `OrderRepository` | JPA-сущности, репозиторий Spring Data |
| **Messaging** | `RabbitConfig`, `OrderCreatedEvent`, `OrderEventListener` | AMQP-конфигурация, публикация и подписка на события |
| **Mapper** | `OrderMapper` (MapStruct) | Преобразование Entity ↔ Domain ↔ DTO |
| **Exception** | `GlobalExceptionHandler`, `OrderNotFoundException` | Централизованная обработка ошибок |

**Поток создания заказа:**

1. Клиент отправляет `POST /api/orders` с JSON-телом
2. `OrderController` принимает запрос, валидирует, маппит в доменную модель
3. `OrderService` сохраняет заказ в PostgreSQL (статус `CREATED`)
4. После сохранения в RabbitMQ публикуется `OrderCreatedEvent`
5. `OrderEventListener` получает событие и меняет статус заказа на `PROCESSING`
