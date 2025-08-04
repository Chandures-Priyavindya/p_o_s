package com.residuesolution.pos.entity;

import com.residuesolution.pos.enums.InventoryChangeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "changed_by", nullable = false)
    private Integer changedBy; // User ID who made the change

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private InventoryChangeType changeType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // Quantity changed (positive or negative)

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "previous_stock")
    private Integer previousStock;

    @Column(name = "new_stock")
    private Integer newStock;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "reference_id", length = 100)
    private String referenceId; // Reference to order, return, or adjustment ID

    @Column(name = "is_system_generated", nullable = false)
    private Boolean isSystemGenerated = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isSystemGenerated == null) {
            this.isSystemGenerated = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}