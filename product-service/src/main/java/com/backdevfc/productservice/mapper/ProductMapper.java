package com.backdevfc.productservice.mapper;

import com.backdevfc.productservice.model.dto.request.ProductRequest;
import com.backdevfc.productservice.model.dto.request.ProductUpdateRequest;
import com.backdevfc.productservice.model.dto.response.ProductResponse;
import com.backdevfc.productservice.model.entity.ProductEntity;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {


	@Mapping(source = "product.category.id", target = "categoryId")
	@Mapping(source = "port", target = "port")
	ProductResponse entityToResponse(ProductEntity product, Integer port);


	@Mapping(source = "categoryId", target = "category.id")
	ProductEntity requestToEntity(ProductRequest request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "deleted", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "updatedDate", ignore = true)
	@Mapping(target = "category", ignore = true)
	void updateEntityFromRequest(ProductUpdateRequest request, @MappingTarget ProductEntity entity);
}