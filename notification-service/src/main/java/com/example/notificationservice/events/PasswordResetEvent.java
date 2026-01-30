package com.example.notificationservice.events;

import lombok.Data;

@Data
public class PasswordResetEvent {
    private String email;
    private String name;
    private String resetLink;
}
