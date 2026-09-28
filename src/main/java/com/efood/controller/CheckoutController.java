package com.efood.controller;

import com.efood.dto.CartSummary;
import com.efood.dto.CheckoutRequest;
import com.efood.entity.Cart;
import com.efood.entity.Order;
import com.efood.entity.User;
import com.efood.service.CartService;
import com.efood.service.OrderService;
import com.efood.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    public CheckoutController(CartService cartService, OrderService orderService, UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String showCheckoutPage(@AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());
        Cart cart = cartService.getOrCreateCartForUser(user);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Please add items to your cart before proceeding to checkout.");
            return "redirect:/cart";
        }

        CartSummary summary = cartService.getCartSummary(cart);

        if (!model.containsAttribute("checkoutRequest")) {
            CheckoutRequest checkoutRequest = new CheckoutRequest(
                    user.getFullName(),
                    user.getMobile(),
                    user.getAddress(),
                    user.getCity(),
                    user.getState(),
                    user.getPincode(),
                    "COD"
            );
            model.addAttribute("checkoutRequest", checkoutRequest);
        }

        model.addAttribute("cart", cart);
        model.addAttribute("summary", summary);

        return "checkout/checkout";
    }

    @PostMapping
    public String placeOrder(@AuthenticationPrincipal UserDetails userDetails,
                             @Valid @ModelAttribute("checkoutRequest") CheckoutRequest checkoutRequest,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());
        Cart cart = cartService.getOrCreateCartForUser(user);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Please add items before placing an order.");
            return "redirect:/cart";
        }

        if (bindingResult.hasErrors()) {
            CartSummary summary = cartService.getCartSummary(cart);
            model.addAttribute("cart", cart);
            model.addAttribute("summary", summary);
            return "checkout/checkout";
        }

        try {
            Order placedOrder = orderService.createOrderFromCart(user, checkoutRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Order placed successfully!");
            return "redirect:/orders/confirmation/" + placedOrder.getId();
        } catch (Exception e) {
            CartSummary summary = cartService.getCartSummary(cart);
            model.addAttribute("cart", cart);
            model.addAttribute("summary", summary);
            model.addAttribute("errorMessage", "Order failed: " + e.getMessage());
            return "checkout/checkout";
        }
    }
}
