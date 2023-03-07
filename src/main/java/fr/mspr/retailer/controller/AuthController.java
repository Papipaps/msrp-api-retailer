package fr.mspr.retailer.controller;

import com.google.zxing.WriterException;
import fr.mspr.retailer.data.dto.BaseDTO;
import fr.mspr.retailer.data.dto.RegistrationDTO;
import fr.mspr.retailer.data.model.Order;
import fr.mspr.retailer.data.model.Product;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.data.model.RoleEnum;
import fr.mspr.retailer.repository.OrderRepository;
import fr.mspr.retailer.repository.ProductRepository;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.token.ConfirmationToken;
import fr.mspr.retailer.security.token.ConfirmationTokenService;
import fr.mspr.retailer.service.EmailSenderService;
import fr.mspr.retailer.utils.AuthorizationHelper;
import fr.mspr.retailer.utils.QRCodeUtils;
import fr.mspr.retailer.utils.mapper.RegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private RegistrationMapper registrationMapper;
    @Autowired
    private AuthorizationHelper authorizationHelper;

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationDTO registrationDTO) throws IOException, WriterException {

        boolean userNameExists = profileRepository.findByUsername(registrationDTO.getUsername()).isPresent();
        boolean emailExists = profileRepository.findByEmail(registrationDTO.getEmail()).isPresent();

        if (userNameExists || emailExists) {
            String errorMsg = String.format("user with %s : %s already exists"
                    , emailExists ? "email" : "username"
                    , emailExists ? registrationDTO.getEmail() : registrationDTO.getUsername());
            return ResponseEntity.badRequest().body(new BaseDTO(true, errorMsg, null));
        }


        Profile profile = registrationMapper.toProfile(registrationDTO);
        profile.setRoles(RoleEnum.ROLE_USER);
        profile.setCreatedAt(LocalDateTime.now());

        String token = UUID.randomUUID().toString();
        Profile save = profileRepository.save(profile);
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .confirmedAt(null)
                .profile(save)
                .build();

        ConfirmationToken savedToken = confirmationTokenService.saveConfirmationToken(confirmationToken);
        String link = "http://localhost:8081/api/auth/confirm?token=" + savedToken.getToken();
        emailSenderService.send(
                profile.getEmail()
                , emailSenderService.buildEmail(profile.getFirstName(), link, QRCodeUtils.generateQRcode(link, "utf-8", 300, 300))
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("token", token, "retailer", savedToken.getProfile()));
    }

    @GetMapping("confirm")
    public String confirmToken(@RequestParam String token, HttpServletResponse response) {
        return confirmationTokenService.confirmToken(token, response);
    }

    @PutMapping("token/update")
    public ResponseEntity<?> updateToken(@RequestParam(required = false, defaultValue = "") String newToken,
                                         @RequestParam String email,
                                         ServletRequest request) throws IOException, WriterException {
        Profile adminProfile = authorizationHelper.getProfileFromToken(request);
        boolean isAdmin = authorizationHelper.isAdmin(adminProfile.getId());

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error:", "You're not allowed to do this operation"));
        }

        newToken = newToken.replaceAll("[^A-Za-z0-9-]", "");

        if (newToken.length() > 0 && newToken.length() < 16 || newToken.length() > 64) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error:", "token length should between 16 and 64 characters"));
        }

        Optional<Profile> profileOpt = profileRepository.findByEmail(email);

        if (profileOpt.isPresent()) {

            Profile profile = profileOpt.get();

            ConfirmationToken updatedToken = confirmationTokenService.update(newToken, profile);

            String link = "http://localhost:8081/api/auth/confirm?token=" + updatedToken.getToken();
            emailSenderService.send(
                    profile.getEmail()
                    , emailSenderService.buildEmail(profile.getFirstName(), link, QRCodeUtils.generateQRcode(link, "utf-8", 300, 300))
            );
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "token updated successfully", "token", updatedToken.getToken()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "user with given mail does not exist"));
    }

}
