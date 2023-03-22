package fr.mspr.retailer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.mspr.retailer.controller.ProductController;
import fr.mspr.retailer.data.dto.ProductDTO;
import fr.mspr.retailer.data.model.ProductDetails;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.token.ConfirmationToken;
import fr.mspr.retailer.security.token.ConfirmationTokenRepository;
import fr.mspr.retailer.service.ProductService;
import fr.mspr.retailer.utils.AuthorizationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
public class MockProductReadingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductController productController;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProfileRepository profileRepository;
    @MockBean
    private AuthorizationHelper authHelper;

    @MockBean
    private ConfirmationTokenRepository confirmationTokenRepository;

    private ProductDTO expectedProduct;

    @BeforeEach
    public void setUp() {
        long id = 6;
        LocalDateTime of = LocalDateTime.of(2023, 2, 19, 13, 42, 19);
        expectedProduct = ProductDTO.builder()
                .id(id)
                .name("Rex Bailey")
                .createdAt(of)
                .stock(12059)
                .details(new ProductDetails(
                        659.00F,
                        "The Nagasaki Lander is the trademarked name of several series of" +
                                " Nagasaki sport bikes, that started with the 1984 ABC800J",
                        "red"))
                .build();
    }

    @Test
    public void getProduct_returnsProduct() throws Exception {
        //GIVEN
        when(confirmationTokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(ConfirmationToken.builder()
                        .token("valid-token")
                        .confirmedAt(LocalDateTime.now())
                        .profile(Profile.builder()
                                .isActive(true)
                                .build())
                        .build()));

        //THEN
        MvcResult mvcResult = mockMvc.perform(get("/api/retailer/product/mock/get/" + expectedProduct.getId())
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andReturn();
        // THEN
        String response = mvcResult.getResponse().getContentAsString();
        assertFalse(response.isEmpty(), "Response should not be empty");
    }

    @Test
    public void getProducts_returnsListOfProducts() throws Exception {
        //GIVEN
        when(confirmationTokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(ConfirmationToken.builder()
                        .token("valid-token")
                        .confirmedAt(LocalDateTime.now())
                        .profile(Profile.builder()
                                .isActive(true)
                                .build())
                        .build()));

        //THEN
        MvcResult mvcResult = mockMvc.perform(get("/api/retailer/product/mock/list")
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andReturn();
        // THEN
        String response = mvcResult.getResponse().getContentAsString();
        assertFalse(response.isEmpty(), "Response should not be empty");
    }
}
