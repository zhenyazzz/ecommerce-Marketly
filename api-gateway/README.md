# 🌐 API Gateway

Единая точка входа для всех микросервисов в архитектуре Marketly.

## 🎯 Назначение

API Gateway обеспечивает:
- **Маршрутизацию** запросов к соответствующим сервисам
- **Балансировку нагрузки** между экземплярами сервисов
- **Аутентификацию и авторизацию**
- **Rate limiting** и защиту от DDoS
- **Мониторинг** и логирование запросов

## 🚀 Запуск

### Локальный запуск:
```bash
mvn spring-boot:run
```

### Запуск через Docker Compose:
```bash
# Сборка и запуск
docker-compose up api-gateway

# Или запуск всех сервисов
docker-compose up -d
```

## 🔗 Доступ

- **API Gateway**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Gateway Info**: http://localhost:8080/actuator/gateway

## 🛣️ Маршруты

### **Eureka Service:**
- **URL**: http://localhost:8080/eureka/**
- **Сервис**: eureka-service

### **Order Service:**
- **URL**: http://localhost:8080/api/orders/**
- **Сервис**: order-service

### **User Service:**
- **URL**: http://localhost:8080/api/users/**
- **Сервис**: user-service

### **Product Service:**
- **URL**: http://localhost:8080/api/products/**
- **Сервис**: product-service

### **Auth Service:**
- **URL**: http://localhost:8080/api/auth/**
- **Сервис**: auth-service

## ⚙️ Конфигурация

### Локальная разработка (application.yml):
- Port: 8080
- Eureka: http://localhost:8761/eureka/
- Config Server: http://localhost:8888

### Docker окружение (application-docker.yml):
- Port: 8080
- Eureka: http://eureka-service:8761/eureka/
- Config Server: http://config-service:8888
- Service Discovery: включено
- Load Balancing: включено

## 🔄 Service Discovery

API Gateway автоматически обнаруживает сервисы через Eureka:

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
```

## ⚖️ Load Balancing

Используется Spring Cloud LoadBalancer:

```yaml
routes:
  - id: order-service
    uri: lb://order-service  # lb:// означает load balancing
    predicates:
      - Path=/api/orders/**
```

## 🔒 Безопасность

### **CORS настройки:**
```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
```

### **Rate Limiting:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

## 📊 Мониторинг

### **Actuator endpoints:**
- `/actuator/health` - статус сервиса
- `/actuator/gateway/routes` - список маршрутов
- `/actuator/gateway/globalfilters` - глобальные фильтры

### **Метрики:**
- Количество запросов
- Время ответа
- Ошибки
- Статус сервисов

## 🔧 Отладка

### **Проверка маршрутов:**
```bash
curl http://localhost:8080/actuator/gateway/routes
```

### **Проверка health:**
```bash
curl http://localhost:8080/actuator/health
```

### **Логи:**
```bash
docker-compose logs api-gateway
```

## 🎯 Преимущества

1. **Единая точка входа** - все запросы через один URL
2. **Автоматическое обнаружение** - через Service Discovery
3. **Балансировка нагрузки** - распределение запросов
4. **Безопасность** - централизованная аутентификация
5. **Мониторинг** - единая точка для метрик
6. **Масштабируемость** - легко добавлять новые сервисы 