package fr.mspr.retailer;

import com.google.zxing.WriterException;
import fr.mspr.retailer.controller.AuthController;
import fr.mspr.retailer.data.dto.OrderDTO;
import fr.mspr.retailer.data.dto.RegistrationDTO;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.data.model.RoleEnum;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.token.ConfirmationToken;
import fr.mspr.retailer.security.token.ConfirmationTokenService;
import fr.mspr.retailer.service.EmailSenderServiceImpl;
import fr.mspr.retailer.utils.mapper.RegistrationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthControllerTest {

    private static final String USERNAME = "johndoe";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "johndoe@example.com";
    private static final String POSTALCODE = "12345";
    private static final String CITY = "City";
    private static final String COMPANYNAME = "Company";
    private static final String VALID_TOKEN = "valid-token";

    @InjectMocks
    private AuthController authController;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private RegistrationMapper registrationMapper;

    @Mock
    private EmailSenderServiceImpl emailSenderService;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    private RegistrationDTO registrationDTO;

    private Profile profile;
    private Profile unregisteredProfile;

    private ConfirmationToken savedToken;


    @BeforeEach
    public void initEntities() {

        registrationDTO = RegistrationDTO.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .city(CITY)
                .companyName(COMPANYNAME)
                .postalCode(POSTALCODE)
                .build();

        profile = Profile.builder()
                .id(1L)
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .city(CITY)
                .companyName(COMPANYNAME)
                .postalCode(POSTALCODE)
                .roles(RoleEnum.ROLE_USER)
                .isActive(true)
//                .orders(Set.of(OrderDTO.builder().build()))
                .build();
        unregisteredProfile = Profile.builder()
                .id(1L)
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .city(CITY)
                .companyName(COMPANYNAME)
                .postalCode(POSTALCODE)
                .roles(null)
                .isActive(false)
//                .orders(new HashSet<>())
                .build();

        savedToken = ConfirmationToken.builder()
                .token(VALID_TOKEN)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .confirmedAt(null)
                .profile(profile)
                .build();
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenRegistrationDTO_whenRegisterUser_thenSuccess() throws IOException, WriterException  {
        // given
        when(profileRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(profileRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(profileRepository.save(unregisteredProfile)).thenReturn(profile);
        when(registrationMapper.toProfile(registrationDTO)).thenReturn(unregisteredProfile);

        when(emailSenderService.buildEmail(eq(profile.getFirstName()), anyString(), any(byte[].class))).thenReturn("success");
        when(confirmationTokenService.saveConfirmationToken(any(ConfirmationToken.class))).thenReturn(savedToken);

        // when
        ResponseEntity<?> responseEntity = authController.registerUser(registrationDTO);

        // then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);

        Map<String, Profile> responseBody = (Map<String, Profile>) responseEntity.getBody();

        assertNotNull(responseBody);
        assertNotNull(responseBody.get("token"));
        assertEquals(savedToken.getProfile(), responseBody.get("retailer"));
    }

    @Test
    public void givenRegistrationDTO_whenRegisterUser_thenBadRequest() throws IOException, WriterException {
        // given

        when(profileRepository.findByUsername(USERNAME)).thenReturn(Optional.of(profile));
        when(profileRepository.findByEmail(EMAIL)).thenReturn(Optional.of(profile));

        // when
        ResponseEntity<?> responseEntity = authController.registerUser(registrationDTO);

        // then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

}

