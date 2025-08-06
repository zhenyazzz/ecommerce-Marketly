# Notification Service Architecture

## Обзор

Notification Service - это микросервис, отвечающий за обработку и отправку уведомлений в системе Marketly. Сервис принимает события от других сервисов через Kafka и отправляет уведомления через различные каналы (email, SMS, push).

## Архитектурные принципы

### 1. Event-Driven Architecture
- Сервис получает события через Kafka топики
- Асинхронная обработка уведомлений
- Отказоустойчивость через retry механизм

### 2. Microservices Best Practices
- Единственная ответственность (Single Responsibility)
- Независимое развертывание
- Собственная база данных
- API Gateway интеграция

### 3. Resilience Patterns
- Circuit Breaker для внешних сервисов
- Retry механизм для неудачных отправок
- Dead Letter Queue для необработанных событий
- Graceful degradation

## Компоненты системы

### 1. Kafka Consumers
```
OrderEventConsumer -> OrderEvent -> NotificationService
UserEventConsumer -> UserEvent -> NotificationService
```

**Ответственности:**
- Слушать Kafka топики
- Десериализация событий
- Обработка ошибок
- Логирование

### 2. Notification Engine
```
NotificationService
├── createNotification()
├── sendNotification()
├── processOrderEvent()
└── processUserEvent()
```

**Ответственности:**
- Создание уведомлений
- Маршрутизация по каналам
- Обработка шаблонов
- Управление статусами

### 3. Channel Providers
```
NotificationChannelProvider (Interface)
├── EmailNotificationProvider
├── SmsNotificationProvider
└── PushNotificationProvider
```

**Ответственности:**
- Отправка через конкретный канал
- Обработка ошибок канала
- Health checks
- Rate limiting

### 4. Template Engine
```
TemplateService
├── processTemplate()
└── getDefaultSubject()
```

**Ответственности:**
- Обработка Thymeleaf шаблонов
- Подстановка переменных
- Валидация шаблонов

### 5. Data Layer
```
NotificationRepository
├── findByUserIdAndStatus()
├── findPendingNotificationsForRetry()
└── findOldNotifications()
```

**Ответственности:**
- CRUD операции
- Специфичные запросы
- Индексация

## Поток данных

### 1. Создание заказа
```
Order Service -> Kafka (order-events) -> Notification Service -> Email Provider -> User
```

### 2. Регистрация пользователя
```
User Service -> Kafka (user-events) -> Notification Service -> Email Provider -> User
```

### 3. Изменение статуса заказа
```
Order Service -> Kafka (order-events) -> Notification Service -> Email Provider -> User
```

## База данных

### Схема таблиц

```sql
-- Основная таблица уведомлений
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(500),
    content TEXT,
    template_name VARCHAR(100),
    user_id BIGINT,
    retry_count INTEGER DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP
);

-- Метаданные уведомлений
CREATE TABLE notification_metadata (
    notification_id UUID NOT NULL,
    metadata_key VARCHAR(100) NOT NULL,
    metadata_value TEXT,
    PRIMARY KEY (notification_id, metadata_key),
    FOREIGN KEY (notification_id) REFERENCES notifications(id)
);
```

### Индексы
- `idx_notifications_user_id` - поиск по пользователю
- `idx_notifications_status` - поиск по статусу
- `idx_notifications_type` - поиск по типу
- `idx_notifications_created_at` - временные запросы

## Конфигурация

### Основные настройки

```yaml
spring:
  application:
    name: notification-service
  
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      auto-offset-reset: earliest
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

notification:
  retry:
    max-attempts: 3
    delay-minutes: 5
```

### Переменные окружения

```bash
# Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/notification_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

## Мониторинг и Observability

### Health Checks
- `/actuator/health` - общее состояние
- `/actuator/health/db` - база данных
- `/actuator/health/kafka` - Kafka

### Метрики
- Количество отправленных уведомлений
- Время обработки событий
- Ошибки отправки
- Размер очереди

### Логирование
- Структурированные логи (JSON)
- Уровни: DEBUG, INFO, WARN, ERROR
- Корреляция запросов

## Безопасность

### Аутентификация
- JWT токены для API
- Service-to-service аутентификация

### Авторизация
- RBAC для администраторов
- Rate limiting для API

### Шифрование
- TLS для внешних соединений
- Шифрование чувствительных данных

## Масштабирование

### Горизонтальное масштабирование
- Множественные инстансы
- Load balancing через API Gateway
- Партиционирование Kafka топиков

### Вертикальное масштабирование
- Увеличение ресурсов JVM
- Оптимизация пулов соединений
- Настройка кэширования

## Отказоустойчивость

### Retry механизм
- Экспоненциальная задержка
- Максимальное количество попыток
- Dead Letter Queue

### Circuit Breaker
- Защита от внешних сервисов
- Graceful degradation
- Автоматическое восстановление

### Backup стратегии
- Резервное копирование БД
- Репликация Kafka
- Географическое распределение

## Развертывание

### Docker
```bash
docker build -t notification-service .
docker run -p 8084:8084 notification-service
```

### Kubernetes
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

### CI/CD Pipeline
1. **Build** - Maven сборка
2. **Test** - Unit и интеграционные тесты
3. **Security Scan** - Проверка уязвимостей
4. **Deploy** - Развертывание в staging/prod

## Тестирование

### Unit Tests
- Сервисы
- Репозитории
- Контроллеры

### Integration Tests
- Kafka интеграция
- Email отправка
- База данных

### End-to-End Tests
- Полный поток уведомлений
- Обработка ошибок
- Performance тесты

## Будущие улучшения

### Планируемые функции
1. **Push Notifications** - интеграция с FCM/APNS
2. **SMS Gateway** - интеграция с Twilio
3. **Webhook Support** - отправка webhook'ов
4. **Template Editor** - веб-интерфейс для шаблонов
5. **Analytics Dashboard** - метрики и аналитика

### Технические улучшения
1. **GraphQL API** - гибкие запросы
2. **Event Sourcing** - полная история событий
3. **CQRS** - разделение команд и запросов
4. **Saga Pattern** - распределенные транзакции
5. **API Versioning** - поддержка версий API 