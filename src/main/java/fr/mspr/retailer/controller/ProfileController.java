package fr.mspr.retailer.controller;

import fr.mspr.retailer.data.dto.CustomerDTO;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.service.ProductService;
import fr.mspr.retailer.service.ProfileService;
import fr.mspr.retailer.utils.AuthorizationHelper;
import fr.mspr.retailer.utils.mapper.ProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.util.Map;

@RestController
@RequestMapping("api/retailer/profile")
public class ProfileController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private AuthorizationHelper authorizationHelper;

    @GetMapping("get/{id}")
    public ResponseEntity<CustomerDTO> getProfileById(@PathVariable long id) {
        CustomerDTO product = profileService.getProfileById(id);
        return ResponseEntity.ok().body(product);
    }

    @GetMapping("getInfo")
    public ResponseEntity<CustomerDTO> getProfile(ServletRequest request) {
        Profile loggedprofile = authorizationHelper.getProfileFromToken(request);
        return ResponseEntity.ok().body(profileMapper.toDTO(loggedprofile));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<?>> listProfiles(
            @RequestParam(required = false, defaultValue = "9") int size
            , @RequestParam(required = false, defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerDTO> customerDTOS = profileService.getProfils(pageable);
        return ResponseEntity.ok().body(customerDTOS);

    }

    @PatchMapping("update")
    public ResponseEntity<CustomerDTO> updateProfile(@RequestBody CustomerDTO customerDTO) {
        CustomerDTO product = profileService.updateProfile(customerDTO);
        return ResponseEntity.ok().body(product);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable long id, ServletRequest request) {
        Profile loggedprofile = authorizationHelper.getProfileFromToken(request);
        boolean isAdmin = authorizationHelper.isAdmin(loggedprofile.getId());
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error:", "You're not allowed to do this operation"));
        }
        boolean b = profileService.deleteProfile(id);
        return ResponseEntity.ok().body(b);
    }
}
