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
	
	private final IProductService productService;
	private final ServletWebServerApplicationContext webServer;
	
	@GetMapping
	ResponseEntity<List<ProductResponse>> findAll(@RequestParam(required = false) ProductStatus status) {
		var result = productService.findAll(status, currentPort());
		return result.isEmpty()
				? ResponseEntity.noContent().build()
				: ResponseEntity.ok(result);
	}
	
	@GetMapping(value = "/{id}")
	ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
		var response = productService.findById(id, currentPort());
		return ResponseEntity.ok(response);
	}
	
	@PostMapping
	ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
		var response = productService.save(request, currentPort());
		return ResponseEntity
				.created(URI.create("/products/".concat(response.getId().toString())))
				.body(response);
	}
	
	@PutMapping(value="/{id}")
	public ResponseEntity<ProductResponse> update(@PathVariable Long id,
												  @Valid @RequestBody ProductUpdateRequest request) {
		var response = productService.update(id, request, currentPort());
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping(value = "/{id}/stock")
	public ResponseEntity<ProductResponse> updateStock(@PathVariable Long id,
													   @Valid @RequestBody ProductUpdateStockRequest request) {
		var response = productService.updateStock(id, request, currentPort());
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		productService.delete(id, currentPort());
		return ResponseEntity.noContent().build();
	}

	private int currentPort() {
		return webServer.getWebServer().getPort();
	}

}