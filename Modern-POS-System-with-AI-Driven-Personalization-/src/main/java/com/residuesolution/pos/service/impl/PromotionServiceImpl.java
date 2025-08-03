package com.residuesolution.pos.service.impl;

import com.residuesolution.pos.dto.Promotion;
import com.residuesolution.pos.entity.PromotionEntity;
import com.residuesolution.pos.repository.PromotionRepository;
import com.residuesolution.pos.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final ModelMapper mapper;

    @Override
    public Boolean createPromotion(Promotion promotion) {
        try {
            PromotionEntity promotionEntity = mapper.map(promotion, PromotionEntity.class);
            PromotionEntity savedPromotion = promotionRepository.save(promotionEntity);
            return savedPromotion != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Promotion> getAllPromotions() {
        List<PromotionEntity> promotionEntities = promotionRepository.findAll();
        return promotionEntities.stream()
                .map(entity -> mapper.map(entity, Promotion.class))
                .toList();
    }

    @Override
    public Promotion getPromotionById(Integer id) {
        Optional<PromotionEntity> promotionEntity = promotionRepository.findById(id);
        return promotionEntity.map(entity -> mapper.map(entity, Promotion.class)).orElse(null);
    }

    @Override
    public Boolean updatePromotion(Integer id, Promotion promotion) {
        Optional<PromotionEntity> existingEntity = promotionRepository.findById(id);

        if (existingEntity.isPresent()) {
            PromotionEntity entityToUpdate = existingEntity.get();

            // Update fields
            entityToUpdate.setDiscountType(promotion.getDiscountType());
            entityToUpdate.setTitle(promotion.getTitle());
            entityToUpdate.setDescription(promotion.getDescription());
            entityToUpdate.setDiscountValue(promotion.getDiscountValue());
            entityToUpdate.setStartDate(promotion.getStartDate());
            entityToUpdate.setEndDate(promotion.getEndDate());
            entityToUpdate.setActive(promotion.isActive());

            promotionRepository.save(entityToUpdate);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deletePromotion(Integer id) {
        if (promotionRepository.existsById(id)) {
            promotionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Promotion> getActivePromotions() {
        List<PromotionEntity> activePromotions = promotionRepository.findByIsActiveTrue();
        return activePromotions.stream()
                .map(entity -> mapper.map(entity, Promotion.class))
                .toList();
    }

    @Override
    public List<Promotion> getCurrentlyValidPromotions() {
        LocalDateTime now = LocalDateTime.now();
        List<PromotionEntity> validPromotions = promotionRepository.findActivePromotionsInDateRange(now);
        return validPromotions.stream()
                .map(entity -> mapper.map(entity, Promotion.class))
                .toList();
    }

    @Override
    public BigDecimal applyPromotion(Integer promotionId, BigDecimal originalAmount) {
        Optional<PromotionEntity> promotionEntity = promotionRepository.findById(promotionId);

        if (promotionEntity.isEmpty() || !promotionEntity.get().isActive()) {
            return BigDecimal.ZERO; // No discount if promotion not found or inactive
        }

        PromotionEntity promotion = promotionEntity.get();
        LocalDateTime now = LocalDateTime.now();

        // Check if promotion is currently valid
        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            return BigDecimal.ZERO; // No discount if promotion is not in valid date range
        }

        BigDecimal discountAmount = BigDecimal.ZERO;

        switch (promotion.getDiscountType().toUpperCase()) {
            case "PERCENTAGE":
                // Calculate percentage discount
                discountAmount = originalAmount.multiply(promotion.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;

            case "FIXED_AMOUNT":
                // Fixed amount discount (but not more than original amount)
                discountAmount = promotion.getDiscountValue().min(originalAmount);
                break;

            default:
                // For other types like BUY_ONE_GET_ONE, implement specific logic as needed
                discountAmount = BigDecimal.ZERO;
                break;
        }

        return discountAmount;
    }

    @Override
    public Boolean togglePromotionStatus(Integer id, boolean isActive) {
        Optional<PromotionEntity> promotionEntity = promotionRepository.findById(id);

        if (promotionEntity.isPresent()) {
            PromotionEntity entity = promotionEntity.get();
            entity.setActive(isActive);
            promotionRepository.save(entity);
            return true;
        }
        return false;
    }

    @Override
    public List<Promotion> searchPromotionsByTitle(String title) {
        List<PromotionEntity> promotionEntities = promotionRepository.findByTitleContainingIgnoreCase(title);
        return promotionEntities.stream()
                .map(entity -> mapper.map(entity, Promotion.class))
                .toList();
    }
}