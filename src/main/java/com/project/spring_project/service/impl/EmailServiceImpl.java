package com.project.spring_project.service.impl;

import com.project.spring_project.service.EmailService;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${disable.emails:false}")
    private boolean disableEmails;

    @PostConstruct
    public void checkMailConfig() {
        System.out.println("checkMailConfig:: mailUsername: " + mailUsername);
    }

    /**
     * Sends a plain text email.
     *
     * @param to      Recipient's email address.
     * @param subject Subject of the email.
     * @param body    Body of the email.
     */
    @Override
    public void sendPlainTextEmail(String to, String subject, String body) {
        if (disableEmails) {
            // You could log this or silently skip
            System.out.println("Emails are disabled. Skipping email to " + to);
            return;
        }

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
