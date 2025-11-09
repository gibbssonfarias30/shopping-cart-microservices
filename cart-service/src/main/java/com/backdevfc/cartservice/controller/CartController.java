package com.backdevfc.cartservice.controller;

import com.backdevfc.cartservice.model.dto.request.CartDeleteRequest;
import com.backdevfc.cartservice.model.dto.request.CartRequest;
import com.backdevfc.cartservice.model.dto.response.CartResponse;
import com.backdevfc.cartservice.service.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/carts")
public class CartController {
	
	private final ICartService cartService;


	@GetMapping("/{customerId}")
	ResponseEntity<CartResponse> findByCustomerId(@PathVariable Long customerId){
		return ResponseEntity.ok(cartService.findByCustomerId(customerId));
	}
	
	@PostMapping( "/{customerId}/item")
	public ResponseEntity<CartResponse> addItem(@PathVariable Long customerId,
												   @Valid @RequestBody CartRequest request) {

		CartResponse response = cartService.addItem(customerId, request);
		return ResponseEntity.created(
				URI.create("/carts/".concat(response.getCustomerId().toString()).concat("/item"))
				).body(response);
	}
	
	@DeleteMapping( "/{customerId}/item")
	public ResponseEntity<CartResponse> removeItem(@PathVariable Long customerId,
											   		  @Valid @RequestBody CartDeleteRequest request) {
		cartService.removeItem(customerId, request);
		return ResponseEntity.noContent().build();
	}
}