package fr.mspr.retailer;

import fr.mspr.retailer.controller.AuthController;
import fr.mspr.retailer.data.dto.RegistrationDTO;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.service.ProfileService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
