package com.backdevfc.productservice;


import com.backdevfc.productservice.model.entity.CategoryEntity;
import com.backdevfc.productservice.model.entity.ProductEntity;
import com.backdevfc.productservice.repository.IProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.backdevfc.productservice.model.enums.DeletedProduct.CREATED;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private IProductRepository productRepository;

    @Test
    void whenGetAll_ThenReturnAllProduct() {
        var list = productRepository.findAll(CREATED,null);
        assertEquals(3, list.size());
    }

    @Test
    void whenValidGetId_ThenReturnProduct() {
        Optional<ProductEntity> productEntity = productRepository.findById(1L);
        assertTrue(productEntity.isPresent());
        assertEquals("Laptop", productEntity.orElseThrow().getName());
    }

    @Test
    void whenInValidGetId_ThenNotFound() {
        Optional<ProductEntity> productEntity = productRepository.findById(55L);
        assertThrows(NoSuchElementException.class, productEntity::orElseThrow);
        assertFalse(productEntity.isPresent());
    }

    @Test
    void whenValidSave_ThenReturnProduct() {
        var productEntity = ProductEntity.builder()
                .name("Teclado")
                .stock(Double.valueOf(10))
                .price(BigDecimal.valueOf(300))
                .category(CategoryEntity.builder().id(1L).build())
                .build();
        productRepository.save(productEntity);

        var product = productRepository.findByCategoryAndDeleted(productEntity.getCategory(), CREATED);
        assertEquals(11, product.size());
    }
}
