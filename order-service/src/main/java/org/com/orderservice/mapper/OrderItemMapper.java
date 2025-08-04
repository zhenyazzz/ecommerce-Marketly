package org.com.orderservice.mapper;

import org.com.orderservice.dto.request.create.CreateOrderItemRequest;
import org.com.orderservice.dto.request.update.UpdateOrderItemRequest;
import org.com.orderservice.dto.response.OrderItemResponse;
import org.com.orderservice.model.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {


    public CreateOrderItemRequest toCreateOrderItemRequest(OrderItem orderItem);

    public UpdateOrderItemRequest toUpdateOrderItemRequest(OrderItem orderItem);

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem);

}
