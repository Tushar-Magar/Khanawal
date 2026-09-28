package com.efood.service;

import com.efood.dto.RegisterRequest;
import com.efood.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(RegisterRequest request);
    User findByEmail(String email);
    User findById(Long id);
    User updateUserProfile(Long userId, String fullName, String mobile, String address, String city, String state, String pincode);
    boolean changePassword(Long userId, String currentPassword, String newPassword);
    List<User> getAllUsers();
    void toggleUserStatus(Long userId, Long currentAdminId);
}
