package fr.mspr.retailer.service;

import fr.mspr.retailer.data.dto.CustomerDTO;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public interface ProfileService {

    boolean activeAccount(long id);

    CustomerDTO getProfileById(long id);

    Page<CustomerDTO> getProfils(Pageable pageable);

    CustomerDTO updateProfile(CustomerDTO customerDTO);

    boolean deleteProfile(long id);

}
