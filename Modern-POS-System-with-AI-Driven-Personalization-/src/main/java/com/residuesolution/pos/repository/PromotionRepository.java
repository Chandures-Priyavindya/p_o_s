package com.residuesolution.pos.repository;

import com.residuesolution.pos.entity.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Integer> {

    // Find all active promotions
    List<PromotionEntity> findByIsActiveTrue();

    // Find active promotions within date range
    @Query("SELECT p FROM PromotionEntity p WHERE p.isActive = true " +
            "AND p.startDate <= :currentDate AND p.endDate >= :currentDate")
    List<PromotionEntity> findActivePromotionsInDateRange(@Param("currentDate") LocalDateTime currentDate);

    // Find promotions by discount type
    List<PromotionEntity> findByDiscountTypeAndIsActiveTrue(String discountType);

    // Find promotions by title (case-insensitive search)
    @Query("SELECT p FROM PromotionEntity p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<PromotionEntity> findByTitleContainingIgnoreCase(@Param("title") String title);

    // Find expired promotions
    @Query("SELECT p FROM PromotionEntity p WHERE p.endDate < :currentDate")
    List<PromotionEntity> findExpiredPromotions(@Param("currentDate") LocalDateTime currentDate);
}