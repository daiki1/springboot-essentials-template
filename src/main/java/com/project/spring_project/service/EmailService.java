package com.project.spring_project.service;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @PostConstruct
    public void checkMailConfig() {
        System.out.println("checkMailConfig:: mailUsername: " + mailUsername);
    }

    public void sendPlainTextEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailUsername);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("Email sent to "+to);
        } catch (MailException ex) {
            System.out.println("Failed to send email to "+to+": "+ ex.getMessage());
            throw ex; // optional: rethrow or handle gracefully
        }
    }
}
