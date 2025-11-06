package com.backdevfc.productservice;

import com.backdevfc.productservice.exception.ResourceNotFoundException;
import com.backdevfc.productservice.model.dto.request.ProductRequest;
import com.backdevfc.productservice.model.dto.request.ProductUpdateRequest;
import com.backdevfc.productservice.model.dto.request.ProductUpdateStockRequest;
import com.backdevfc.productservice.model.dto.response.ProductResponse;
import com.backdevfc.productservice.model.entity.CategoryEntity;
import com.backdevfc.productservice.model.entity.ProductEntity;
import com.backdevfc.productservice.mapper.ProductMapper;
import com.backdevfc.productservice.repository.IProductRepository;
import com.backdevfc.productservice.service.ProductServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.backdevfc.productservice.model.enums.DeletedProduct.CREATED;
import static com.backdevfc.productservice.model.enums.DeletedProduct.DELETED;
import static com.backdevfc.productservice.model.enums.ProductStatus.NEW;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private IProductRepository productRepository;

	@Mock
	private ProductMapper mapper;

	@InjectMocks
	private ProductServiceImpl productService;

	@Test
	void givenProducts_whenFindAll_thenReturnResponses() {

		// Given
		ProductEntity product = Instancio.of(ProductEntity.class)
				.set(field(ProductEntity::getDeleted), CREATED)
				.create();

		ProductResponse mapped = Instancio.create(ProductResponse.class);

		when(productRepository.findAll(CREATED, NEW))
				.thenReturn(List.of(product));

		when(mapper.entityToResponse(product, 10))
				.thenReturn(mapped);

		// When
		List<ProductResponse> result = productService.findAll(NEW, 10);

		// Then
		assertEquals(1, result.size());
	}

	@Test
	void givenValidId_whenFindById_thenReturnProduct() {

		// Given
		ProductEntity product = Instancio.of(ProductEntity.class)
				.set(field(ProductEntity::getId), 1L)
				.set(field(ProductEntity::getDeleted), CREATED)
				.create();

		ProductResponse mapped = Instancio.create(ProductResponse.class);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(mapper.entityToResponse(product, 10)).thenReturn(mapped);

		// When
		ProductResponse result = productService.findById(1L, 10);

		// Then
		assertNotNull(result);
	}

	@Test
	void givenDeletedProduct_whenFindById_thenThrowException() {

		// Given
		ProductEntity product = Instancio.of(ProductEntity.class)
				.set(field(ProductEntity::getDeleted), DELETED)
				.create();

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));

		// Then
		assertThrows(ResourceNotFoundException.class,
				() -> productService.findById(1L, 10));
	}

	@Test
	void givenCategory_whenFindByIdCategory_thenReturnProductResponses() {

		// Given
		ProductEntity product = Instancio.of(ProductEntity.class)
				.set(field(ProductEntity::getDeleted), CREATED)
				.create();

		ProductResponse mapped = Instancio.create(ProductResponse.class);

		when(productRepository.findByCategoryAndDeleted(
				any(CategoryEntity.class), eq(CREATED)))
				.thenReturn(List.of(product));

		when(mapper.entityToResponse(product, 10)).thenReturn(mapped);

		// When
		List<ProductResponse> result = productService.findByIdCategory(1L, 10);

		// Then
		assertEquals(1, result.size());
	}

	@Test
	void givenValidRequest_whenSave_thenReturnResponse() {

		// Given
		ProductRequest request = Instancio.create(ProductRequest.class);

		ProductEntity entity = Instancio.of(ProductEntity.class)
				.set(field(ProductEntity::getId), 1L)
				.create();

		ProductResponse response = Instancio.of(ProductResponse.class)
				.set(field(ProductResponse::getId), 1L)
				.create();

		when(mapper.requestToEntity(request)).thenReturn(entity);
		when(productRepository.save(entity)).thenReturn(entity);
		when(mapper.entityToResponse(entity, 10)).thenReturn(response);

		// When
		ProductResponse result = productService.save(request, 10);

		// Then
		assertEquals(1L, result.getId());
	}

	@Test
	void givenValidRequest_whenUpdate_thenReturnResponse() {

		// Given
		ProductUpdateRequest request = Instancio.create(ProductUpdateRequest.class);
		request.setCategoryId(2L);

		ProductEntity product = Instancio.of(ProductEntity.class)
				.set(field(ProductEntity::getId), 1L)
				.set(field(ProductEntity::getDeleted), CREATED)
				.create();

		ProductResponse mapped = Instancio.create(ProductResponse.class);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		doNothing().when(mapper).updateEntityFromRequest(request, product);
		when(productRepository.save(product)).thenReturn(product);
		when(mapper.entityToResponse(product, 10)).thenReturn(mapped);

		// When
		ProductResponse result = productService.update(1L, request, 10);

		// Then
		assertNotNull(result);
		assertEquals(2L, product.getCategory().getId());
	}


	@Test
	void givenValidRequest_whenUpdateStock_thenIncreaseStock() {

		// Given
		ProductEntity product = Instancio.of(ProductEntity.class)
				.set(field(ProductEntity::getStock), 10D)
				.set(field(ProductEntity::getDeleted), CREATED)
				.create();

		ProductUpdateStockRequest request = new ProductUpdateStockRequest(5D);

		ProductResponse mapped = Instancio.of(ProductResponse.class)
				.set(field(ProductResponse::getStock), 15D)
				.create();

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productRepository.save(product)).thenReturn(product);
		when(mapper.entityToResponse(product, 10)).thenReturn(mapped);

		// When
		ProductResponse result = productService.updateStock(1L, request, 10);

		// Then
		assertEquals(15D, result.getStock());
	}

	@Test
	void givenValidId_whenDelete_thenProductIsMarkedAsDeleted() {

		// Given
		ProductEntity product = Instancio.of(ProductEntity.class)
				.set(field(ProductEntity::getDeleted), CREATED)
				.create();

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productRepository.save(product)).thenReturn(product);

		// When
		productService.delete(1L, 10);

		// Then
		assertEquals(DELETED, product.getDeleted());
		verify(productRepository).save(product);
	}
}
