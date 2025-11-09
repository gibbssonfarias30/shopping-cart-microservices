package com.backdevfc.cartservice.service;

import com.backdevfc.cartservice.client.ClientProductFeign;
import com.backdevfc.cartservice.exception.ResourceNotFoundException;
import com.backdevfc.cartservice.model.dto.request.CartDeleteRequest;
import com.backdevfc.cartservice.model.dto.request.CartRequest;
import com.backdevfc.cartservice.model.dto.response.CartResponse;
import com.backdevfc.cartservice.model.dto.response.ProductResponse;
import com.backdevfc.cartservice.model.entity.CartEntity;
import com.backdevfc.cartservice.mapper.CartMapper;
import com.backdevfc.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final CartMapper mapper;
    private final ClientProductFeign clientProductFeign;

    @Transactional(readOnly = true)
    @Override
    public CartResponse findByCustomerId(Long customerId) {
        log.info("findByCustomerId");
        return cartRepository.findByCustomerId(customerId)
                .map(mapper::entityToResponse)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional
    @Override
    public CartResponse addItem(Long customerId, CartRequest request) {
        log.info("addItem");
        CartEntity cartEntity = cartRepository.findByCustomerId(customerId)
                .orElseThrow(ResourceNotFoundException::new);

        request.getItems().forEach(itemRequest -> {

            boolean itemExists = cartEntity.getItems()
                    .stream()
                    .anyMatch(item -> Objects.equals(item.getProductId(), itemRequest.getProductId()));

            if (!itemExists) {
                ProductResponse product = clientProductFeign.findById(itemRequest.getProductId());
                log.info("Product found on port: {}", product.getPort());

                cartEntity.getItems().add(mapper.buildCartItem(product, itemRequest.getQuantity()));
            }
        });

        cartRepository.save(cartEntity);

        return mapper.entityToResponse(cartEntity);
    }

    @Override
    public CartResponse removeItem(Long customerId, CartDeleteRequest request) {
        CartEntity cartEntity = cartRepository.findByCustomerId(customerId)
                .orElseThrow(ResourceNotFoundException::new);

        request.getItems().forEach(itemRequest -> cartEntity.getItems()
                .removeIf(item -> Objects.equals(item.getProductId(), itemRequest.getProductId())));

        cartRepository.save(cartEntity);

        return mapper.entityToResponse(cartEntity);
    }
}