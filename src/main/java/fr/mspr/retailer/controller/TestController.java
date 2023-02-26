package fr.mspr.retailer.controller;

import fr.mspr.retailer.data.dto.CustomerDTO;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.utils.mapper.ProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/test")
@RestController
public class TestController {
    @Autowired
    private ProfileRepository profileRepository;

    @GetMapping("get/{id}")
    private CustomerDTO getUser(@PathVariable Long id) {
        return ProfileMapper.toCustomerDTO(profileRepository.findById(id).get());
    }

}
