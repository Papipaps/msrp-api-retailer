package fr.mspr.retailer;

import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.data.model.RoleEnum;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.token.ConfirmationToken;
import fr.mspr.retailer.security.token.ConfirmationTokenRepository;
import fr.mspr.retailer.security.token.ConfirmationTokenService;
import fr.mspr.retailer.service.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TokenServiceTest {

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



    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private ProfileServiceImpl profileService;
    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    private Profile registeredProfile;
    private Profile inactiveProfile;

    private ConfirmationToken notconfirmedToken;
    private ConfirmationToken expiredToken;
    private ConfirmationToken confirmedToken;
    private HttpServletResponse response;


    @BeforeEach
    public void initEntities() {

        response = mock(HttpServletResponse.class);

        registeredProfile = Profile.builder()
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

        inactiveProfile = Profile.builder()
                .id(2L)
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .city(CITY)
                .companyName(COMPANYNAME)
                .postalCode(POSTALCODE)
                .roles(null)
                .isActive(false)
                .orders(new HashSet<>())
                .build();

        notconfirmedToken = ConfirmationToken.builder()
                .token(VALID_TOKEN)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .confirmedAt(null)
                .profile(inactiveProfile)
                .build();

        confirmedToken = ConfirmationToken.builder()
                .token(VALID_TOKEN)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .confirmedAt(LocalDateTime.now().plusDays(1))
                .profile(registeredProfile)
                .build();

        expiredToken = ConfirmationToken.builder()
                .token(VALID_TOKEN)
                .createdAt(LocalDateTime.now().minusDays(5))
                .expiresAt(LocalDateTime.now().minusDays(4))
                .confirmedAt(null)
                .profile(inactiveProfile)
                .build();
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testConfirmToken() {
        Profile inactiveProfile = Profile.builder()
                .id(1L)
                .isActive(false)
                .build();
        Profile activeProfile = Profile.builder()
                .id(1L)
                .isActive(true)
                .build();
        ConfirmationToken confirmedToken = ConfirmationToken.builder()
                .token(VALID_TOKEN)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .confirmedAt(LocalDateTime.now())
                .profile(inactiveProfile)
                .build();;
        // Given
        when(confirmationTokenRepository.findByToken(VALID_TOKEN)).thenReturn(Optional.of(notconfirmedToken));
        when(profileRepository.findById(anyLong())).thenReturn(Optional.of(notconfirmedToken.getProfile()));
        when(profileService.activeAccount(confirmedToken.getProfile().getId())).thenReturn(true);
        when(profileRepository.save(notconfirmedToken.getProfile())).thenReturn(activeProfile);
        when(confirmationTokenRepository.save(notconfirmedToken)).thenReturn(confirmedToken);

        // When
        String result = confirmationTokenService.confirmToken(VALID_TOKEN, response);

        // Then
        assertEquals("confirmed", result);
        verify(response).setHeader(eq("token"),anyString());
    }

    @Test
    public void testConfirmTokenWithExpiredToken() {
        when(confirmationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(expiredToken));

        // When/Then
        assertThrows(IllegalStateException.class, () -> confirmationTokenService.confirmToken(INVALID_TOKEN, response));

        verify(confirmationTokenRepository).findByToken(INVALID_TOKEN);
    }

    @Test
    public void testConfirmTokenWithAlreadyConfirmedToken() {
        // Given
        when(confirmationTokenRepository.findByToken(INVALID_TOKEN)).thenReturn(Optional.of(confirmedToken));

        // When/Then
        assertThrows(IllegalStateException.class, () -> confirmationTokenService.confirmToken(INVALID_TOKEN, response));

        verify(confirmationTokenRepository).findByToken(INVALID_TOKEN);

    }

    @Test
    public void testConfirmTokenWithInvalidToken() {
        // Given

        when(confirmationTokenRepository.findByToken(INVALID_TOKEN)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(IllegalStateException.class, () -> confirmationTokenService.confirmToken(INVALID_TOKEN, response));

        verify(confirmationTokenRepository).findByToken(INVALID_TOKEN);
    }

    @Test
    void update_shouldUpdateTokenSuccessfully() {
        // GIVEN
        String newToken = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";

        ConfirmationToken oldToken = ConfirmationToken.builder()
                .token("old_token")
                .createdAt(LocalDateTime.now().minusDays(10))
                .expiresAt(LocalDateTime.now().minusDays(8))
                .confirmedAt(LocalDateTime.now().minusDays(9))
                .updatedAt(null)
                .profile(registeredProfile)
                .build();
        ConfirmationToken updatedToken = ConfirmationToken.builder()
                .token(newToken)
                .createdAt(LocalDateTime.now().minusDays(10))
                .expiresAt(LocalDateTime.now().plusDays(7))
                .confirmedAt(null)
                .updatedAt(LocalDateTime.now())
                .profile(inactiveProfile)
                .build();

        when(confirmationTokenRepository.findByProfileId(registeredProfile.getId()))
                .thenReturn(Optional.of(oldToken));
        when(confirmationTokenRepository.findByToken(newToken))
                .thenReturn(Optional.empty());
        when(confirmationTokenRepository.save(any(ConfirmationToken.class)))
                .thenReturn(updatedToken);

        // WHEN
        ConfirmationToken result = confirmationTokenService.update(newToken, registeredProfile);

        // THEN
        verify(confirmationTokenRepository).findByProfileId(registeredProfile.getId());
        verify(confirmationTokenRepository).findByToken(newToken);
        verify(confirmationTokenRepository).save(any(ConfirmationToken.class));
        assertAll(
                () -> assertEquals(updatedToken.getToken(), result.getToken()),
                () -> assertEquals(updatedToken.getCreatedAt(), result.getCreatedAt()),
                () -> assertEquals(updatedToken.getExpiresAt(), result.getExpiresAt()),
                () -> assertEquals(updatedToken.getUpdatedAt(), result.getUpdatedAt()),
                () -> assertNull(result.getConfirmedAt())
        );
    }

    @Test
    void update_shouldThrowExceptionForSameToken() {
        // GIVEN
        String newToken = "old_token";
        ConfirmationToken oldToken = ConfirmationToken.builder()
                .token(newToken)
                .createdAt(LocalDateTime.now().minusDays(1))
                .confirmedAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .profile(registeredProfile)
                .build();

        when(confirmationTokenRepository.findByProfileId(registeredProfile.getId()))
                .thenReturn(Optional.of(oldToken));
        when(confirmationTokenRepository.findByToken(newToken))
                .thenReturn(Optional.of(oldToken));

        // WHEN
        Throwable exception = assertThrows(IllegalStateException.class,
                () -> confirmationTokenService.update(newToken, registeredProfile));

        // THEN
        assertEquals("New token cannot be the same as previous", exception.getMessage());
        verify(confirmationTokenRepository).findByProfileId(registeredProfile.getId());
        verify(confirmationTokenRepository).findByToken(newToken);
        verifyNoMoreInteractions(confirmationTokenRepository);
    }

}

