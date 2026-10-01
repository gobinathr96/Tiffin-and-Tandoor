package com.foodapp.controller;

import com.foodapp.dto.PhoneLoginRequest;
import com.foodapp.model.User;
import com.foodapp.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * The customer types their mobile number (and name, the first time).
     * Returns their account - existing or newly created - including their
     * current loyalty point balance.
     */
    @PostMapping("/phone-login")
    public User phoneLogin(@RequestBody PhoneLoginRequest request) {
        return userService.findOrCreateByPhone(request.getPhone(), request.getName());
    }

    @GetMapping("/{id}")
    public User getOne(@PathVariable Long id) {
        return userService.getById(id);
    }
}
