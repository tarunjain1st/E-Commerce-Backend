package com.example.notificationservice.consumers;

import com.example.notificationservice.dtos.EmailDto;
import com.example.notificationservice.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

@Component

public class EmailConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "signup", groupId = "notification-service-group")

    public void sendEmail(String message){
        EmailDto emailDto = objectMapper.readValue(message, EmailDto.class);
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailDto.getFrom(), "vhgh lqqf jsrf jhsn\n");
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, emailDto.getTo(), emailDto.getSubject(), emailDto.getBody());

    }
}
