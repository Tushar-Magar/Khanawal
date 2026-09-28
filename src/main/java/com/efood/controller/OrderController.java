package com.efood.controller;

import com.efood.entity.Order;
import com.efood.entity.User;
import com.efood.service.OrderService;
import com.efood.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String viewUserOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<Order> orders = orderService.getUserOrders(user);
        model.addAttribute("orders", orders);
        return "order/list";
    }

    @GetMapping("/{id}")
    public String viewOrderDetails(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable("id") Long id,
                                   Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Order order = orderService.getOrderById(id);

        if (!orderService.canUserAccessOrder(user, order)) {
            throw new AccessDeniedException("You are not authorized to view this order.");
        }

        model.addAttribute("order", order);
        return "order/details";
    }

    @GetMapping("/confirmation/{id}")
    public String orderConfirmation(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable("id") Long id,
                                    Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Order order = orderService.getOrderById(id);

        if (!orderService.canUserAccessOrder(user, order)) {
            throw new AccessDeniedException("You are not authorized to view this order confirmation.");
        }

        model.addAttribute("order", order);
        return "order/confirmation";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable("id") Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            orderService.cancelOrder(user, id);
            redirectAttributes.addFlashAttribute("successMessage", "Order has been cancelled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }
}
