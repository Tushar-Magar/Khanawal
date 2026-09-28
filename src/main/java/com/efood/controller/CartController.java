package com.efood.controller;

import com.efood.dto.CartSummary;
import com.efood.entity.Cart;
import com.efood.entity.Food;
import com.efood.entity.User;
import com.efood.service.CartService;
import com.efood.service.FoodService;
import com.efood.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final FoodService foodService;

    public CartController(CartService cartService, UserService userService, FoodService foodService) {
        this.cartService = cartService;
        this.userService = userService;
        this.foodService = foodService;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Cart cart = cartService.getOrCreateCartForUser(user);
        CartSummary summary = cartService.getCartSummary(cart);

        model.addAttribute("cart", cart);
        model.addAttribute("summary", summary);

        return "cart/cart";
    }

    @PostMapping("/add")
    public String addToCart(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("foodId") Long foodId,
                            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                            @RequestParam(value = "returnUrl", required = false) String returnUrl,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            Food food = foodService.getFoodById(foodId);
            cartService.addToCart(user, foodId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", food.getName() + " added to your cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        if (returnUrl != null && !returnUrl.trim().isEmpty() && returnUrl.startsWith("/")) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam("itemId") Long itemId,
                                 @RequestParam("quantity") Integer quantity,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            cartService.updateCartItemQuantity(user, itemId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Cart updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String removeItem(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable("id") Long itemId,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            cartService.removeFromCart(user, itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            cartService.clearCart(user);
            redirectAttributes.addFlashAttribute("successMessage", "Your cart has been cleared.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }
}
