package fr.mspr.retailer.controller;

import com.google.zxing.WriterException;
import fr.mspr.retailer.data.dto.CustomerDTO;
import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.data.model.RoleEnum;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.token.ConfirmationToken;
import fr.mspr.retailer.security.token.ConfirmationTokenService;
import fr.mspr.retailer.service.EmailSenderService;
import fr.mspr.retailer.utils.QRCodeUtils;
import fr.mspr.retailer.utils.mapper.ProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
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

    @PostMapping("register")
    public String registerUser() throws IOException, WriterException {

        String token = UUID.randomUUID().toString();

        Profile profile = profileRepository.save(Profile.builder()
                .username("Jojo")
                .email("jojoreceiver0024@yopmail.com")
                .password(new BCryptPasswordEncoder().encode("secret"))
                .roles(RoleEnum.USER)
                .build());

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
                , emailSenderService.buildEmail(profile.getFirstName(), link,QRCodeUtils.generateQRcode(link, "utf-8", 300, 300))
                );

        return token;
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
