package com.efood.service.impl;

import com.efood.dto.CheckoutRequest;
import com.efood.entity.*;
import com.efood.exception.InvalidOrderException;
import com.efood.exception.ResourceNotFoundException;
import com.efood.repository.CartRepository;
import com.efood.repository.OrderItemRepository;
import com.efood.repository.OrderRepository;
import com.efood.service.CartService;
import com.efood.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final Random random = new Random();

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CartRepository cartRepository,
                            CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    @Override
    public Order createOrderFromCart(User user, CheckoutRequest checkoutRequest) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new InvalidOrderException("No active cart found."));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new InvalidOrderException("Your cart is empty. Please add items before checking out.");
        }

        double subtotal = 0.0;
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Food food = cartItem.getFood();
            if (food == null || !Boolean.TRUE.equals(food.getAvailable())) {
                throw new InvalidOrderException("The food item '" +
                        (food != null ? food.getName() : "Unknown") +
                        "' is currently unavailable. Please remove it from your cart.");
            }

            if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
                throw new InvalidOrderException("Invalid quantity for food item '" + food.getName() + "'.");
            }

            // Fresh database price snapshot
            double priceAtOrder = food.getPrice();
            double itemSubtotal = Math.round(priceAtOrder * cartItem.getQuantity() * 100.0) / 100.0;
            subtotal += itemSubtotal;

            OrderItem orderItem = new OrderItem();
            orderItem.setFood(food);
            orderItem.setFoodName(food.getName()); // Frozen snapshot
            orderItem.setPrice(priceAtOrder);      // Frozen snapshot
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(itemSubtotal);
            orderItemsToSave.add(orderItem);
        }

        subtotal = Math.round(subtotal * 100.0) / 100.0;
        double deliveryFee = 40.0;
        double tax = Math.round(subtotal * 0.05 * 100.0) / 100.0;
        double grandTotal = Math.round((subtotal + deliveryFee + tax) * 100.0) / 100.0;

        Order order = new Order();
        order.setOrderNumber(generateUniqueOrderNumber());
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setSubtotal(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setTax(tax);
        order.setTotalAmount(grandTotal);

        order.setDeliveryName(checkoutRequest.getDeliveryName().trim());
        order.setDeliveryMobile(checkoutRequest.getDeliveryMobile().trim());
        order.setDeliveryAddress(checkoutRequest.getDeliveryAddress().trim());
        order.setCity(checkoutRequest.getCity().trim());
        order.setState(checkoutRequest.getState().trim());
        order.setPincode(checkoutRequest.getPincode().trim());

        order.setPaymentMethod(checkoutRequest.getPaymentMethod() != null ? checkoutRequest.getPaymentMethod() : "COD");
        order.setPaymentStatus("PENDING");
        order.setOrderStatus("PLACED");

        Order savedOrder = orderRepository.save(order);

        for (OrderItem oi : orderItemsToSave) {
            oi.setOrder(savedOrder);
            orderItemRepository.save(oi);
        }
        savedOrder.setOrderItems(orderItemsToSave);

        // Clear cart after successful order creation
        cartService.clearCart(user);

        return savedOrder;
    }

    private String generateUniqueOrderNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String orderNumber;
        do {
            int seq = 1000 + random.nextInt(9000);
            orderNumber = "EF" + datePart + seq;
        } while (orderRepository.findByOrderNumber(orderNumber).isPresent());
        return orderNumber;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByOrderStatusOrderByOrderDateDesc(status);
    }

    @Override
    public boolean canUserAccessOrder(User user, Order order) {
        if (user == null || order == null) return false;
        if (Role.ROLE_ADMIN.equals(user.getRole())) return true;
        return order.getUser() != null && order.getUser().getId().equals(user.getId());
    }

    @Override
    public void cancelOrder(User user, Long orderId) {
        Order order = getOrderById(orderId);
        if (!canUserAccessOrder(user, order)) {
            throw new IllegalArgumentException("You are not authorized to cancel this order.");
        }

        String currentStatus = order.getOrderStatus();
        if ("PLACED".equalsIgnoreCase(currentStatus) || "CONFIRMED".equalsIgnoreCase(currentStatus)) {
            order.setOrderStatus("CANCELLED");
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("Order cannot be cancelled because it is already '" + currentStatus + "'.");
        }
    }

    @Override
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = getOrderById(orderId);
        String current = order.getOrderStatus();

        if ("DELIVERED".equalsIgnoreCase(current) || "CANCELLED".equalsIgnoreCase(current)) {
            throw new IllegalStateException("Cannot change status of an order that is already " + current + ".");
        }

        boolean validTransition = false;
        if ("PLACED".equalsIgnoreCase(current)) {
            validTransition = "CONFIRMED".equalsIgnoreCase(newStatus) || "CANCELLED".equalsIgnoreCase(newStatus);
        } else if ("CONFIRMED".equalsIgnoreCase(current)) {
            validTransition = "PREPARING".equalsIgnoreCase(newStatus) || "CANCELLED".equalsIgnoreCase(newStatus);
        } else if ("PREPARING".equalsIgnoreCase(current)) {
            validTransition = "OUT_FOR_DELIVERY".equalsIgnoreCase(newStatus);
        } else if ("OUT_FOR_DELIVERY".equalsIgnoreCase(current)) {
            validTransition = "DELIVERED".equalsIgnoreCase(newStatus);
        }

        if (!validTransition) {
            throw new IllegalStateException("Invalid status transition from " + current + " to " + newStatus + ".");
        }

        order.setOrderStatus(newStatus);
        if ("DELIVERED".equalsIgnoreCase(newStatus) && "COD".equalsIgnoreCase(order.getPaymentMethod())) {
            order.setPaymentStatus("PAID");
        }
        orderRepository.save(order);
    }

    @Override
    public void updatePaymentStatus(Long orderId, String newPaymentStatus) {
        Order order = getOrderById(orderId);
        order.setPaymentStatus(newPaymentStatus);
        orderRepository.save(order);
    }
}
