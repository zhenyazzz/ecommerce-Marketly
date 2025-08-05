package org.com.orderservice.dto.mapper;

import org.com.orderservice.client.ProductStockUpdateRequest;
import org.com.orderservice.dto.request.CreateOrderRequest;
import org.com.orderservice.dto.request.UpdateOrderRequest;
import org.com.orderservice.dto.response.OrderItemResponse;
import org.com.orderservice.dto.response.OrderResponse;
import org.com.orderservice.dto.external.cart_service.CartItemResponse;
import org.com.orderservice.model.Order;
import org.com.orderservice.model.OrderItem;
import org.mapstruct.*;

import java.lang.annotation.Target;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {


    @Mappings({
        @Mapping(target = "orderId", source = "id"),
        @Mapping(target = "totalAmount", source = "total"),
        @Mapping(target = "items", source = "orderItems")
    })
    OrderResponse toOrderResponse(Order order);


    List<OrderResponse> toOrderResponseList(List<Order> orders);


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


    default List<OrderItem> toOrderItemList(List<CartItemResponse> cartItems, Order order) {
        return cartItems.stream()
                .map(cartItem -> {
                    OrderItem item = toOrderItem(cartItem);
                    item.setOrder(order); // Устанавливаем связь вручную
                    return item;
                })
                .toList();
    }

    void updateOrderFromUpdateOrderRequest(UpdateOrderRequest request, @MappingTarget Order order);

    @Mappings({
            @Mapping(target="status",expression = "java(OrderStatus.CREATED)"),
            @Mapping(target = "userId",expression = "java(userId)")})
    Order toOrder(CreateOrderRequest createOrderRequest, @Context Long userId);

    @Mappings({
            @Mapping(target = "productId", source = "productId"),
            @Mapping(target = "quantity", source = "quantity")
    })
    ProductStockUpdateRequest toProductStockUpdateRequest(OrderItem orderItem);
}
