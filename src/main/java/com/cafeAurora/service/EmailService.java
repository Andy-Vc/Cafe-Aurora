package com.cafeAurora.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async 
    public void sendEmail(String to, String subject, String message) {
        if (to == null || to.isBlank()) {
            log.warn("Intento de envío sin destinatario");
            return;
        }
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom("Café Aurora <andy.v.centeno@gmail.com>");
            email.setTo(to);
            email.setSubject(subject);
            email.setText(message);
            mailSender.send(email);
            log.info("✅ Correo enviado a {}", to);
        } catch (Exception e) {
            log.error("❌ Error enviando correo a {}: {}", to, e.getMessage());
        }
    }
}