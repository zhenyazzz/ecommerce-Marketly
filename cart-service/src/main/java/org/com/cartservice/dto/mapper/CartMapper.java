package org.com.cartservice.dto.mapper;

import org.com.cartservice.dto.response.CartItemResponse;
import org.com.cartservice.dto.response.CartResponse;
import org.com.cartservice.model.Cart;
import org.com.cartservice.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper {
    public static CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> cartItems = cart.getCartItems().stream().map(CartMapper::toCartItemDto).toList();
        return new CartResponse(cart.getId(),
                cartItems,
                cart.getTotal(),
                cart.getStatus().toString());
    }

    public static CartItemResponse toCartItemDto(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getProductId(),
                cartItem.getName(),
                cartItem.getPrice(),
                cartItem.getQuantity()
        );
    }
}
