package fr.mspr.retailer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.mspr.retailer.data.dto.OrderDTO;
import fr.mspr.retailer.data.dto.ProductDTO;
import fr.mspr.retailer.data.model.Order;
import fr.mspr.retailer.repository.OrderRepository;
import fr.mspr.retailer.repository.ProfileRepository;
//import fr.mspr.retailer.utils.mapper.OrderCustomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequestMapping("api/retailer/order")
@RestController
public class OrderController {
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${app.customers.api-url}")
    private String CUSTOMER_APIURL;

//    @GetMapping("get/{id}")
//    private OrderDTO getOrder(@PathVariable Long id) {
//        Order order = orderRepository.findById(id).orElse(null);
//        return OrderCustomMapper.toDTO(order);
//    }

    @GetMapping("mock/customer/{customerId}")
    private ResponseEntity<OrderDTO[]> getOrdersByUser(@PathVariable Long customerId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseProducts = null;
        String url = String.format("%s/%s/orders", CUSTOMER_APIURL,customerId);
        responseProducts = restTemplate.getForEntity(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            OrderDTO[] orderDTOS = mapper.readValue(responseProducts.getBody(), OrderDTO[].class);
            return ResponseEntity.ok().body(orderDTOS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("mock/customer/{customerId}/order/{id}")
    private ResponseEntity<?> getProductByUserOrder(@PathVariable Long id, @PathVariable Long customerId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseProducts = null;
        String url = String.format("%s/%s/orders/%s/products", CUSTOMER_APIURL,customerId, id);
        try{
            responseProducts = restTemplate.getForEntity(url, String.class);
        }catch (HttpServerErrorException e){
            return ResponseEntity.status(500).body(Map.of("error",true,"message","external API is not responding"));
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            ProductDTO[] productDTOS = mapper.readValue(responseProducts.getBody(), ProductDTO[].class);
            return ResponseEntity.ok().body(productDTOS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
