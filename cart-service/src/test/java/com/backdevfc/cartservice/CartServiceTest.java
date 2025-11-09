package com.backdevfc.cartservice;

import com.backdevfc.cartservice.client.ClientProductFeign;
import com.backdevfc.cartservice.model.dto.request.CartItemRequest;
import com.backdevfc.cartservice.model.dto.request.CartRequest;
import com.backdevfc.cartservice.model.dto.response.CartResponse;
import com.backdevfc.cartservice.model.dto.response.ProductResponse;
import com.backdevfc.cartservice.model.entity.CartEntity;
import com.backdevfc.cartservice.model.entity.CartItemEntity;
import com.backdevfc.cartservice.mapper.CartMapper;
import com.backdevfc.cartservice.repository.CartRepository;
import com.backdevfc.cartservice.service.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private CartRepository cartRepository;

	@Mock
	private ClientProductFeign clientProductFeign;

	@Mock
	private CartMapper mapper;

	@InjectMocks
	private CartServiceImpl cartService;

	@Test
	void givenValidCustomerId_whenFindByCustomerId_thenReturnCartResponse() {
		// Given
		CartItemEntity item1 = CartItemEntity.builder()
				.productId(3L)
				.name("Teclado")
				.price(BigDecimal.valueOf(300))
				.quantity(3)
				.build();

		CartEntity cartEntity = CartEntity.builder()
				.customerId(1L)
				.items(List.of(item1))
				.build();

		when(cartRepository.findByCustomerId(1L))
				.thenReturn(Optional.of(cartEntity));

		CartResponse cartResponseMock = new CartResponse();
		cartResponseMock.setCustomerId(1L);

		when(mapper.entityToResponse(cartEntity))
				.thenReturn(cartResponseMock);

		// When
		CartResponse response = cartService.findByCustomerId(1L);

		// Then
		assertEquals(1L, response.getCustomerId());
	}

	@Test
	void givenNewItem_whenAddItem_thenItemIsAdded() {
		// Given
		CartEntity cartEntity = CartEntity.builder()
				.customerId(1L)
				.items(new ArrayList<>())
				.build();

		when(cartRepository.findByCustomerId(1L))
				.thenReturn(Optional.of(cartEntity));

		var request = new CartRequest(
				List.of(new CartItemRequest(10L, 2))
		);

		// mock feign
		var productResponse = ProductResponse.builder()
				.id(10L)
				.name("Mouse Gamer")
				.price(BigDecimal.valueOf(150))
				.stock(5.0)
				.build();

		when(clientProductFeign.findById(10L))
				.thenReturn(productResponse);

		// mock mapper responseToEntity
		CartItemEntity newItem = CartItemEntity.builder()
				.productId(10L)
				.name("Mouse Gamer")
				.price(BigDecimal.valueOf(150))
				.quantity(2)
				.build();

		when(mapper.buildCartItem(productResponse, 2))
				.thenReturn(newItem);

		// mock mapper entityToResponse final
		CartResponse responseMock = new CartResponse();
		responseMock.setCustomerId(1L);
		when(mapper.entityToResponse(cartEntity))
				.thenReturn(responseMock);


		// When
		CartResponse response = cartService.addItem(1L, request);


		// Then
		assertEquals(1L, response.getCustomerId());
		assertEquals(1, cartEntity.getItems().size());
		assertEquals(10L, cartEntity.getItems().get(0).getProductId());
	}
}