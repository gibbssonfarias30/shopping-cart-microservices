package com.backdevfc.productservice.controller;


import com.backdevfc.productservice.model.dto.request.ProductRequest;
import com.backdevfc.productservice.model.dto.request.ProductUpdateRequest;
import com.backdevfc.productservice.model.dto.request.ProductUpdateStockRequest;
import com.backdevfc.productservice.model.dto.response.ProductResponse;
import com.backdevfc.productservice.model.enums.ProductStatus;
import com.backdevfc.productservice.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
	
	private final IProductService service;
	private final ServletWebServerApplicationContext webServer;
	
	@GetMapping
	ResponseEntity<List<ProductResponse>> findAll(@RequestParam(required = false) ProductStatus status) {
		var result = service.findAll(status, currentPort());
		return result.isEmpty()
				? ResponseEntity.noContent().build()
				: ResponseEntity.ok(result);
	}
	
	@GetMapping(value = "/{id}")
	ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
		var response = service.findById(id, currentPort());
		return ResponseEntity.ok(response);
	}
	
	@PostMapping
	ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
		var response = service.save(request, currentPort());
		return ResponseEntity
				.created(URI.create("/" + response.getId()))
				.body(response);
	}
	
	@PutMapping(value="/{id}")
	public ResponseEntity<ProductResponse> update(@PathVariable Long id,
												  @Valid @RequestBody ProductUpdateRequest request) {
		var response = service.update(id, request, currentPort());
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping(value = "/{id}/stock")
	public ResponseEntity<ProductResponse> updateStock(@PathVariable Long id,
													   @Valid @RequestBody ProductUpdateStockRequest request) {
		var response = service.updateStock(id, request, currentPort());
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		service.delete(id, currentPort());
		return ResponseEntity.noContent().build();
	}

	private int currentPort() {
		return webServer.getWebServer().getPort();
	}

}