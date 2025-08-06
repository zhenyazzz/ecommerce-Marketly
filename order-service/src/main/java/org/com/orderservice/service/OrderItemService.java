package org.com.orderservice.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.com.orderservice.dto.mapper.OrderMapper;
import org.com.orderservice.dto.request.create.CreateOrderItemRequest;
import org.com.orderservice.dto.request.update.UpdateOrderItemRequest;
import org.com.orderservice.dto.response.OrderItemResponse;
import org.com.orderservice.exception.OrderItemNotFoundException;
import org.com.orderservice.model.OrderItem;
import org.com.orderservice.repository.OrderItemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    public final OrderItemRepository orderItemRepository;
    public final OrderMapper orderMapper;



    public List<OrderItemResponse> getAllOrderItems(
    ){
       List<OrderItem> orderItems = orderItemRepository.findAll();
       return orderMapper.toOrderItemResponseList(orderItems);
   }




    @Cacheable(value = "orderItems", key = "#orderItemId")
    public OrderItem getOrderItem(UUID orderItemId
    ){
        OrderItem orderItem=orderItemRepository.findById(orderItemId)
                .orElseThrow(()->new OrderItemNotFoundException("Order with id " + orderItemId + " not found"));
        return orderItem;
    }




    @Transactional
    public OrderItemResponse createOrderItem(CreateOrderItemRequest request
    ){
        OrderItem orderItem=buildOrderItemFromCreateRequest(request);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return orderMapper.toOrderItemResponse(savedOrderItem);
    }


    @CacheEvict(value = "orderItems", key = "#orderItemId")
    @Transactional
    public void deleteOrderItem(UUID orderItemId
    ){
        if (!orderItemRepository.existsById(orderItemId)) {
            throw new OrderItemNotFoundException("Product not found with id: " + orderItemId);
        }
        orderItemRepository.deleteById(orderItemId);
    }




    public OrderItem buildOrderItemFromCreateRequest(CreateOrderItemRequest request
    ){
        OrderItem orderItem=new OrderItem();
        orderItem.setName(request.getName());
        orderItem.setPrice(request.getPrice());
        orderItem.setQuantity(request.getQuantity());
        return orderItem;
    }

}
