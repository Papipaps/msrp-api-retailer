package fr.mspr.retailer.utils.mapper;

import fr.mspr.retailer.data.dto.RegistrationDTO;
import fr.mspr.retailer.data.model.Profile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {
    Profile toProfile(RegistrationDTO registrationDTO);
}
