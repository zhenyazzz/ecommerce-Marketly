package org.com.cartservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "carts", timeToLive = 2_592_000)
public class Cart {
    @Id
    UUID id;
    @Indexed
    UUID userId;
    List<CartItem> cartItems;
    BigDecimal total;
    @Indexed
    CartStatus status;

    public Cart withAddedItem(CartItem item) {
        List<CartItem> newItems = new ArrayList<>(cartItems);
        newItems.add(item);
        BigDecimal newTotal = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return new Cart(id, userId, List.copyOf(newItems), newTotal, status);
    }
}
