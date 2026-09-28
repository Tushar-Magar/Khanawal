package com.efood.config;

import com.efood.entity.User;
import com.efood.service.CartService;
import com.efood.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UserService userService;
    private final CartService cartService;

    public GlobalModelAdvice(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return userService.findByEmail(auth.getName());
        }
        return null;
    }

    @ModelAttribute("cartCount")
    public int getCartCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            User user = userService.findByEmail(auth.getName());
            if (user != null) {
                return cartService.getCartItemCount(user);
            }
        }
        return 0;
    }
}
