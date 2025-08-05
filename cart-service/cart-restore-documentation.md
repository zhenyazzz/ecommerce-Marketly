# Восстановление корзины и архитектура PostgreSQL + Redis

## 🏗️ Новая архитектура: PostgreSQL + Redis

### **Схема работы:**
```
PostgreSQL (основное хранение) ←→ Redis (кеш для быстрого доступа)
```

### **Преимущества:**
- **PostgreSQL**: Надежное хранение, сложные запросы, аналитика
- **Redis**: Быстрый доступ к активным корзинам, снижение нагрузки на БД

---

## 🔄 Восстановление корзины

### **Когда используется:**
1. Пользователь хочет восстановить последнюю корзину
2. Корзина была очищена после заказа
3. Пользователь хочет повторить покупку

### **API Endpoint:**
```http
POST /api/carts/restore
Header: X-User-Id: 1
```

### **Логика восстановления:**
```java
// 1. Найти архивные корзины пользователя
List<Cart> archivedCarts = cartRepository.findByUserIdAndStatusIn(
    userId, List.of(CartStatus.ARCHIVED, CartStatus.CONVERTED_TO_ORDER));

// 2. Взять самую последнюю
Cart lastCart = archivedCarts.get(0);

// 3. Создать новую корзину с товарами из последней
Cart newCart = Cart.builder()
    .userId(userId)
    .cartItems(new ArrayList<>(lastCart.getCartItems()))
    .total(lastCart.getTotal())
    .status(CartStatus.ACTIVE)
    .build();

// 4. Сохранить в БД и обновить кеш
```

---

## 📊 Примеры восстановления

### **Сценарий 1: Восстановление после заказа**
```
День 1: Пользователь добавил iPhone → ACTIVE корзина
День 2: Пользователь купил iPhone → ARCHIVED корзина + новая ACTIVE (пустая)
День 3: Пользователь хочет купить еще iPhone → Восстанавливает корзину
Результат: ACTIVE корзина с iPhone
```

### **Сценарий 2: Восстановление брошенной корзины**
```
День 1: Пользователь добавил товары → ACTIVE корзина
День 2: Пользователь не купил → ABANDONED корзина
День 3: Пользователь хочет купить → Восстанавливает корзину
Результат: ACTIVE корзина с товарами
```

---

## 🚀 Кеширование в Redis

### **Структура кеша:**
```redis
cart:1 → CartResponse (активная корзина пользователя 1)
cart:2 → CartResponse (активная корзина пользователя 2)
```

### **TTL кеша:**
- **24 часа** - оптимальный баланс между производительностью и актуальностью
- **Автоматическое обновление** при изменении корзины
- **Принудительная очистка** при создании заказа

### **Стратегия кеширования:**
```java
// Получение корзины
public CartResponse getCart(Long userId) {
    // 1. Пробуем кеш
    return cartCacheService.getCachedCart(userId)
        .orElseGet(() -> {
            // 2. Если нет в кеше, получаем из БД
            Cart cart = getActiveCartOrThrow(userId);
            CartResponse response = CartMapper.toCartResponse(cart);
            
            // 3. Сохраняем в кеш
            cartCacheService.cacheCart(userId, response);
            
            return response;
        });
}
```

---

## 📈 Производительность

### **Без кеша:**
```
Запрос корзины → PostgreSQL → ~10-50ms
```

### **С кешем:**
```
Запрос корзины → Redis → ~1-5ms (95% случаев)
Запрос корзины → PostgreSQL → ~10-50ms (5% случаев)
```

### **Улучшение:**
- **В 10-50 раз быстрее** для кешированных запросов
- **Снижение нагрузки** на PostgreSQL на 80-90%
- **Лучший UX** для пользователей

---

## 🛠️ API Endpoints

### **Основные операции:**
```http
GET    /api/carts              # Получить активную корзину (с кешем)
POST   /api/carts/items        # Добавить товар
DELETE /api/carts/items/{id}   # Удалить товар
DELETE /api/carts              # Очистить корзину
```

### **Восстановление и история:**
```http
GET    /api/carts/history      # История всех корзин
POST   /api/carts/restore      # Восстановить последнюю корзину
POST   /api/carts/convert-to-order  # Преобразовать в заказ
```

---

## 🔍 Мониторинг кеша

### **Метрики для отслеживания:**
```java
// Hit rate (процент попаданий в кеш)
double hitRate = cacheHits / (cacheHits + cacheMisses);

// Среднее время ответа
double avgResponseTime = totalResponseTime / totalRequests;

// Размер кеша
long cacheSize = redisTemplate.keys("cart:*").size();
```

### **Алерты:**
- Hit rate < 80% → Проверить настройки кеша
- Response time > 100ms → Проверить PostgreSQL
- Cache size > 10000 → Очистить старые записи

---

## ⚠️ Важные моменты

### **Синхронизация кеша:**
- **Автоматическое обновление** при изменении корзины
- **Принудительная очистка** при создании заказа
- **Graceful degradation** при недоступности Redis

### **Консистентность данных:**
- **PostgreSQL** - источник истины
- **Redis** - только кеш
- **При расхождении** - приоритет PostgreSQL

### **Обработка ошибок:**
```java
try {
    // Попытка кеша
    return cartCacheService.getCachedCart(userId);
} catch (Exception e) {
    log.warn("Cache failed, falling back to database");
    // Fallback к БД
    return getCartFromDatabase(userId);
}
```

---

## 🎯 Заключение

### **Преимущества новой архитектуры:**
1. **Высокая производительность** - Redis кеш
2. **Надежность** - PostgreSQL хранение
3. **Гибкость** - возможность восстановления корзин
4. **Масштабируемость** - легко добавить новые функции

### **Результат:**
- **Быстрые ответы** для пользователей
- **Полная история** корзин
- **Простое восстановление** покупок
- **Профессиональный уровень** e-commerce 