package com.efood.controller.admin;

import com.efood.dto.DashboardStats;
import com.efood.entity.Order;
import com.efood.service.AdminService;
import com.efood.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {

    private final OrderService orderService;
    private final AdminService adminService;

    public AdminOrderController(OrderService orderService, AdminService adminService) {
        this.orderService = orderService;
        this.adminService = adminService;
    }

    @GetMapping("/orders")
    public String listOrders(@RequestParam(value = "status", required = false) String status, Model model) {
        List<Order> orders;
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            orders = orderService.getOrdersByStatus(status.toUpperCase());
        } else {
            orders = orderService.getAllOrders();
        }

        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status != null ? status.toUpperCase() : "ALL");

        return "admin/orders/list";
    }

    @GetMapping("/orders/{id}")
    public String viewOrderDetails(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "admin/orders/details";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable("id") Long id,
                                    @RequestParam("status") String newStatus,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Order status updated to " + newStatus);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/orders/{id}/payment-status")
    public String updatePaymentStatus(@PathVariable("id") Long id,
                                     @RequestParam("paymentStatus") String newPaymentStatus,
                                     RedirectAttributes redirectAttributes) {
        try {
            orderService.updatePaymentStatus(id, newPaymentStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Payment status updated to " + newPaymentStatus);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        DashboardStats stats = adminService.getDashboardStats();
        model.addAttribute("stats", stats);
        return "admin/reports";
    }
}
