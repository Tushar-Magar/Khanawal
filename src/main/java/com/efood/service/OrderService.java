package com.efood.service;

import com.efood.dto.CheckoutRequest;
import com.efood.entity.Order;
import com.efood.entity.User;

import java.util.List;

public interface OrderService {
    Order createOrderFromCart(User user, CheckoutRequest checkoutRequest);
    Order getOrderById(Long id);
    Order getOrderByOrderNumber(String orderNumber);
    List<Order> getUserOrders(User user);
    List<Order> getAllOrders();
    List<Order> getOrdersByStatus(String status);
    boolean canUserAccessOrder(User user, Order order);
    void cancelOrder(User user, Long orderId);
    void updateOrderStatus(Long orderId, String newStatus);
    void updatePaymentStatus(Long orderId, String newPaymentStatus);
}
