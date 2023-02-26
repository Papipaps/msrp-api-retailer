package fr.mspr.retailer.service;

import fr.mspr.retailer.data.model.Profile;
import fr.mspr.retailer.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Transactional
    public boolean activeAccount(long id) {
        Optional<Profile> optionalProfile = profileRepository.findById(id);

        if (optionalProfile.isEmpty()) {
            throw new IllegalArgumentException("");
        }

        Profile profile = optionalProfile.get();

        if (profile.isActive()) {
            throw new IllegalArgumentException("");
        }
        profile.setActive(true);
        return true;
    }

}
