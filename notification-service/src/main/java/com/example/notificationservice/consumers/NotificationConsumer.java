package com.example.notificationservice.consumers;

import com.example.notificationservice.events.*;
import com.example.notificationservice.services.EmailService;
import com.example.notificationservice.templates.EmailTemplates;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public NotificationConsumer(ObjectMapper objectMapper,
                                EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user.created", groupId = "notification-group")
    public void handleUserCreated(String msg) throws Exception {
        UserCreatedEvent event =
                objectMapper.readValue(msg, UserCreatedEvent.class);

        emailService.sendEmail(
                event.getEmail(),
                "Welcome to ShopEasy",
                EmailTemplates.userCreated(event.getName())
        );
    }

    @KafkaListener(topics = "user.password-reset", groupId = "notification-group")
    public void handlePasswordReset(String msg) throws Exception {
        PasswordResetEvent event =
                objectMapper.readValue(msg, PasswordResetEvent.class);

        emailService.sendEmail(
                event.getEmail(),
                "Reset your password",
                EmailTemplates.passwordReset(event.getName(), event.getResetLink())
        );
    }

    @KafkaListener(topics = "order.placed", groupId = "notification-group")
    public void handleOrderPlaced(String msg) throws Exception {
        OrderPlacedEvent event =
                objectMapper.readValue(msg, OrderPlacedEvent.class);

        emailService.sendEmail(
                event.getEmail(),
                "Order Confirmed",
                EmailTemplates.orderPlaced(
                        event.getName(),
                        event.getOrderId(),
                        event.getTotalAmount()
                )
        );
    }

    @KafkaListener(topics = "payment.success", groupId = "notification-group")
    public void handlePaymentSuccess(String msg) throws Exception {
        PaymentSuccessEvent event =
                objectMapper.readValue(msg, PaymentSuccessEvent.class);

        emailService.sendEmail(
                event.getEmail(),
                "Payment Successful",
                EmailTemplates.paymentSuccess(
                        event.getName(),
                        event.getPaymentId(),
                        event.getAmount()
                )
        );
    }
}
