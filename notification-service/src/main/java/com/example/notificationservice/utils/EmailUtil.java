package com.example.notificationservice.utils;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

public class EmailUtil {

    public static void send(Session session, String to, String subject, String htmlBody) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("no-reply@tarunkart.com", "TarunKart"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(htmlBody, "text/html; charset=UTF-8");
            msg.setSentDate(new Date());

            Transport.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
