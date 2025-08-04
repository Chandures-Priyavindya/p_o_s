package com.residuesolution.pos.service;

import com.residuesolution.pos.dto.InventoryLog;
import com.residuesolution.pos.enums.InventoryChangeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface InventoryLogService {

    // Core logging operations
    Boolean logInventoryChange(InventoryLog inventoryLog);

    Boolean logInventoryChange(Integer productId, Integer changedBy, InventoryChangeType changeType,
                               Integer quantity, String reason, Integer previousStock, Integer newStock);

    // Retrieve logs
    List<InventoryLog> getAllInventoryLogs();

    InventoryLog getInventoryLogById(Integer id);

    List<InventoryLog> getLogsByProductId(Integer productId);

    List<InventoryLog> getLogsByUserId(Integer userId);

    List<InventoryLog> getLogsByChangeType(InventoryChangeType changeType);

    // Date-based queries
    List<InventoryLog> getLogsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    List<InventoryLog> getLogsByProductAndDateRange(Integer productId, LocalDateTime startDate, LocalDateTime endDate);

    List<InventoryLog> getLogsByUserAndDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate);

    // Reference-based queries
    List<InventoryLog> getLogsByReferenceId(String referenceId);

    List<InventoryLog> getSystemGeneratedLogs();

    List<InventoryLog> getManualLogs();

    // Analytics and reporting
    Integer getTotalQuantityChangesForProduct(Integer productId, LocalDateTime startDate, LocalDateTime endDate);

    List<Map<String, Object>> getInventoryActivitySummary(LocalDateTime startDate, LocalDateTime endDate);

    List<InventoryLog> getRecentLogs(Integer limit);

    // Search functionality
    List<InventoryLog> searchLogsByReason(String reason);

    List<InventoryLog> getLogsByMultipleChangeTypes(List<InventoryChangeType> changeTypes);

    // Stock tracking operations
    Boolean addStock(Integer productId, Integer quantity, Integer userId, String reason, String referenceId);

    Boolean removeStock(Integer productId, Integer quantity, Integer userId, String reason, String referenceId);

    Boolean adjustStock(Integer productId, Integer newQuantity, Integer userId, String reason);

    // Validation
    Boolean validateInventoryLog(InventoryLog inventoryLog);
}