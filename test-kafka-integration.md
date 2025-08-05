# Тестирование интеграции User Service ↔ Cart Service через Kafka

## 🎯 Что мы реализовали:

1. **User Service** → создает пользователя → отправляет `UserCreatedEvent`
2. **Cart Service** → получает событие → создает корзину для пользователя

## 📋 Шаги для тестирования:

### 1. Запуск сервисов:
```bash
# Запустить Kafka
docker-compose up -d kafka

# Запустить Eureka Server
# Запустить User Service
# Запустить Cart Service
```

### 2. Создание пользователя:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "phone": "+1234567890",
    "roles": ["USER"]
  }'
```

### 3. Проверка создания корзины:
```bash
# Проверить корзину пользователя
curl -X GET http://localhost:8080/api/carts \
  -H "X-User-Id: 1"
```

### 4. Проверка логов:
```bash
# В логах User Service должно быть:
"Sending UserCreatedEvent to topic user-created: UserCreatedEvent(userId=1, username=testuser, email=test@example.com)"

# В логах Cart Service должно быть:
"Received UserCreatedEvent: UserCreatedEvent(userId=1, username=testuser, email=test@example.com)"
"Created cart for user: 1"
```

## 🔍 Проверка в Redis:
```bash
# Подключиться к Redis
redis-cli

# Проверить созданную корзину
KEYS *cart*
GET carts:1
```

## ⚠️ Возможные проблемы:

1. **Kafka не запущен** → проверить docker-compose
2. **Топик не создан** → Kafka создаст автоматически
3. **Сериализация** → проверить типы данных в событиях
4. **Сетевые проблемы** → проверить порты и доступность

## 🎉 Ожидаемый результат:

После создания пользователя в User Service автоматически создается корзина в Cart Service! 