package org.com.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.com.orderservice.client.CartServiceClient;
import org.com.orderservice.client.ProductServiceClient;
import org.com.orderservice.dto.external.cart_service.CartResponse;
import org.com.orderservice.dto.mapper.OrderMapper;
import org.com.orderservice.dto.request.OrderRequest;
import org.com.orderservice.dto.response.OrderResponse;
import org.com.orderservice.exception.OrderCancellationException;
import org.com.orderservice.exception.OrderNotFoundException;
import org.com.orderservice.model.Order;
import org.com.orderservice.model.OrderItem;
import org.com.orderservice.model.OrderStatus;
import org.com.orderservice.repository.OrderItemRepository;
import org.com.orderservice.repository.OrderRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartServiceClient cartServiceClient;
    private final ProductServiceClient productServiceClient;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(UUID userId, OrderRequest request) {
        // 1. Получаем корзину из cart-service
        CartResponse cart = cartServiceClient.getCart(userId);

        // 2. Создаем новый заказ
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);
        order.setShippingAddress(request.shippingAddress());
        order.setCustomerNotes(request.customerNotes());
        order.setPaymentMethod(request.paymentMethod());
        order.setDeliveryType(request.deliveryType());
        // 3. Конвертируем товары из корзины в позиции заказа
        List<OrderItem> items = cart.items().stream()
                .map(cartItem -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(cartItem.productId());
                    item.setName(cartItem.name());
                    item.setPrice(cartItem.price());
                    item.setQuantity(cartItem.quantity());
                    item.setOrder(order); // Устанавливаем связь
                    return item;
                })
                .toList();

        order.setItems(items);

        // 4. Сохраняем заказ (каскадно сохранит OrderItem)
        Order savedOrder = orderRepository.save(order);

        // 5. Очищаем корзину
        cartServiceClient.clearCart(userId);

        // 6. Обновляем остатки товаров
        updateProductStocks(items);

        return orderMapper.mapToResponse(order);
    }

    @Cacheable(value = "orders", key = "#orderId")
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));
        return orderMapper.mapToResponse(order);
    }

    @CacheEvict(value = "orders", key = "#orderId")
    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(UUID userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::mapToResponse)
                .toList();
    }

    @CacheEvict(value = "orders", key = "#orderId")
    @Transactional
    public void cancelOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new OrderCancellationException("Only CREATED orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Возвращаем товары на склад
        returnProductStocks(order.getItems());
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updateProductStocks(List<OrderItem> items) {
        items.forEach(item ->
                productServiceClient.updateStock(
                        item.getProductId(),
                        item.getQuantity()
                )
        );
    }

    private void returnProductStocks(List<OrderItem> items) {
        items.forEach(item ->
                productServiceClient.updateStock(
                        item.getProductId(),
                        item.getQuantity()
                )
        );
    }

}
