package com.efood.service.impl;

import com.efood.dto.RegisterRequest;
import com.efood.entity.Role;
import com.efood.entity.User;
import com.efood.exception.ResourceNotFoundException;
import com.efood.exception.UserAlreadyExistsException;
import com.efood.repository.UserRepository;
import com.efood.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new UserAlreadyExistsException("An account with email " + request.getEmail() + " already exists.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setMobile(request.getMobile().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAddress(request.getAddress().trim());
        user.setCity(request.getCity().trim());
        user.setState(request.getState().trim());
        user.setPincode(request.getPincode().trim());
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        if (email == null) return null;
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Override
    public User updateUserProfile(Long userId, String fullName, String mobile, String address, String city, String state, String pincode) {
        User user = findById(userId);
        user.setFullName(fullName.trim());
        user.setMobile(mobile.trim());
        user.setAddress(address.trim());
        user.setCity(city.trim());
        user.setState(state.trim());
        user.setPincode(pincode.trim());
        return userRepository.save(user);
    }

    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        User user = findById(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public void toggleUserStatus(Long userId, Long currentAdminId) {
        if (userId.equals(currentAdminId)) {
            throw new IllegalArgumentException("You cannot disable your own admin account.");
        }
        User user = findById(userId);
        user.setEnabled(!Boolean.TRUE.equals(user.getEnabled()));
        userRepository.save(user);
    }
}
