package com.backdevfc.cartservice.client;

import com.backdevfc.cartservice.model.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service", path = "/api/v1/products")
public interface ClientProductFeign {
	
	@GetMapping
	public List<ProductResponse> findAll();
	
	@GetMapping("/{id}")
	public ProductResponse findById(@PathVariable Long id);

}