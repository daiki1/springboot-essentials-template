package com.project.spring_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

//@Configuration
public class MailConfig {
    /*@Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl(); // Spring Boot auto-configures it using properties
    }*/
}
