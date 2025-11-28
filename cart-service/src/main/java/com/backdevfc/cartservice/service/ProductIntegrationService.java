package com.backdevfc.cartservice.service;

import com.backdevfc.cartservice.client.ClientProductFeign;
import com.backdevfc.cartservice.model.dto.response.ProductResponse;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIntegrationService {

    private final ClientProductFeign client;


    @Qualifier("applicationTaskExecutor")
    private final Executor executor;

    @CircuitBreaker(name = "product", fallbackMethod = "findByIdFallback")
    public CompletableFuture<ProductResponse> findByIdAsync(Long id) {
        log.info("Calling product service async...");

        return CompletableFuture.supplyAsync(() -> client.findById(id), executor);
    }

    // --- Fallback async ---
    public CompletableFuture<ProductResponse> findByIdFallback(Long id, Throwable t) {
        log.warn("Fallback async for product {} | reason={}", id, t.toString());

        if (t instanceof FeignException &&
                ((FeignException) t).status() == HttpStatus.NOT_FOUND.value()) {
            return CompletableFuture.completedFuture(null);
        }

        ProductResponse fallback = ProductResponse.builder()
                .id(id)
                .name("TEMPORAL")
                .stock(0D)
                .price(BigDecimal.ZERO)
                .build();

        return CompletableFuture.completedFuture(fallback);
    }
}
