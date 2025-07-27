package org.com.orderservice.dto.mapper;

import org.com.orderservice.dto.response.OrderItemDto;
import org.com.orderservice.dto.response.OrderResponse;
import org.com.orderservice.model.Order;
import org.com.orderservice.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderResponse mapToResponse(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(this::mapToOrderItemDto).toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus().toString(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getShippingAddress(),
                order.getPaymentMethod().toString(),
                order.getDeliveryType().toString(),
                items
        );

    }

    public OrderItemDto mapToOrderItemDto(OrderItem orderItem) {
        return new OrderItemDto(
          orderItem.getId(),
          orderItem.getName(),
          orderItem.getPrice(),
          orderItem.getQuantity()
        );
    }
}
