package com.project.spring_project.service;

public interface EmailService {
    void sendPlainTextEmail(String to, String subject, String body);

}
