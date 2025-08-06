package org.com.orderservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.orderservice.client.CartServiceClient;
import org.com.orderservice.client.ProductServiceClient;
import org.com.orderservice.dto.external.cart_service.CartResponse;
import org.com.orderservice.dto.mapper.OrderMapper;
import org.com.orderservice.dto.request.CreateOrderRequest;
import org.com.orderservice.dto.request.UpdateOrderRequest;
import org.com.orderservice.dto.response.OrderResponse;
import org.com.orderservice.exception.OrderCancellationException;
import org.com.orderservice.exception.OrderNotFoundException;
import org.com.orderservice.kafka.KafkaProducerService;
import org.com.orderservice.model.Order;
import org.com.orderservice.model.OrderItem;
import org.com.orderservice.model.OrderStatus;
import org.com.orderservice.repository.OrderItemRepository;
import org.com.orderservice.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
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
    private final KafkaProducerService kafkaProducerService;



    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId, Long userId
    ){
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));
        return orderMapper.toOrderResponse(order);
    }




    public List<OrderResponse> getAllOrders(
    ){
        return orderMapper.toOrderResponseList(orderRepository.findAll());
    }




    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId
    ){
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }




    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrdersByStatus(Long userId, OrderStatus status
    ){
        return orderMapper.toOrderResponseList(orderRepository.findByUserIdAndStatus(userId,status));
    }




    @Transactional
    @Retry(name = "cartService", fallbackMethod = "createOrderFallback")
    @CircuitBreaker(name = "cartService", fallbackMethod = "createOrderFallback")
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        // 1. Получаем корзину из cart-service
        CartResponse cart = cartServiceClient.getCart(userId);

        // 2. Создаем новый заказ
        Order order = orderMapper.toOrder(request, userId);
        order.setOrderItems(orderMapper.toOrderItemList(cart.cartItems(), order));

        // 3. Сохраняем заказ (каскадно сохранит OrderItem)
        Order savedOrder = orderRepository.save(order);

        kafkaProducerService.sendOrderCreatedEvent(orderMapper.tOrderPaymentEvent(order));
        // 4. Очищаем корзину
        cartServiceClient.clearCart(userId);

        // 5. Обновляем остатки товаров
        updateProductStocks(savedOrder.getOrderItems());

        return orderMapper.toOrderResponse(order);
    }

    // Fallback-метод для createOrder
    public OrderResponse createOrderFallback(Long userId, CreateOrderRequest request, Throwable t) {
        // Логируем ошибку и возвращаем дефолтный ответ или кидаем кастомное исключение
        // Можно также поставить заказ в очередь на повторную обработку
        throw new RuntimeException("Cart service is unavailable. Order creation failed.", t);
    }




    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status
    ){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }









    @Transactional
    public void cancelOrder(UUID orderId, Long userId
    ){
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new OrderCancellationException("Only CREATED orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Возвращаем товары на склад
        returnProductStocks(order.getOrderItems());
    }




        private BigDecimal calculateTotal(List<OrderItem> items
        ){
            return items.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }




        private void updateProductStocks(List<OrderItem> items
        ){
            productServiceClient.updateStockBatch(items.stream().map(orderMapper::toProductStockUpdateRequest).toList());
        }




    private void returnProductStocks(List<OrderItem> items) {
        productServiceClient.returnStockBatch(items.stream().map(orderMapper::toProductStockUpdateRequest).toList());
    }



    @Transactional
    public OrderResponse updateOrder(UUID orderId, @Valid UpdateOrderRequest request
    ){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));
        orderMapper.updateOrderFromUpdateOrderRequest(request, order);
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }



}
