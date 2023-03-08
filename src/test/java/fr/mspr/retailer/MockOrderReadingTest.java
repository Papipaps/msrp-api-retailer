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

import java.time.LocalDateTime;
import java.util.Optional;

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

    private ProductDTO expectedProductWithId5;
    private ProductDTO expectedProductWithId55;
    private OrderDTO order5_2;
    private OrderDTO order5;
    private OrderDTO orderDTO;

    @BeforeEach
    public void setUp() {
        expectedProductWithId5 = ProductDTO.builder()
                .id(5L)
                .name("Mr. Felix Homenick DDS")
                .createdAt(LocalDateTime.parse("2023-02-19T17:59:22.648"))
                .stock(49045)
                .details(new ProductDetails(
                        445.00F,
                        "Andy shoes are designed to keeping in mind durability as well as trends," +
                                " the most stylish range of shoes & sandals",
                        "mint green"))
                .build();
        expectedProductWithId55 = ProductDTO.builder()
                .id(55L)
                .name("Joan Rowe")
                .createdAt(LocalDateTime.parse("2023-02-20T06:49:56.880"))
                .stock(94135)
                .details(new ProductDetails(
                        714.00F,
                        "Andy shoes are designed to keeping in mind durability" +
                                " as well as trends, the most stylish range of shoes & sandals",
                        "fuchsia"))
                .build();

        order5 = OrderDTO.builder()
                .createdAt(LocalDateTime.parse("2023-02-19T14:01:50.652"))
                .id(5L)
                .customerId(5L)
                .build();
        order5_2 = OrderDTO.builder()
                .createdAt(LocalDateTime.parse("2023-02-20T11:08:47.866"))
                .id(55L)
                .customerId(5L)
                .build();


    }

    @Test
    public void getOrdersWithId5_returnsOrdersSuccessfully() throws Exception {
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
        mockMvc.perform(get("/api/retailer/order/mock/customer/5")
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(order5.getId()))
                .andExpect(jsonPath("$.[0].customerId").value(order5.getCustomerId()))
                .andExpect(jsonPath("$.[0].createdAt").value(order5.getCreatedAt().toString()))

                .andExpect(jsonPath("$.[1].id").value(order5_2.getId()))
                .andExpect(jsonPath("$.[1].customerId").value(order5_2.getCustomerId()))
                .andExpect(jsonPath("$.[1].createdAt").value(order5_2.getCreatedAt().toString()));
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
        mockMvc.perform(get("/api/retailer/order/mock/customer/5/order/5")
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(expectedProductWithId5.getId()))
                .andExpect(jsonPath("$.[0].name").value(expectedProductWithId5.getName()))
                .andExpect(jsonPath("$.[0].stock").value(expectedProductWithId5.getStock()))
                .andExpect(jsonPath("$.[0].details.price").value(expectedProductWithId5.getDetails().getPrice()))
                .andExpect(jsonPath("$.[0].details.color").value(expectedProductWithId5.getDetails().getColor()))
                .andExpect(jsonPath("$.[0].details.description").value(expectedProductWithId5.getDetails().getDescription()))

                .andExpect(jsonPath("$.[1].id").value(expectedProductWithId55.getId()))
                .andExpect(jsonPath("$.[1].name").value(expectedProductWithId55.getName()))
                .andExpect(jsonPath("$.[1].stock").value(expectedProductWithId55.getStock()))
                .andExpect(jsonPath("$.[1].details.price").value(expectedProductWithId55.getDetails().getPrice()))
                .andExpect(jsonPath("$.[1].details.color").value(expectedProductWithId55.getDetails().getColor()))
                .andExpect(jsonPath("$.[1].details.description").value(expectedProductWithId55.getDetails().getDescription()));
    }
}
