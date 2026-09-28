package com.efood.repository;

import com.efood.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi.foodName, SUM(oi.quantity), SUM(oi.subtotal) " +
           "FROM OrderItem oi " +
           "GROUP BY oi.foodName " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findPopularFoodsRaw();
}
