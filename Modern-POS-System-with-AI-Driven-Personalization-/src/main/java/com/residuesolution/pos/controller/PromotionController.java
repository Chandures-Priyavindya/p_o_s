package com.residuesolution.pos.controller;

import com.residuesolution.pos.dto.Promotion;
import com.residuesolution.pos.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promotion")
@CrossOrigin
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    // ✅ Full CRUD - Create promotion (Admin Only)
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> createPromotion(@RequestBody Promotion promotion) {
        Boolean isCreated = promotionService.createPromotion(promotion);

        if (isCreated) {
            return ResponseEntity.ok("Promotion created successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to create promotion");
        }
    }

    // ✅ Full CRUD - Update promotion (Admin Only)
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updatePromotion(
            @PathVariable Integer id,
            @RequestBody Promotion promotion) {

        Boolean isUpdated = promotionService.updatePromotion(id, promotion);

        if (isUpdated) {
            return ResponseEntity.ok("Promotion updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Full CRUD - Delete promotion (Admin Only)
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deletePromotion(@PathVariable Integer id) {
        Boolean isDeleted = promotionService.deletePromotion(id);

        if (isDeleted) {
            return ResponseEntity.ok("Promotion deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ View promotions - Get all promotions (Admin & Manager)
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        List<Promotion> promotions = promotionService.getAllPromotions();

        if (promotions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(promotions);
    }

    // ✅ View promotions - Get promotion by ID (Admin, Manager, Cashier)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable Integer id) {
        Promotion promotion = promotionService.getPromotionById(id);

        if (promotion != null) {
            return ResponseEntity.ok(promotion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ View and apply promotions - Get active promotions (All roles)
    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<List<Promotion>> getActivePromotions() {
        List<Promotion> activePromotions = promotionService.getActivePromotions();

        if (activePromotions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(activePromotions);
    }

    // ✅ View and apply promotions - Get currently valid promotions (All roles)
    @GetMapping("/valid")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<List<Promotion>> getCurrentlyValidPromotions() {
        List<Promotion> validPromotions = promotionService.getCurrentlyValidPromotions();

        if (validPromotions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(validPromotions);
    }

    // ✅ Apply promotions - Calculate discount for a promotion (All roles)
    @PostMapping("/apply/{promotionId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<Map<String, Object>> applyPromotion(
            @PathVariable Integer promotionId,
            @RequestBody Map<String, BigDecimal> request) {

        BigDecimal originalAmount = request.get("originalAmount");

        if (originalAmount == null || originalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Original amount must be provided and greater than zero"
            ));
        }

        BigDecimal discountAmount = promotionService.applyPromotion(promotionId, originalAmount);
        BigDecimal finalAmount = originalAmount.subtract(discountAmount);

        return ResponseEntity.ok(Map.of(
                "originalAmount", originalAmount,
                "discountAmount", discountAmount,
                "finalAmount", finalAmount,
                "promotionId", promotionId
        ));
    }

    // ✅ Additional feature - Toggle promotion status (Admin Only)
    @PostMapping("/toggle-status/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> togglePromotionStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> request) {

        Boolean isActive = request.get("isActive");

        if (isActive == null) {
            return ResponseEntity.badRequest().body("isActive field is required");
        }

        Boolean isToggled = promotionService.togglePromotionStatus(id, isActive);

        if (isToggled) {
            return ResponseEntity.ok("Promotion status updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Additional feature - Search promotions by title (All roles)
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<List<Promotion>> searchPromotionsByTitle(@RequestParam String title) {
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Promotion> promotions = promotionService.searchPromotionsByTitle(title.trim());

        if (promotions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(promotions);
    }

    // ✅ Test endpoint to check if controller is working
    @GetMapping("/test")
    public ResponseEntity<String> testPromotionController() {
        return ResponseEntity.ok("Promotion Controller is working!");
    }
}