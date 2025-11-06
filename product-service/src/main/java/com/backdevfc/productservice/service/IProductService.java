package com.backdevfc.productservice.service;

import com.backdevfc.productservice.model.dto.request.ProductRequest;
import com.backdevfc.productservice.model.dto.request.ProductUpdateRequest;
import com.backdevfc.productservice.model.dto.request.ProductUpdateStockRequest;
import com.backdevfc.productservice.model.dto.response.ProductResponse;
import com.backdevfc.productservice.model.enums.ProductStatus;

import java.util.List;

public interface IProductService {

    List<ProductResponse> findAll(ProductStatus status, int port);
    ProductResponse findById(Long id, int port);
    List<ProductResponse> findByIdCategory(Long id, int port);
    ProductResponse save(ProductRequest request, int port);
    ProductResponse update(Long id, ProductUpdateRequest request, int port);
    ProductResponse updateStock(Long id, ProductUpdateStockRequest request, int port);
    void delete(Long id, int port);
}
