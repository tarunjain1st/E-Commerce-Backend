package com.example.usermanagement.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class User extends BaseModel {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    @ManyToMany
    private List<Role> roles = new ArrayList<>();
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<UserAddress> userAddresses = new ArrayList<>();
}