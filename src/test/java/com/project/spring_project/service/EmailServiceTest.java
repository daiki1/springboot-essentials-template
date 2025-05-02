package com.project.spring_project.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@SpringBootTest
@Profile("dev")
public class EmailServiceTest {
    private final EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    public EmailServiceTest(EmailService emailService) {
        this.emailService = emailService;
    }

    @Test
    void sendTestEmail() {
        // Setup dummy behavior
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendPlainTextEmail("test@example.com", "Test Subject", "Test Body");

        // Optionally verify it was called
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
