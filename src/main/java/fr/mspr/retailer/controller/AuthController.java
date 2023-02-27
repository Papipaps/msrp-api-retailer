package fr.mspr.retailer.controller;

import com.google.zxing.WriterException;
import fr.mspr.retailer.data.dto.BaseDTO;
import fr.mspr.retailer.data.dto.CustomerDTO;
import fr.mspr.retailer.data.dto.RegistrationDTO;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.data.model.RoleEnum;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.token.ConfirmationToken;
import fr.mspr.retailer.security.token.ConfirmationTokenService;
import fr.mspr.retailer.service.EmailSenderService;
import fr.mspr.retailer.utils.QRCodeUtils;
import fr.mspr.retailer.utils.exception.ValidationExceptionHandler;
import fr.mspr.retailer.utils.mapper.ProfileMapper;
import fr.mspr.retailer.utils.mapper.RegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private RegistrationMapper registrationMapper;

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationDTO registrationDTO) throws IOException, WriterException {

        boolean userNameExists = profileRepository.findByUsername(registrationDTO.getUsername()).isPresent();
        boolean emailExists = profileRepository.findByEmail(registrationDTO.getEmail()).isPresent();
        if (userNameExists
                || emailExists) {
            String errorMsg = String.format("user with %s : %s already exists"
                    , emailExists ? "email" : "username"
                    , emailExists ? registrationDTO.getEmail() : registrationDTO.getUsername());
            return ResponseEntity.badRequest().body(new BaseDTO(true, errorMsg, null));
        }
        String token = UUID.randomUUID().toString();
        Profile profile = profileRepository.save(registrationMapper.toProfile(registrationDTO));

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .confirmedAt(null)
                .profile(profile)
                .build();

        ConfirmationToken savedToken = confirmationTokenService.saveConfirmationToken(confirmationToken);
        String link = "http://localhost:8081/api/auth/confirm?token=" + savedToken.getToken();
        emailSenderService.send(
                profile.getEmail()
                , emailSenderService.buildEmail(profile.getFirstName(), link, QRCodeUtils.generateQRcode(link, "utf-8", 300, 300))
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @GetMapping("qr")
    public ResponseEntity<?> generateQR() throws IOException, WriterException {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(QRCodeUtils.generateQRcode("hello", "utf-8", 300, 300));
    }

    @GetMapping("confirm")
    public String confirmToken(@RequestParam String token) {
        return confirmationTokenService.confirmToken(token);
    }
}
