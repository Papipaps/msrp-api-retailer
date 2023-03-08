package fr.mspr.retailer.security.token;

import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.service.ProfileService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConfirmationTokenService {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private ProfileService profileService;

    public ConfirmationToken saveConfirmationToken(ConfirmationToken token) {
        return confirmationTokenRepository.save(token);
    }

    public String confirmToken(String token, HttpServletResponse response) {
        Optional<ConfirmationToken> confirmationTokenOpt = confirmationTokenRepository.findByToken(token);

        if (confirmationTokenOpt.isEmpty()) {
            throw new IllegalStateException("Token not found");

        }

        ConfirmationToken confirmationToken = confirmationTokenOpt.get();

        LocalDateTime expiresAt = confirmationToken.getExpiresAt();

        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token has expired");
        }

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Token deja confirmé");
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());


        ConfirmationToken savedToken = confirmationTokenRepository.save(confirmationToken);

        Profile profile = savedToken.getProfile();

        if (!profile.isActive()) {
            profileService.activeAccount(profile.getId());
        }


        response.setHeader("token", token);

        return "confirmed";

    }

    public ConfirmationToken update(String newToken, Profile profile) {
//       check if token is the same as previous
        Optional<ConfirmationToken> userTokenOpt = confirmationTokenRepository.findByProfileId(profile.getId());
//      check if token is already given
        Optional<ConfirmationToken> optionalToken = confirmationTokenRepository.findByToken(newToken);

        if (optionalToken.isPresent()) {
            throw new IllegalStateException("New token cannot be the same as previous");
        }

        String token = newToken.isEmpty() ? UUID.randomUUID().toString() : newToken;

        ConfirmationToken userToken = userTokenOpt.get();
        userToken.setToken(token);
        userToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        userToken.setUpdatedAt(LocalDateTime.now());
        userToken.setConfirmedAt(null);

        return confirmationTokenRepository.save(userToken);
    }
}
