package fr.mspr.retailer.service;

import fr.mspr.retailer.data.dto.CustomerDTO;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.utils.mapper.ListToPage;
 import fr.mspr.retailer.utils.mapper.ProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileMapper profileMapper;


     public boolean activeAccount(long id) {
        Optional<Profile> optionalProfile = profileRepository.findById(id);

        if (optionalProfile.isEmpty()) {
            throw new IllegalArgumentException("");
        }

        Profile profile = optionalProfile.get();

        if (profile.isActive()) {
            throw new IllegalArgumentException("");
        }
        profile.setActive(true);
        profileRepository.save(profile);
        return true;
    }

    public CustomerDTO getProfileById(long id) {
        Profile profile = profileRepository.findById(id).orElse(null);
        return profileMapper.toDTO(profile);
    }

    public Page<CustomerDTO> getProfils(Pageable pageable) {
        Page<Profile> profiles = profileRepository.findAll(pageable);
        List<CustomerDTO> customerDTOS = profileMapper.toDTOs(profiles.getContent());
        return (Page<CustomerDTO>) ListToPage.toPage(customerDTOS, pageable, profiles.getTotalElements());
    }

    public CustomerDTO updateProfile(CustomerDTO customerDTO) {
        Optional<Profile> optionalProfile = profileRepository.findById(customerDTO.getId());
        if (optionalProfile.isEmpty()){
            CustomerDTO res = new CustomerDTO();
            res.setHasError(true);
            res.setErrorMessage("User is not in database");
            return res;
        }
        Profile profile = optionalProfile.get();
        Profile save = profileRepository.save(profileMapper.updateProfileFromDTO(customerDTO,profile));
        return profileMapper.toDTO(save);
    }

    public boolean deleteProfile(long id) {
        Optional<Profile> profileOptional = profileRepository.findById(id);
        if(profileOptional.isEmpty()){
            return false;
        }
        profileRepository.deleteById(id);
        return true;
    }
}
