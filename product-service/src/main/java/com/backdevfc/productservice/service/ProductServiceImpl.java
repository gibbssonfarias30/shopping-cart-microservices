package com.backdevfc.productservice.service;


import com.backdevfc.productservice.exception.ResourceNotFoundException;
import com.backdevfc.productservice.model.dto.request.ProductRequest;
import com.backdevfc.productservice.model.dto.request.ProductUpdateRequest;
import com.backdevfc.productservice.model.dto.request.ProductUpdateStockRequest;
import com.backdevfc.productservice.model.dto.response.ProductResponse;
import com.backdevfc.productservice.model.entity.CategoryEntity;
import com.backdevfc.productservice.model.entity.ProductEntity;
import com.backdevfc.productservice.model.enums.ProductStatus;
import com.backdevfc.productservice.mapper.ProductMapper;
import com.backdevfc.productservice.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.backdevfc.productservice.model.enums.DeletedProduct.CREATED;
import static com.backdevfc.productservice.model.enums.DeletedProduct.DELETED;


@RequiredArgsConstructor
@Slf4j
@Service
public class ProductServiceImpl implements IProductService {

    private final IProductRepository productRepository;
    private final ProductMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponse> findAll(ProductStatus status, int port) {
        log.info("Finding all products with status {} on port {}", status, port);
        return productRepository.findAll(CREATED, status)
                .stream()
                .map(product -> mapper.entityToResponse(product, port))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse findById(Long id, int port) {
        log.info("Finding product with id {}", id);
        return productRepository.findById(id)
                .filter(product -> product.getDeleted() == CREATED)
                .map(product -> mapper.entityToResponse(product, port))
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponse> findByIdCategory(Long id, int port) {
        log.info("Finding products by category {}", id);
        return productRepository.findByCategoryAndDeleted(
                        CategoryEntity.builder().id(id).build(), CREATED)
                .stream()
                .map(product -> mapper.entityToResponse(product, port))
                .toList();
    }

    @Transactional
    @Override
    public ProductResponse save(ProductRequest request, int port) {
        log.info("Creating product {}", request.getName());
        ProductEntity productEntity = mapper.requestToEntity(request);
        productRepository.save(productEntity);
        return mapper.entityToResponse(productEntity, port);
    }

    @Transactional
    @Override
    public ProductResponse update(Long id, ProductUpdateRequest request, int port) {
        log.info("Updating product {}", id);
        ProductEntity productEntity = getProductById(id);
        mapper.updateEntityFromRequest(request, productEntity);

        productEntity.setCategory(
                CategoryEntity.builder().id(request.getCategoryId()).build()
        );
        productRepository.save(productEntity);
        log.info("updated");
        return mapper.entityToResponse(productEntity, port);
    }

    @Transactional
    @Override
    public ProductResponse updateStock(Long id, ProductUpdateStockRequest request, int port) {
        log.info("Updating stock for product {} (+{})", id, request.getStock());
        ProductEntity productEntity = getProductById(id);
        productEntity.setStock(productEntity.getStock() + request.getStock());
        productRepository.save(productEntity);
        return mapper.entityToResponse(productEntity, port);
    }

    @Transactional
    @Override
    public void delete(Long id, int port) {
        log.info("Deleting product {}", id);
        ProductEntity productEntity = getProductById(id);
        productEntity.setDeleted(DELETED);
        productRepository.save(productEntity);
    }

    private ProductEntity getProductById(Long id) {
        return productRepository.findById(id)
                .filter(product -> product.getDeleted() == CREATED)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
