package com.backdevfc.cartservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart_item")
public class CartItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long productId;
	
	private String name;
	
	private BigDecimal price;
	
	private Integer quantity;
	
	private BigDecimal subTotal;
	
	@CreationTimestamp
	private LocalDateTime createdDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cart_id", insertable = false, updatable = false)
	private CartEntity cart;

	@PrePersist
	void setPrePersist() {
		subTotal = price.multiply(BigDecimal.valueOf(quantity));
	}

}
