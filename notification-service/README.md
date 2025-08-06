# Notification Service

Сервис уведомлений для платформы Marketly. Обрабатывает события от других сервисов и отправляет уведомления через различные каналы (email, SMS, push).

## Архитектура

### Основные компоненты:

1. **Kafka Consumers** - слушают события от других сервисов
2. **Notification Engine** - обрабатывает и маршрутизирует уведомления
3. **Channel Providers** - отправляют уведомления через разные каналы
4. **Template Engine** - генерирует контент уведомлений
5. **Database** - сохраняет историю уведомлений

### Поддерживаемые типы уведомлений:

- **Order Events**: создание заказа, изменение статуса, отмена
- **User Events**: регистрация, верификация email, сброс пароля
- **System Events**: технические уведомления

### Каналы доставки:

- **Email** - через SMTP (Gmail, SendGrid, etc.)
- **SMS** - через провайдеров (Twilio, AWS SNS)
- **Push Notifications** - для мобильных приложений
- **In-App** - внутренние уведомления

## Запуск

### Предварительные требования:

1. Java 21
2. Maven 3.8+
3. Docker & Docker Compose
4. PostgreSQL
5. Redis
6. Kafka

### Локальный запуск:

```bash
# Запуск инфраструктуры
docker-compose up -d

# Сборка и запуск сервиса
mvn clean install
mvn spring-boot:run
```

### Переменные окружения:

```bash
# Email настройки
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# База данных
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/notification_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

## API Endpoints

### Отправка уведомления

```http
POST /api/v1/notifications/send
Content-Type: application/json

{
  "type": "ORDER_CREATED",
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "subject": "Order Confirmation",
  "templateName": "order-created",
  "templateData": {
    "orderId": "123e4567-e89b-12d3-a456-426614174000",
    "orderNumber": "#12345",
    "userName": "John Doe",
    "totalAmount": "99.99"
  },
  "userId": 123
}
```

### Health Check

```http
GET /api/v1/notifications/health
```

## Kafka Topics

### Входящие события:

- `order-events` - события заказов
- `user-events` - события пользователей

### Пример события заказа:

```json
{
  "orderId": "123e4567-e89b-12d3-a456-426614174000",
  "userId": 123,
  "userEmail": "user@example.com",
  "userName": "John Doe",
  "orderStatus": "CREATED",
  "totalAmount": 99.99,
  "orderDate": "2024-01-15T10:30:00Z",
  "orderNumber": "#12345"
}
```

### Пример события пользователя:

```json
{
  "userId": 123,
  "userEmail": "user@example.com",
  "userName": "John Doe",
  "eventType": "USER_REGISTERED",
  "eventDate": "2024-01-15T10:30:00Z",
  "verificationToken": "abc123"
}
```

## Шаблоны

Шаблоны уведомлений находятся в `src/main/resources/templates/`:

- `order-created.html` - уведомление о создании заказа
- `order-status-updated.html` - изменение статуса заказа
- `user-registered.html` - приветствие нового пользователя
- `password-reset.html` - сброс пароля

## Мониторинг

### Health Checks:

- `/actuator/health` - общее состояние сервиса
- `/actuator/health/db` - состояние базы данных
- `/actuator/health/kafka` - состояние Kafka

### Метрики:

- `/actuator/metrics` - Prometheus метрики
- Количество отправленных уведомлений
- Время обработки событий
- Ошибки отправки

## Развертывание

### Docker:

```bash
# Сборка образа
docker build -t notification-service .

# Запуск контейнера
docker run -p 8084:8084 notification-service
```

### Kubernetes:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notification-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: notification-service
  template:
    metadata:
      labels:
        app: notification-service
    spec:
      containers:
      - name: notification-service
        image: notification-service:latest
        ports:
        - containerPort: 8084
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

## Логирование

Логи доступны через:

```bash
# Логи приложения
tail -f logs/notification-service.log

# Логи Kafka
docker logs kafka-broker

# Логи базы данных
docker logs notification-db
```

## Troubleshooting

### Частые проблемы:

1. **Email не отправляется** - проверьте настройки SMTP
2. **Kafka события не обрабатываются** - проверьте подключение к Kafka
3. **Шаблоны не загружаются** - проверьте путь к templates

### Отладка:

```bash
# Проверка состояния сервисов
docker ps

# Проверка логов
docker logs notification-service

# Проверка Kafka топиков
docker exec -it kafka-broker kafka-topics --list --bootstrap-server localhost:9092
``` 