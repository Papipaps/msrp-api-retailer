package fr.mspr.retailer.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailSenderService {
    void send(String to, String email);

    String buildEmail(String name, String link, byte[] attachment);
}
