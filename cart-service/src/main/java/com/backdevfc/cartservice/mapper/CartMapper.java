package com.backdevfc.cartservice.mapper;



import com.backdevfc.cartservice.model.dto.response.CartItemResponse;
import com.backdevfc.cartservice.model.dto.response.CartResponse;
import com.backdevfc.cartservice.model.dto.response.ProductResponse;
import com.backdevfc.cartservice.model.entity.CartEntity;
import com.backdevfc.cartservice.model.entity.CartItemEntity;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartMapper {


	@Mapping(source = "items", target = "items", qualifiedByName = "entityToResponseForItems")
	CartResponse entityToResponse(CartEntity entity);


	CartItemResponse entityToResponse(CartItemEntity entity);
	
	@Named("entityToResponseForItems")
    default List<CartItemResponse> entityToResponseForItems(List<CartItemEntity> items) {
		return items.stream()
				.map(this::entityToResponse)
				.toList();
    }


	default CartItemEntity buildCartItem(ProductResponse response, int quantity) {

		BigDecimal subTotal = response.getPrice().multiply(BigDecimal.valueOf(quantity));

		return CartItemEntity.builder()
				.productId(response.getId())
				.name(response.getName())
				.price(response.getPrice())
				.quantity(quantity)
				.subTotal(subTotal)
				.build();
	}

}