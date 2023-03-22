package fr.mspr.retailer;

import fr.mspr.retailer.data.dto.Address;
import fr.mspr.retailer.data.dto.Company;
import fr.mspr.retailer.data.dto.CustomerDTO;
import fr.mspr.retailer.data.model.Order;
import fr.mspr.retailer.data.model.Product;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.service.ProfileService;
import fr.mspr.retailer.service.ProfileServiceImpl;
//import fr.mspr.retailer.utils.mapper.OrderCustomMapper;
import fr.mspr.retailer.utils.mapper.ProfileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private Profile profile;
    private CustomerDTO customerDTO;

    @BeforeEach
    public void initEntities() {

        Product product = Product.builder()
                .id(1L)
                .color("bleue")
                .price(10)
                .stock(10)
                .name("nom")
                .description("description")
                .build();

//        Order order = Order.builder()
//                .id(1L)
//                .createdAt(LocalDateTime.now())
//                .profile(profile)
//                .product(product)
//                .quantity(1)
//                .build();

        profile = Profile.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
//                .orders(Set.of(order))
                .build();

        customerDTO = CustomerDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .company(Company.builder()
                        .companyName("EPSI")
                        .build())
                .address(Address.builder()
                        .city("CITY")
                        .postalCode("POSTALCODE")
                        .build())
//                .orders(List.of(OrderCustomMapper.toDTO(order)))
                .build();
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void givenNonExistentProfile_whenActivateAccount_thenThrowIllegalArgumentException() {
        // GIVEN
        long nonExistentId = 1L;
        when(profileRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        assertThrows(IllegalArgumentException.class, () -> {
            profileService.activeAccount(nonExistentId);
        });

        // THEN
        verify(profileRepository).findById(nonExistentId);
    }

    @Test
    public void givenInactiveProfile_whenActivateAccount_thenActivateProfile() {
        // GIVEN
        long inactiveProfileId = 1L;
        Profile inactiveProfile = Profile.builder()
                .id(inactiveProfileId)
                .isActive(false)
                .build();
        when(profileRepository.findById(any())).thenReturn(Optional.of(inactiveProfile));
        Profile activeProfile = Profile.builder()
                .id(inactiveProfileId)
                .isActive(true)
                .build();
        when(profileRepository.save(any())).thenReturn(activeProfile);

        // WHEN
        boolean activated = profileService.activeAccount(inactiveProfileId);

        // THEN
        assertTrue(activated);
        assertTrue(activeProfile.isActive());
        verify(profileRepository).findById(inactiveProfileId);
        verify(profileRepository).save(activeProfile);
    }

    @Test
    public void givenActiveProfile_whenActivateAccount_thenThrowIllegalArgumentException() {
        // GIVEN
        long activeProfileId = 1L;
        Profile activeProfile = Profile.builder()
                .id(activeProfileId)
                .isActive(true)
                .build();
        when(profileRepository.findById(any())).thenReturn(Optional.of(activeProfile));

        // WHEN
        assertThrows(IllegalArgumentException.class, () -> {
            profileService.activeAccount(activeProfileId);
        });

        // THEN
        verify(profileRepository).findById(activeProfileId);
    }

    @Test
    public void givenProfileId_whenGetProfileById_thenReturnCustomerDTO() {
        // Given
        long id = 1L;
        when(profileRepository.findById(any())).thenReturn(Optional.of(profile));
        when(profileMapper.toDTO(any())).thenReturn(customerDTO);

        // When
        CustomerDTO result = profileService.getProfileById(id);

        // Then
        assertEquals(customerDTO, result);
    }

    @Test
    public void givenNonExistingProfileId_whenGetProfileById_thenReturnNull() {
        // Given
        when(profileRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        CustomerDTO result = profileService.getProfileById(1L);

        // Then
        assertNull(result);
    }

    @Test
    public void givenPageable_whenGetProfiles_thenReturnPageOfCustomerDTOs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        Page<Profile> profilePage = new PageImpl<>(profiles, pageable, 1);
        List<CustomerDTO> customerDTOS = new ArrayList<>();
        customerDTOS.add(customerDTO);
        when(profileRepository.findAll(any(Pageable.class))).thenReturn(profilePage);
        when(profileMapper.toDTOs(any())).thenReturn(customerDTOS);

        // When
        Page<CustomerDTO> result = profileService.getProfils(pageable);

        // Then
        assertEquals(customerDTOS, result.getContent());
        assertEquals(profilePage.getTotalElements(), result.getTotalElements());
        assertEquals(pageable, result.getPageable());
    }

    @Test
    public void givenExistingProfile_whenUpdateProfile_thenProfileIsSavedAndReturned() {
        // given
        CustomerDTO updatedCustomerDTO = CustomerDTO.builder()
                .id(profile.getId())
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .build();

        Profile updatedProfile = Profile.builder()
                .id(profile.getId())
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .build();

        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));
        when(profileRepository.save(updatedProfile)).thenReturn(updatedProfile);
        when(profileMapper.updateProfileFromDTO(updatedCustomerDTO, profile)).thenReturn(updatedProfile);
        when(profileMapper.toDTO(updatedProfile)).thenReturn(updatedCustomerDTO);

        // when
        CustomerDTO result = profileService.updateProfile(updatedCustomerDTO);

        // then
        verify(profileRepository).findById(profile.getId());
        verify(profileRepository).save(updatedProfile);
        verify(profileMapper).updateProfileFromDTO(updatedCustomerDTO, profile);
        verify(profileMapper).toDTO(updatedProfile);

        assertFalse(result.isHasError());
        assertNull(result.getErrorMessage());
        assertEquals(updatedCustomerDTO.getId(), result.getId());
        assertEquals(updatedCustomerDTO.getFirstName(), result.getFirstName());
        assertEquals(updatedCustomerDTO.getLastName(), result.getLastName());
        assertEquals(updatedCustomerDTO.getEmail(), result.getEmail());
    }

    @Test
    public void givenNonExistingProfile_whenUpdateProfile_thenErrorIsReturned() {
        // given
        when(profileRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        CustomerDTO result = profileService.updateProfile(customerDTO);

        // then
        verify(profileRepository).findById(profile.getId());
        verifyNoMoreInteractions(profileRepository, profileMapper);

        assertTrue(result.isHasError());
        assertEquals("User is not in database", result.getErrorMessage());
        assertNull(result.getId());
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
        assertNull(result.getEmail());
    }

    @Test
    void deleteProfile_ShouldReturnTrue_WhenProfileExists() {
        // Given

        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));

        // When
        boolean result = profileService.deleteProfile(profile.getId());

        // Then
        assertTrue(result);
        verify(profileRepository, times(1)).deleteById(profile.getId());
    }

    @Test
    void deleteProfile_ShouldReturnFalse_WhenProfileDoesNotExist() {
        // Given
        when(profileRepository.findById(profile.getId())).thenReturn(Optional.empty());

        // When
        boolean result = profileService.deleteProfile(profile.getId());

        // Then
        assertFalse(result);
        verify(profileRepository, never()).deleteById(profile.getId());
    }


}

