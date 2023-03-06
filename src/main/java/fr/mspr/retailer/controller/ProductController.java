package fr.mspr.retailer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.mspr.retailer.data.dto.OrderDTO;
import fr.mspr.retailer.data.dto.ProductDTO;
import fr.mspr.retailer.data.model.Order;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.data.model.RoleEnum;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.token.ConfirmationToken;
import fr.mspr.retailer.security.token.ConfirmationTokenRepository;
import fr.mspr.retailer.service.ProductService;
import fr.mspr.retailer.utils.ListToPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("api/retailer/product")
public class ProductController {
    @Value("${app.products.api-url}")
    private String PRODUCT_APIURL;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;


    @GetMapping("mock/get/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable long id) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s/%s", PRODUCT_APIURL, id);
        ResponseEntity<String> responseProducts = null;
        try {
            responseProducts = restTemplate.getForEntity(url, String.class);
        } catch (HttpServerErrorException e) {
            ProductDTO res = ProductDTO.builder().build();
            res.setErrorMessage(String.format("No product with id %s found", id));
            res.setHasError(true);
            return ResponseEntity.status(500).body(res);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            ProductDTO productDTO = mapper.readValue(responseProducts.getBody(), ProductDTO.class);
            return ResponseEntity.ok().body(productDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("mock/list")
    public ResponseEntity<Page<?>> getProducts(
            @RequestParam(required = false, defaultValue = "9") int size
            , @RequestParam(required = false, defaultValue = "0") int page) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseProducts = null;
        responseProducts = restTemplate.getForEntity(PRODUCT_APIURL, String.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            ProductDTO[] productDTOS = mapper.readValue(responseProducts.getBody(), ProductDTO[].class);
            Pageable pageable = PageRequest.of(page, size);

            return ResponseEntity.ok().body(ListToPage.toPage(Arrays.asList(productDTOS), pageable, productDTOS.length));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("get/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok().body(product);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<?>> listProfucts(
            @RequestParam(required = false, defaultValue = "9") int size
            , @RequestParam(required = false, defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productDTOS = productService.getProducts(pageable);
        return ResponseEntity.ok().body(productDTOS);

    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO productDTO, ServletRequest request) {
        Profile profile = getProfileFromToken(request);
        boolean isAdmin = isAdmin(profile.getId());
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You're not allowed to do this operation");
        }

        ProductDTO res = productService.addProduct(productDTO);
        return ResponseEntity.ok().body(res);
    }

    @PatchMapping("update")
    public ResponseEntity<?> updateProduct(@RequestBody ProductDTO productDTO, ServletRequest request) {
        Profile profile = getProfileFromToken(request);
        boolean isAdmin = isAdmin(profile.getId());
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You're not allowed to do this operation");
        }

        ProductDTO product = productService.updateProduct(productDTO);
        return ResponseEntity.ok().body(product);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteProfuct(@PathVariable long id, ServletRequest request) {
        Profile profile = getProfileFromToken(request);
        boolean isAdmin = isAdmin(profile.getId());
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You're not allowed to do this operation");
        }

        boolean b = productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(b);
    }

//    @PostMapping("placeOrder")
//    public ResponseEntity<Boolean> placeOrder(@RequestBody OrderDTO orderDTO, ServletRequest request) {
//        Profile profile = getProfileFromToken(request);
//        return ResponseEntity.ok().body(productService.placeOrder(orderDTO, profile.getId()));
//
//    }

    private Profile getProfileFromToken(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;

        String apikey = req.getHeader("APIKEY");
        Optional<ConfirmationToken> token = confirmationTokenRepository.findByToken(apikey);
        return token.get().getProfile();
    }

    private boolean isAdmin(Long profileId) {
        if (profileId != null) {
            Optional<Profile> profileOptional = profileRepository.findById(profileId);
            if (profileOptional.isEmpty()) {
                throw new RuntimeException("Error : user not in db");
            }

            Profile profile = profileOptional.get();
            return profile.getRoles() == RoleEnum.ROLE_ADMIN;
        }
        throw new RuntimeException("Error : id cannot be null");

    }
}
