package com.efood.controller.admin;

import com.efood.dto.DashboardStats;
import com.efood.entity.Order;
import com.efood.repository.OrderRepository;
import com.efood.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final AdminService adminService;
    private final OrderRepository orderRepository;

    public AdminDashboardController(AdminService adminService, OrderRepository orderRepository) {
        this.adminService = adminService;
        this.orderRepository = orderRepository;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        DashboardStats stats = adminService.getDashboardStats();
        List<Order> recentOrders = orderRepository.findTop5ByOrderByOrderDateDesc();

        model.addAttribute("stats", stats);
        model.addAttribute("recentOrders", recentOrders);

        return "admin/dashboard";
    }
}
