package fr.mspr.retailer.utils.mapper;

import fr.mspr.retailer.data.dto.ProductDTO;
import fr.mspr.retailer.data.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product updateProductFromDTO(ProductDTO productDTO, @MappingTarget Product product);
}
