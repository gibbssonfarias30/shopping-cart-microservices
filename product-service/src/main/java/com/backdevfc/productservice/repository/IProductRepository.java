package com.backdevfc.productservice.repository;

import com.backdevfc.productservice.model.entity.CategoryEntity;
import com.backdevfc.productservice.model.entity.ProductEntity;
import com.backdevfc.productservice.model.enums.DeletedProduct;
import com.backdevfc.productservice.model.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface IProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("FROM ProductEntity p WHERE p.deleted = :deleted AND (:status IS NULL OR p.status = :status)")
    List<ProductEntity> findAll(@Param("deleted") DeletedProduct deleted, @Param("status") ProductStatus status);

    List<ProductEntity> findByCategoryAndDeleted(CategoryEntity category, DeletedProduct deleted);

}
