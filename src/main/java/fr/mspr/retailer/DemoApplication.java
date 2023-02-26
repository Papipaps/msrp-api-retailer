package fr.mspr.retailer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.mspr.retailer.data.dto.CustomerDTO;
import fr.mspr.retailer.data.dto.ProductDTO;
import fr.mspr.retailer.data.model.Order;
import fr.mspr.retailer.data.model.Product;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.data.model.RoleEnum;
import fr.mspr.retailer.repository.OrderRepository;
import fr.mspr.retailer.repository.ProductRepository;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("api/retailer")
public class DemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		initUsers();
	}

	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Value("${app.customers.api-url}")
	private String CUSTOMER_APIURL;
	@Value("${app.products.api-url}")
	private String PRODUCT_APIURL;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OrderRepository orderRepository;
	private void initUsers() {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseCustomers = restTemplate.getForEntity(CUSTOMER_APIURL, String.class);
		ResponseEntity<String> responseProducts = restTemplate.getForEntity(PRODUCT_APIURL, String.class);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			CustomerDTO[] customersList = mapper.readValue(responseCustomers.getBody(), CustomerDTO[].class);
			ProductDTO[] productDTOS = mapper.readValue(responseProducts.getBody(), ProductDTO[].class);


			Arrays.asList(productDTOS).forEach(dto -> {
				Product product = Product.builder()
						.id(dto.getId())
						.name(dto.getName())
						.price(dto.getDetails().getPrice())
						.stock(dto.getStock())
						.color(dto.getDetails().getColor())
						.description(dto.getDetails().getDescription())
						.createdAt(dto.getCreatedAt())
						.stock(dto.getStock())
						.build();
				productRepository.save(product);
			});

			Arrays.asList(customersList).forEach(dto -> {
				ArrayList<Order> dtoOrders = dto.getOrders();
				List<Order> orders = dtoOrders == null || dtoOrders.isEmpty() ? null : orderRepository.saveAll(dtoOrders);
				Profile profile = Profile.builder()
						.username(dto.getUsername())
						.password(new BCryptPasswordEncoder().encode("secret"))
						.roles(RoleEnum.USER)
						.orders(orders)
						.createdAt(dto.getCreatedAt())
						.firstName(dto.getFirstName())
						.lastName(dto.getLastName())
						.postalCode(dto.getAddress().getPostalCode())
						.city(dto.getAddress().getCity())
						.companyName(dto.getCompany().getCompanyName())
						.build();
				profileRepository.save(profile);
			});
			System.out.println(customersList[0] + " " + productDTOS[0]);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
