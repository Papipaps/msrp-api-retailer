package fr.mspr.retailer;

import com.google.zxing.WriterException;
import fr.mspr.retailer.controller.AuthController;
import fr.mspr.retailer.data.dto.RegistrationDTO;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.data.model.RoleEnum;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.token.ConfirmationToken;
import fr.mspr.retailer.security.token.ConfirmationTokenRepository;
import fr.mspr.retailer.security.token.ConfirmationTokenService;
import fr.mspr.retailer.service.EmailSenderService;
import fr.mspr.retailer.service.EmailSenderServiceImpl;
import fr.mspr.retailer.service.ProfileServiceImpl;
import fr.mspr.retailer.utils.mapper.RegistrationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    private static final String INVALID_TOKEN = "invalid-token";
    private static final String TOKEN_UPDATED = "updated-token";

    @InjectMocks
    private AuthController authController;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private RegistrationMapper registrationMapper;

    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private ProfileServiceImpl profileService;
    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private EmailSenderServiceImpl emailSenderService;

    private RegistrationDTO registrationDTO;

    private Profile profile;

    private ConfirmationToken notconfirmedToken;
    private ConfirmationToken expiredToken;
    private ConfirmationToken confirmedToken;
    private HttpServletResponse response;


    @BeforeEach
    public void initEntities(){

        response = mock(HttpServletResponse.class);

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
                .orders(new HashSet<>())
                .build();

        notconfirmedToken = ConfirmationToken.builder()
                .token(VALID_TOKEN)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .confirmedAt(null)
                .profile(Profile.builder()
                        .isActive(false)
                        .build())
                .build();

        confirmedToken = ConfirmationToken.builder()
                .token(VALID_TOKEN)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .confirmedAt(LocalDateTime.now().plusDays(1))
                .profile(profile)
                .build();

        expiredToken = ConfirmationToken.builder()
                .token(VALID_TOKEN)
                .createdAt(LocalDateTime.now().minusDays(5))
                .expiresAt(LocalDateTime.now().minusDays(4))
                .confirmedAt(null)
                .profile(Profile.builder()
                        .isActive(false)
                        .build())
                .build();
    }
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenRegistrationDTO_whenRegisterUser_thenSuccess() throws IOException, WriterException {
        // given
        when(profileRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(profileRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(registrationMapper.toProfile(registrationDTO)).thenReturn(profile);
        when(confirmationTokenService.saveConfirmationToken(any())).thenReturn(notconfirmedToken);

        // when
        ResponseEntity<?> responseEntity = authController.registerUser(registrationDTO);

        // then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);

        Map<String, Profile> responseBody = (Map<String, Profile>) responseEntity.getBody();

        assertNotNull(responseBody);
        assertNotNull(responseBody.get("token"));
        assertEquals(notconfirmedToken.getProfile(),responseBody.get("retailer"));
    }

    @Test
    public void givenRegistrationDTO_whenRegisterUser_thenBadRequest() throws IOException, WriterException {
        // given

        when(profileRepository.findByUsername(USERNAME)).thenReturn(Optional.of(profile));
        when(profileRepository.findByEmail(EMAIL)).thenReturn(Optional.of(profile))      ;

        // when
        ResponseEntity<?> responseEntity = authController.registerUser(registrationDTO);

        // then
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testConfirmToken() {
        // Given
        when(confirmationTokenRepository.findByToken(VALID_TOKEN)).thenReturn(Optional.of(notconfirmedToken));
        when(profileRepository.findById(anyLong())).thenReturn(Optional.of(notconfirmedToken.getProfile()));
        when(profileService.activeAccount(anyLong())).thenReturn(true);
        when(confirmationTokenService.saveConfirmationToken(notconfirmedToken)).thenReturn(notconfirmedToken);

        // When
        String result = confirmationTokenService.confirmToken(VALID_TOKEN, response);

        // Then
        assertEquals("confirmed", result);
        assertNotNull(response.getHeader("token"));
    }

    @Test
    public void testConfirmTokenWithExpiredToken() {
        when(confirmationTokenRepository.findByToken(INVALID_TOKEN)).thenReturn(Optional.of(expiredToken));

        // When/Then
        assertThrows(IllegalStateException.class, () -> authController.confirmToken(INVALID_TOKEN, response));
    }

    @Test
    public void testConfirmTokenWithAlreadyConfirmedToken() {
        // Given
        when(confirmationTokenRepository.findByToken(INVALID_TOKEN)).thenReturn(Optional.of(confirmedToken));

        // When/Then
        assertThrows(IllegalStateException.class, () -> authController.confirmToken(INVALID_TOKEN, response));
    }

    @Test
    public void testConfirmTokenWithInvalidToken() {
        // Given

        when(confirmationTokenRepository.findByToken(INVALID_TOKEN)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(IllegalStateException.class, () -> authController.confirmToken(INVALID_TOKEN, response));
    }

}

