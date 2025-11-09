package com.backdevfc.cartservice.service;

import com.backdevfc.cartservice.model.dto.request.CartDeleteRequest;
import com.backdevfc.cartservice.model.dto.request.CartRequest;
import com.backdevfc.cartservice.model.dto.response.CartResponse;

public interface ICartService {
    CartResponse findByCustomerId(Long customerId);
    CartResponse addItem(Long customerId, CartRequest request);
    CartResponse removeItem(Long customerId, CartDeleteRequest request);
}
