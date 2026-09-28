package com.efood.repository;

import com.efood.entity.Order;
import com.efood.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByOrderDateDesc(User user);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findAllByOrderByOrderDateDesc();

    List<Order> findTop5ByOrderByOrderDateDesc();

    List<Order> findByOrderStatusOrderByOrderDateDesc(String orderStatus);

    long countByOrderStatus(String orderStatus);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.orderStatus <> 'CANCELLED'")
    Double calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.orderStatus <> 'CANCELLED' AND o.orderDate >= :start AND o.orderDate <= :end")
    Double calculateRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :start AND o.orderDate <= :end")
    long countOrdersBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :start")
    long countOrdersSince(@Param("start") LocalDateTime start);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.orderStatus <> 'CANCELLED' AND o.orderDate >= :start")
    Double calculateRevenueSince(@Param("start") LocalDateTime start);
}
