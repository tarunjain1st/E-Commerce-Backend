package com.example.notificationservice.events;

import lombok.Data;

@Data
public class UserCreatedEvent {
    private String email;
    private String name;
}
