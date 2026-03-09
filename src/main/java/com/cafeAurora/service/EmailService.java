package com.cafeAurora.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();

            email.setFrom("Café Aurora <andy.v.centeno@gmail.com>");
            email.setTo(to);
            email.setSubject(subject);
            email.setText(message);

            mailSender.send(email);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo: " + e.getMessage());
        }
    }
}