package org.com.cartservice.model;

import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    private UUID productId;
    private String name;
    private BigDecimal price;
    @Min(value = 1, message = "quantity must be more than 0")
    private int quantity;
}
