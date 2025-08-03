package org.com.cartservice.dto.mapper;

import org.com.cartservice.dto.response.CartItemResponse;
import org.com.cartservice.dto.response.CartResponse;
import org.com.cartservice.dto.response.ProductDto;
import org.com.cartservice.model.Cart;
import org.com.cartservice.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", imports = {Instant.class, Collectors.class})
public interface CartMapper {

    CartItemResponse toCartItemResponse(CartItem cartItem);

    @Mapping(target = "cartItems", expression = "java(cart.getCartItems().stream().map(this::toCartItemResponse).collect(Collectors.toList()))")
    CartResponse toCartResponse(Cart cart);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", source = "productDto.id")
    @Mapping(target = "name", source = "productDto.name")
    @Mapping(target = "price", source = "productDto.price")
    @Mapping(target = "quantity", source = "productDto.quantity")
    @Mapping(target = "cart", ignore = true)
    CartItem toCartItem(ProductDto productDto);


} 
