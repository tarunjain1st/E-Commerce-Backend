package com.example.usermanagement.services;

import com.example.usermanagement.exceptions.UserNotFoundException;
import com.example.usermanagement.exceptions.UserInactiveException;
import com.example.usermanagement.models.Status;
import com.example.usermanagement.models.User;
import com.example.usermanagement.models.UserAddress;
import com.example.usermanagement.repos.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /* ---------------- INTERNAL SERVICE ---------------- */
    // Internal service call: fetch by ID, regardless of status
    @Override
    public User getUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " does not exist"));
    }

    /* ---------------- CLIENT APIs ---------------- */
    // Client API: only ACTIVE users
    @Override
    public User getUserProfile(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " does not exist"));

        if (user.getStatus() != Status.ACTIVE) {
            throw new UserInactiveException("User with id " + userId + " is inactive");
        }
        return user;
    }

    @Override
    @Transactional
    public User updateUserProfile(Long userId,
                                  String firstName,
                                  String lastName,
                                  String phoneNumber,
                                  String imageUrl,
                                  UserAddress address) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " does not exist"));

        if (user.getStatus() != Status.ACTIVE) {
            throw new UserInactiveException("User with id " + userId + " is inactive");
        }

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (phoneNumber != null) user.setPhoneNumber(phoneNumber);
        if (imageUrl != null) user.setImageUrl(imageUrl);

        if (address != null) {
            address.setUser(user);

            if (address.getId() != null) {
                // Update existing address
                UserAddress existingAddress = user.getUserAddresses().stream()
                        .filter(a -> a.getId().equals(address.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Address with id " + address.getId() + " not found"));

                existingAddress.setStreet(address.getStreet());
                existingAddress.setCity(address.getCity());
                existingAddress.setState(address.getState());
                existingAddress.setCountry(address.getCountry());
                existingAddress.setPostalCode(address.getPostalCode());
                existingAddress.setPhone(address.getPhone());

            } else {
                // New address
                user.getUserAddresses().add(address);
            }
        }

        return userRepo.save(user);
    }

    @Override
    @Transactional
    public boolean deleteUserProfile(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " does not exist"));

        if (user.getStatus() != Status.ACTIVE) {
            throw new UserInactiveException("User with id " + userId + " is already inactive");
        }

        // Soft delete
        user.setStatus(Status.INACTIVE);
        userRepo.save(user);
        return true;
    }
}
