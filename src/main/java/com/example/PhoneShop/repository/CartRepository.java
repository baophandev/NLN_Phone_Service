package com.example.PhoneShop.repository;

import com.example.PhoneShop.entities.Cart;
import com.example.PhoneShop.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Cart findByUserId(String userId);
}
