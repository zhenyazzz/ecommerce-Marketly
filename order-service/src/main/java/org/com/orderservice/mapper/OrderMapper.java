package org.com.orderservice.mapper;


import org.com.orderservice.dto.request.create.CreateOrderItemRequest;
import org.com.orderservice.dto.request.create.CreateOrderRequest;
import org.com.orderservice.dto.request.update.UpdateOrderItemRequest;
import org.com.orderservice.dto.request.update.UpdateOrderRequest;
import org.com.orderservice.dto.response.OrderItemResponse;
import org.com.orderservice.dto.response.OrderResponse;
import org.com.orderservice.model.Order;
import org.com.orderservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;




//TODO: complete in need
@Component
@Mapper(componentModel = "spring")
public interface OrderMapper {




    public CreateOrderRequest toCreateOrderRequest(Order order);


    public UpdateOrderRequest toUpdateOrderRequest(Order order);


    public OrderResponse toOrderResponse(Order order);




}
