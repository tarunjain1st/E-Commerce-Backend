package com.example.notificationservice.services;

import com.example.notificationservice.utils.EmailUtil;
import org.springframework.stereotype.Service;

import javax.mail.Session;

@Service
public class EmailService {

    private final Session session;

    public EmailService(Session session) {
        this.session = session;
    }

    public void sendEmail(String to, String subject, String body) {
        EmailUtil.send(session, to, subject, body);
    }
}
