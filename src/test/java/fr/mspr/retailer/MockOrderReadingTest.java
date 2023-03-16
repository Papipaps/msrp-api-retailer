package fr.mspr.retailer;

import fr.mspr.retailer.controller.OrderController;
import fr.mspr.retailer.data.dto.OrderDTO;
import fr.mspr.retailer.data.dto.ProductDTO;
import fr.mspr.retailer.data.model.ProductDetails;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.repository.OrderRepository;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.token.ConfirmationToken;
import fr.mspr.retailer.security.token.ConfirmationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderController.class)
public class MockOrderReadingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderController orderController;

    @MockBean
    private ProfileRepository profileRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ConfirmationTokenRepository confirmationTokenRepository;

    private OrderDTO order5;

    @BeforeEach
    public void setUp() {
        order5 = OrderDTO.builder()
                .createdAt(LocalDateTime.parse("2023-02-19T14:01:50.652"))
                .id(5L)
                .customerId(5L)
                .build();
    }

    @Test
    public void getOrders_GivenCustomerWithId5_returnsOrdersSuccessfully() throws Exception {
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
        MvcResult mvcResult = mockMvc.perform(get("/api/retailer/order/mock/customer/5")
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andReturn();
        // THEN
        String response = mvcResult.getResponse().getContentAsString();
        assertFalse(response.isEmpty(), "Response should not be empty");
    }

    @Test
    public void getProducts_givenCustomer6AndOrder6_returnsListOfProducts() throws Exception {
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
        MvcResult mvcResult = mockMvc.perform(get("/api/retailer/order/mock/customer/6/order/6")
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andReturn();
        // THEN
        String response = mvcResult.getResponse().getContentAsString();
        assertFalse(response.isEmpty(), "Response should not be empty");
    }
}
