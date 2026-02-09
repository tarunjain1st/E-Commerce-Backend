package com.example.usermanagement.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreatedEvent {
    private String userId;
    private String email;
    private String name;
}
