package com.residuesolution.pos.repository;

import com.residuesolution.pos.entity.InventoryLogEntity;
import com.residuesolution.pos.enums.InventoryChangeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLogEntity, Integer> {

    // Find logs by product ID
    List<InventoryLogEntity> findByProductIdOrderByCreatedAtDesc(Integer productId);

    // Find logs by user (who made the change)
    List<InventoryLogEntity> findByChangedByOrderByCreatedAtDesc(Integer changedBy);

    // Find logs by change type
    List<InventoryLogEntity> findByChangeTypeOrderByCreatedAtDesc(InventoryChangeType changeType);

    // Find logs between dates
    @Query("SELECT il FROM InventoryLogEntity il WHERE il.createdAt BETWEEN :startDate AND :endDate ORDER BY il.createdAt DESC")
    List<InventoryLogEntity> findLogsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    // Find logs by product and date range
    @Query("SELECT il FROM InventoryLogEntity il WHERE il.productId = :productId " +
            "AND il.createdAt BETWEEN :startDate AND :endDate ORDER BY il.createdAt DESC")
    List<InventoryLogEntity> findByProductAndDateRange(@Param("productId") Integer productId,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);

    // Find logs by user and date range
    @Query("SELECT il FROM InventoryLogEntity il WHERE il.changedBy = :userId " +
            "AND il.createdAt BETWEEN :startDate AND :endDate ORDER BY il.createdAt DESC")
    List<InventoryLogEntity> findByUserAndDateRange(@Param("userId") Integer userId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    // Find logs by reference ID (e.g., order ID, return ID)
    List<InventoryLogEntity> findByReferenceIdOrderByCreatedAtDesc(String referenceId);

    // Find system-generated vs manual logs
    List<InventoryLogEntity> findByIsSystemGeneratedOrderByCreatedAtDesc(Boolean isSystemGenerated);

    // Get total quantity changes for a product within date range
    @Query("SELECT SUM(il.quantity) FROM InventoryLogEntity il WHERE il.productId = :productId " +
            "AND il.createdAt BETWEEN :startDate AND :endDate")
    Integer getTotalQuantityChangesForProduct(@Param("productId") Integer productId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    // Get logs by product and change type
    List<InventoryLogEntity> findByProductIdAndChangeTypeOrderByCreatedAtDesc(Integer productId,
                                                                              InventoryChangeType changeType);

    // Get recent logs (last N records)
    @Query("SELECT il FROM InventoryLogEntity il ORDER BY il.createdAt DESC")
    List<InventoryLogEntity> findRecentLogs();

    // Get logs by multiple change types
    List<InventoryLogEntity> findByChangeTypeInOrderByCreatedAtDesc(List<InventoryChangeType> changeTypes);

    // Search logs by reason (case-insensitive)
    @Query("SELECT il FROM InventoryLogEntity il WHERE LOWER(il.reason) LIKE LOWER(CONCAT('%', :reason, '%')) " +
            "ORDER BY il.createdAt DESC")
    List<InventoryLogEntity> findByReasonContainingIgnoreCase(@Param("reason") String reason);

    // Get inventory activity summary by date
    @Query("SELECT il.changeType, COUNT(il), SUM(il.quantity) FROM InventoryLogEntity il " +
            "WHERE il.createdAt BETWEEN :startDate AND :endDate GROUP BY il.changeType")
    List<Object[]> getInventoryActivitySummary(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
}