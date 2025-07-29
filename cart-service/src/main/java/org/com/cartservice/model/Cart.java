package org.com.cartservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> cartItems = new ArrayList<>();
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CartStatus status;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @Column(name = "last_activity_at")
    private Instant lastActivityAt;
    
    @Column(name = "session_id")
    private String sessionId; // Для неавторизованных пользователей
    
    @Column(name = "order_id")
    private Long orderId; 

    
    public void addItem(CartItem item) {
        cartItems.add(item);
        item.setCart(this);
        recalculateTotal();
    }
    
    
    public void removeItem(UUID productId) {
        cartItems.removeIf(item -> item.getProductId().equals(productId));
        recalculateTotal();
    }
    
    
    public void recalculateTotal() {
        this.total = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    
    public Cart createNewCartAfterOrder(Long orderId) {
        return Cart.builder()
                .userId(this.userId)
                .cartItems(new ArrayList<>())
                .total(BigDecimal.ZERO)
                .status(CartStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .lastActivityAt(Instant.now())
                .sessionId(this.sessionId)
                .build();
    }
    
    
    public void archive(Long orderId) {
        this.status = CartStatus.ARCHIVED;
        this.orderId = orderId;
        this.updatedAt = Instant.now();
    }
    
    
    public void updateActivity() {
        this.updatedAt = Instant.now();
        this.lastActivityAt = Instant.now();
    }
}
