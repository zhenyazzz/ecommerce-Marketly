package org.com.cartservice.repository;

import org.com.cartservice.model.Cart;
import org.com.cartservice.model.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    
    // Найти активную корзину пользователя
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
    
    // Найти все корзины пользователя
    List<Cart> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Найти корзины по статусу
    List<Cart> findByUserIdAndStatusIn(Long userId, List<CartStatus> statuses);
    
    // Найти корзину по ID заказа
    Optional<Cart> findByOrderId(Long orderId);
    
    // Найти корзины по статусу (для аналитики)
    List<Cart> findByStatus(CartStatus status);
    
    // Найти брошенные корзины (неактивные более 30 дней)
    @Query("SELECT c FROM Cart c WHERE c.status = :status AND c.lastActivityAt < :cutoffDate")
    List<Cart> findAbandonedCarts(@Param("status") CartStatus status, @Param("cutoffDate") java.time.Instant cutoffDate);
    
    // Найти корзины с товарами (для аналитики)
    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND SIZE(c.cartItems) > 0 ORDER BY c.createdAt DESC")
    List<Cart> findCartsWithItems(@Param("userId") Long userId);
    
    // Удалить старые архивные корзины (старше 1 года)
    @Query("DELETE FROM Cart c WHERE c.status IN (:archivedStatuses) AND c.createdAt < :cutoffDate")
    void deleteOldArchivedCarts(@Param("archivedStatuses") List<CartStatus> archivedStatuses, 
                               @Param("cutoffDate") java.time.Instant cutoffDate);
}
