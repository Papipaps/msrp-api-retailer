package fr.mspr.retailer.service;

import org.springframework.stereotype.Service;
import reactor.util.annotation.Nullable;

@Service
public interface EmailSenderService {
    void send(String to, String email);

    String buildEmail(String name, String link, byte[] attachment);
}
