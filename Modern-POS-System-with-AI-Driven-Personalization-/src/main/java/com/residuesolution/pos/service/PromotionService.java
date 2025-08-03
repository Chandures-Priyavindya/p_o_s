package com.residuesolution.pos.service;

import com.residuesolution.pos.dto.Promotion;

import java.util.List;

public interface PromotionService {

    // Create a new promotion
    Boolean createPromotion(Promotion promotion);

    // Get all promotions
    List<Promotion> getAllPromotions();

    // Get promotion by ID
    Promotion getPromotionById(Integer id);

    // Update existing promotion
    Boolean updatePromotion(Integer id, Promotion promotion);

    // Delete promotion
    Boolean deletePromotion(Integer id);

    // Get all active promotions
    List<Promotion> getActivePromotions();

    // Get currently valid promotions (within date range)
    List<Promotion> getCurrentlyValidPromotions();

    // Apply promotion to calculate discount
    // This method will calculate the actual discount amount based on promotion type
    java.math.BigDecimal applyPromotion(Integer promotionId, java.math.BigDecimal originalAmount);

    // Activate/Deactivate promotion
    Boolean togglePromotionStatus(Integer id, boolean isActive);

    // Search promotions by title
    List<Promotion> searchPromotionsByTitle(String title);
}