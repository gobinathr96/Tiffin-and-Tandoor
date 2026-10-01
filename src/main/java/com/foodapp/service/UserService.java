package com.foodapp.service;

import com.foodapp.dto.AuthRequest;
import com.foodapp.exception.ResourceNotFoundException;
import com.foodapp.model.User;
import com.foodapp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signup(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // NOTE: For learning purposes only. In a real app, hash the password
        // with Spring Security's BCryptPasswordEncoder before saving.
        user.setPassword(request.getPassword());
        return userRepository.save(user);
    }

    public User login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }
        return user;
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    /**
     * The core of "order with your mobile number": if this phone number has
     * ordered before, return that same account (and their loyalty points).
     * If not, create a new guest account for it on the spot.
     */
    public User findOrCreateByPhone(String phone, String name) {
        return userRepository.findByPhone(phone).orElseGet(() -> {
            User user = new User();
            user.setPhone(phone);
            user.setName((name == null || name.isBlank()) ? "Guest" : name);
            return userRepository.save(user);
        });
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
