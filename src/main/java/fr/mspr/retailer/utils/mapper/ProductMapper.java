package fr.mspr.retailer.utils.mapper;

import fr.mspr.retailer.data.dto.ProductDTO;
import fr.mspr.retailer.data.model.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "price", source = "details.price")
    @Mapping(target = "description", source = "details.description")
    @Mapping(target = "color", source = "details.color")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product updateProductFromDTO(ProductDTO productDTO, @MappingTarget Product product);

    @Mapping(source = "price", target = "details.price")
    @Mapping(source = "description", target = "details.description")
    @Mapping(source = "color", target = "details.color")
    ProductDTO toDTO(Product product);

    @Mapping(target = "price", source = "details.price")
    @Mapping(target = "description", source = "details.description")
    @Mapping(target = "color", source = "details.color")
    Product toEntity(ProductDTO product);

    @Mapping(source = "price", target = "details.price")
    @Mapping(source = "description", target = "details.description")
    @Mapping(source = "color", target = "details.color")
    List<ProductDTO> toDTOs(List<Product> content);

    @Mapping(target = "price", source = "details.price")
    @Mapping(target = "description", source = "details.description")
    @Mapping(target = "color", source = "details.color")
    List<Product> toEntities(List<ProductDTO> products);
}
