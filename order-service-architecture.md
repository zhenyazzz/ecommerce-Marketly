# Архитектура Order Service

## 🎯 Основные компоненты:

### **1. Модели данных:**
```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false)
    private DeliveryType deliveryType;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
}

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "quantity", nullable = false)
    private int quantity;
}
```

### **2. Статусы заказа:**
```java
public enum OrderStatus {
    PENDING,        // Ожидает оплаты
    PAID,           // Оплачен
    PROCESSING,     // Обрабатывается
    SHIPPED,        // Отправлен
    DELIVERED,      // Доставлен
    CANCELLED,      // Отменен
    REFUNDED        // Возвращен
}

public enum PaymentMethod {
    CARD,           // Карта
    CASH,           // Наличные
    BANK_TRANSFER   // Банковский перевод
}

public enum DeliveryType {
    COURIER,        // Курьер
    PICKUP,         // Самовывоз
    POST            // Почта
}
```

### **3. DTO для запросов:**
```java
public record CreateOrderRequest(
    @NotBlank String shippingAddress,
    @NotNull PaymentMethod paymentMethod,
    @NotNull DeliveryType deliveryType
) {}

public record OrderResponse(
    Long id,
    Long userId,
    OrderStatus status,
    BigDecimal total,
    String shippingAddress,
    PaymentMethod paymentMethod,
    DeliveryType deliveryType,
    List<OrderItemResponse> items,
    Instant createdAt
) {}
```

---

## 🔄 Процесс создания заказа:

### **1. Получение запроса:**
```http
POST /api/orders
Header: X-User-Id: 1
Body: {
  "shippingAddress": "ул. Пушкина, 10",
  "paymentMethod": "CARD",
  "deliveryType": "COURIER"
}
```

### **2. Валидация:**
- Проверка пользователя
- Проверка корзины (не пустая)
- Проверка остатков товаров
- Валидация адреса доставки

### **3. Создание заказа:**
- Создание записи в БД
- Копирование товаров из корзины
- Расчет общей стоимости

### **4. Обновление других сервисов:**
- Преобразование корзины в заказ
- Уменьшение остатков товаров
- Отправка событий

### **5. Ответ:**
```json
{
  "id": 1001,
  "userId": 1,
  "status": "PENDING",
  "total": 1399.97,
  "shippingAddress": "ул. Пушкина, 10",
  "paymentMethod": "CARD",
  "deliveryType": "COURIER",
  "items": [
    {
      "productId": "uuid-1",
      "name": "iPhone",
      "price": 999.99,
      "quantity": 1
    }
  ],
  "createdAt": "2024-01-15T14:30:00Z"
}
```

---

## 🔗 Интеграция с другими сервисами:

### **Cart Service:**
```java
@FeignClient(name = "cart-service")
public interface CartServiceClient {
    @GetMapping("/api/carts")
    CartResponse getCart(@RequestHeader("X-User-Id") Long userId);
    
    @PostMapping("/api/carts/convert-to-order")
    void convertCartToOrder(@RequestHeader("X-User-Id") Long userId, 
                           @RequestParam Long orderId);
}
```

### **Product Service:**
```java
@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @GetMapping("/api/products/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
    
    @PutMapping("/api/products/{productId}/stock")
    void updateStock(@PathVariable UUID productId, @RequestParam int quantity);
}
```

### **User Service:**
```java
@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/users/{userId}")
    UserDto getUser(@PathVariable Long userId);
}
```

---

## 📊 События Kafka:

### **OrderCreatedEvent:**
```java
public record OrderCreatedEvent(
    Long orderId,
    Long userId,
    BigDecimal total,
    OrderStatus status,
    Instant createdAt
) {}
```

### **OrderStatusChangedEvent:**
```java
public record OrderStatusChangedEvent(
    Long orderId,
    Long userId,
    OrderStatus oldStatus,
    OrderStatus newStatus,
    Instant changedAt
) {}
```

---

## ⚠️ Обработка ошибок:

### **Транзакционность:**
```java
@Transactional
public OrderResponse createOrder(CreateOrderRequest request, Long userId) {
    try {
        // 1. Создание заказа
        Order order = createOrderEntity(request, userId);
        
        // 2. Обновление корзины
        cartServiceClient.convertCartToOrder(userId, order.getId());
        
        // 3. Обновление остатков
        updateProductStock(order.getOrderItems());
        
        // 4. Отправка событий
        sendOrderEvents(order);
        
        return orderMapper.toOrderResponse(order);
        
    } catch (Exception e) {
        // Откат транзакции
        throw new OrderCreationException("Failed to create order", e);
    }
}
```

### **Circuit Breaker:**
```java
@CircuitBreaker(name = "cartService", fallbackMethod = "cartServiceFallback")
public CartResponse getCart(Long userId) {
    return cartServiceClient.getCart(userId);
}

public CartResponse cartServiceFallback(Long userId, Exception e) {
    // Fallback логика
    throw new ServiceUnavailableException("Cart service unavailable");
}
```

---

## 🎯 Заключение:

### **Ключевые моменты:**
1. **Транзакционность** - все операции или ничего
2. **Валидация** - проверка данных перед созданием
3. **Синхронизация** - обновление всех связанных сервисов
4. **События** - уведомление других сервисов
5. **Обработка ошибок** - graceful degradation

### **Результат:**
- Надежное создание заказов
- Консистентность данных
- Масштабируемость
- Отказоустойчивость 