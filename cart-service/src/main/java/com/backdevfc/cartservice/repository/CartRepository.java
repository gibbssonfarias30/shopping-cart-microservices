package com.backdevfc.cartservice.repository;

import com.backdevfc.cartservice.model.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
	
	Optional<CartEntity> findByCustomerId(Long customerId);

}
