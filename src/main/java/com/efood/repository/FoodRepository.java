package com.efood.repository;

import com.efood.entity.Food;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

       List<Food> findByAvailableTrue();

       List<Food> findByCategoryId(Long categoryId);

       List<Food> findByCategoryIdAndAvailableTrue(Long categoryId);

       long countByCategoryId(Long categoryId);

       List<Food> findTop6ByAvailableTrueOrderByRatingDesc();

       List<Food> findByNameContainingIgnoreCase(String name);

       @Query("SELECT f FROM Food f WHERE " +
                     "(:keyword IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
                     +
                     "AND (:categoryId IS NULL OR f.category.id = :categoryId) " +
                     "AND (:availableOnly IS FALSE OR f.available IS TRUE) " +
                     "AND (:minPrice IS NULL OR f.price >= :minPrice) " +
                     "AND (:maxPrice IS NULL OR f.price <= :maxPrice)")
       List<Food> filterFoods(@Param("keyword") String keyword,
                     @Param("categoryId") Long categoryId,
                     @Param("availableOnly") Boolean availableOnly,
                     @Param("minPrice") Double minPrice,
                     @Param("maxPrice") Double maxPrice,
                     Sort sort);
}
