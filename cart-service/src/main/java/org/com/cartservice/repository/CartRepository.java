package org.com.cartservice.repository;

import org.com.cartservice.model.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends CrudRepository<Cart, UUID> {
    Optional<Cart> findByUserId(UUID uuid);
}
