package com.efood.service.impl;

import com.efood.dto.DashboardStats;
import com.efood.repository.CategoryRepository;
import com.efood.repository.FoodRepository;
import com.efood.repository.OrderItemRepository;
import com.efood.repository.OrderRepository;
import com.efood.repository.UserRepository;
import com.efood.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            FoodRepository foodRepository,
                            CategoryRepository categoryRepository,
                            OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        stats.setTotalUsers(userRepository.count());
        stats.setTotalFoods(foodRepository.count());
        stats.setTotalCategories(categoryRepository.count());
        stats.setTotalOrders(orderRepository.count());

        long placed = orderRepository.countByOrderStatus("PLACED");
        long confirmed = orderRepository.countByOrderStatus("CONFIRMED");
        long preparing = orderRepository.countByOrderStatus("PREPARING");
        long outForDelivery = orderRepository.countByOrderStatus("OUT_FOR_DELIVERY");
        long delivered = orderRepository.countByOrderStatus("DELIVERED");
        long cancelled = orderRepository.countByOrderStatus("CANCELLED");

        stats.setPendingOrders(placed + confirmed + preparing + outForDelivery);
        stats.setDeliveredOrders(delivered);
        stats.setCancelledOrders(cancelled);

        Double totalRev = orderRepository.calculateTotalRevenue();
        stats.setTotalRevenue(totalRev != null ? Math.round(totalRev * 100.0) / 100.0 : 0.0);

        // Today's metrics
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);
        stats.setTodayOrders(orderRepository.countOrdersBetween(startOfToday, endOfToday));
        Double todayRev = orderRepository.calculateRevenueBetween(startOfToday, endOfToday);
        stats.setTodayRevenue(todayRev != null ? Math.round(todayRev * 100.0) / 100.0 : 0.0);

        // Month metrics
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        stats.setMonthOrders(orderRepository.countOrdersSince(startOfMonth));
        Double monthRev = orderRepository.calculateRevenueSince(startOfMonth);
        stats.setMonthRevenue(monthRev != null ? Math.round(monthRev * 100.0) / 100.0 : 0.0);

        // Status breakdown
        Map<String, Long> statusMap = new LinkedHashMap<>();
        statusMap.put("PLACED", placed);
        statusMap.put("CONFIRMED", confirmed);
        statusMap.put("PREPARING", preparing);
        statusMap.put("OUT_FOR_DELIVERY", outForDelivery);
        statusMap.put("DELIVERED", delivered);
        statusMap.put("CANCELLED", cancelled);
        stats.setOrdersByStatus(statusMap);

        // Popular foods
        List<Object[]> rawPopular = orderItemRepository.findPopularFoodsRaw();
        List<Map<String, Object>> popularFoods = new ArrayList<>();
        int limit = Math.min(rawPopular.size(), 5);
        for (int i = 0; i < limit; i++) {
            Object[] row = rawPopular.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("quantity", row[1]);
            map.put("revenue", row[2] != null ? Math.round(((Number) row[2]).doubleValue() * 100.0) / 100.0 : 0.0);
            popularFoods.add(map);
        }
        stats.setPopularFoods(popularFoods);

        return stats;
    }
}
