package fr.mspr.retailer.utils;

import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.data.model.RoleEnum;
import fr.mspr.retailer.repository.ProfileRepository;
import fr.mspr.retailer.security.token.ConfirmationToken;
import fr.mspr.retailer.security.token.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class AuthorizationHelper {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    public boolean isAdmin(Long profileId) {
        if (profileId != null) {
            Optional<Profile> profileOptional = profileRepository.findById(profileId);
            if (profileOptional.isEmpty()) {
                throw new RuntimeException("Error : user not in db");
            }
            Profile profile = profileOptional.get();
            return profile.getRoles() == RoleEnum.ROLE_ADMIN;
        }
        throw new RuntimeException("Error : id cannot be null");

    }

    public Profile getProfileFromToken(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;
        String apikey = req.getHeader("token");
        Optional<ConfirmationToken> tokenOptional = confirmationTokenRepository.findByToken(apikey);
        if (tokenOptional.isEmpty()) {
            throw new RuntimeException("Error : token not in db");
        }
        return tokenOptional.get().getProfile();
    }

}
