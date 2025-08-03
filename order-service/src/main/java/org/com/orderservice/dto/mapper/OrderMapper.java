package org.com.orderservice.dto.mapper;

import org.com.orderservice.dto.response.OrderItemResponse;
import org.com.orderservice.dto.response.OrderResponse;
import org.com.orderservice.dto.external.cart_service.CartItemResponse;
import org.com.orderservice.model.Order;
import org.com.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mappings({
        @Mapping(target = "orderId", source = "id"),
        @Mapping(target = "totalAmount", source = "total"),
        @Mapping(target = "items", source = "orderItems")
    })
    OrderResponse toOrderResponse(Order order);

    @Mappings({
        @Mapping(target = "productId", source = "productId"),
        @Mapping(target = "name", source = "name"),
        @Mapping(target = "price", source = "price"),
        @Mapping(target = "quantity", source = "quantity")
    })
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems);

    @Mappings({
        @Mapping(target = "productId", source = "productId"),
        @Mapping(target = "name", source = "name"),
        @Mapping(target = "price", source = "price"),
        @Mapping(target = "quantity", source = "quantity")
    })
    OrderItem toOrderItem(CartItemResponse cartItemResponse);
    List<OrderItem> toOrderItemList(List<CartItemResponse> cartItems);
}
