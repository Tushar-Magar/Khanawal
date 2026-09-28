package com.efood.controller;

import com.efood.entity.User;
import com.efood.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profile/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam("fullName") String fullName,
                                @RequestParam("mobile") String mobile,
                                @RequestParam("address") String address,
                                @RequestParam("city") String city,
                                @RequestParam("state") String state,
                                @RequestParam("pincode") String pincode,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            userService.updateUserProfile(user.getId(), fullName, mobile, address, city, state, pincode);
            redirectAttributes.addFlashAttribute("successMessage", "Profile details updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmNewPassword") String confirmNewPassword,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmNewPassword)) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", "New password and confirmation password do not match.");
            return "redirect:/profile";
        }

        if (newPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", "New password must be at least 8 characters long.");
            return "redirect:/profile";
        }

        User user = userService.findByEmail(userDetails.getUsername());
        boolean success = userService.changePassword(user.getId(), currentPassword, newPassword);

        if (success) {
            redirectAttributes.addFlashAttribute("passwordSuccessMessage", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", "Incorrect current password. Please try again.");
        }

        return "redirect:/profile";
    }
}
