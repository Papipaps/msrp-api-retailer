package fr.mspr.retailer.utils.mapper;

import fr.mspr.retailer.data.dto.CustomerDTO;
import fr.mspr.retailer.data.dto.ProductDTO;
import fr.mspr.retailer.data.model.Product;
import fr.mspr.retailer.data.model.Profile;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(source = "address.postalCode",target = "postalCode")
    @Mapping(source = "address.city",target = "city")
    @Mapping(source = "company.companyName",target = "companyName")
    Profile toEntity(CustomerDTO customerDTO);
    @Mapping(target = "address.postalCode",source = "postalCode")
    @Mapping(target = "address.city",source = "city")
    @Mapping(target = "company.companyName",source = "companyName")
    CustomerDTO toDTO(Profile profile);
    List<Profile> toEntities(List<CustomerDTO> customerDTOS);
    List<CustomerDTO> toDTOs(List<Profile> profiles);
    @Mapping(source = "address.postalCode",target = "postalCode")
    @Mapping(source = "address.city",target = "city")
    @Mapping(source = "company.companyName",target = "companyName")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Profile updateProfileFromDTO(CustomerDTO customerDTO, @MappingTarget Profile profile);

}
