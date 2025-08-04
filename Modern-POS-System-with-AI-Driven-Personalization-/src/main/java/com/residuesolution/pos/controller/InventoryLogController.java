package com.residuesolution.pos.controller;

import com.residuesolution.pos.dto.InventoryLog;
import com.residuesolution.pos.enums.InventoryChangeType;
import com.residuesolution.pos.service.InventoryLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory-log")
@CrossOrigin
@RequiredArgsConstructor
public class InventoryLogController {

    private final InventoryLogService inventoryLogService;

    // ================================================================================================
    // ✅ ADMIN: Full access (track changes, add/remove stock)
    // ================================================================================================

    // ✅ Full access - Create inventory log entry
    @PostMapping("/log")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> logInventoryChange(@RequestBody InventoryLog inventoryLog) {
        Boolean isLogged = inventoryLogService.logInventoryChange(inventoryLog);

        if (isLogged) {
            return ResponseEntity.ok("Inventory change logged successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to log inventory change");
        }
    }

    // ✅ Full access - Add stock to inventory
    @PostMapping("/add-stock")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addStock(@RequestBody Map<String, Object> request) {
        try {
            Integer productId = Integer.valueOf(request.get("productId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            Integer userId = Integer.valueOf(request.get("userId").toString());
            String reason = request.get("reason").toString();
            String referenceId = request.get("referenceId") != null ?
                    request.get("referenceId").toString() : null;

            Boolean isAdded = inventoryLogService.addStock(productId, quantity, userId, reason, referenceId);

            if (isAdded) {
                return ResponseEntity.ok("Stock added successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to add stock");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }

    // ✅ Full access - Remove stock from inventory
    @PostMapping("/remove-stock")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> removeStock(@RequestBody Map<String, Object> request) {
        try {
            Integer productId = Integer.valueOf(request.get("productId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            Integer userId = Integer.valueOf(request.get("userId").toString());
            String reason = request.get("reason").toString();
            String referenceId = request.get("referenceId") != null ?
                    request.get("referenceId").toString() : null;

            Boolean isRemoved = inventoryLogService.removeStock(productId, quantity, userId, reason, referenceId);

            if (isRemoved) {
                return ResponseEntity.ok("Stock removed successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to remove stock (insufficient stock or error)");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }

    // ✅ Full access - Adjust stock (inventory count correction)
    @PostMapping("/adjust-stock")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> adjustStock(@RequestBody Map<String, Object> request) {
        try {
            Integer productId = Integer.valueOf(request.get("productId").toString());
            Integer newQuantity = Integer.valueOf(request.get("newQuantity").toString());
            Integer userId = Integer.valueOf(request.get("userId").toString());
            String reason = request.get("reason").toString();

            Boolean isAdjusted = inventoryLogService.adjustStock(productId, newQuantity, userId, reason);

            if (isAdjusted) {
                return ResponseEntity.ok("Stock adjusted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to adjust stock");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }

    // ✅ Full access - Get all inventory logs
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<InventoryLog>> getAllInventoryLogs() {
        List<InventoryLog> logs = inventoryLogService.getAllInventoryLogs();

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ✅ Full access - Get inventory log by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<InventoryLog> getInventoryLogById(@PathVariable Integer id) {
        InventoryLog log = inventoryLogService.getInventoryLogById(id);

        if (log != null) {
            return ResponseEntity.ok(log);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Full access - Get logs by product ID
    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<InventoryLog>> getLogsByProductId(@PathVariable Integer productId) {
        List<InventoryLog> logs = inventoryLogService.getLogsByProductId(productId);

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ✅ Full access - Get logs by user ID
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<InventoryLog>> getLogsByUserId(@PathVariable Integer userId) {
        List<InventoryLog> logs = inventoryLogService.getLogsByUserId(userId);

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ✅ Full access - Get logs by change type
    @GetMapping("/change-type/{changeType}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<InventoryLog>> getLogsByChangeType(@PathVariable String changeType) {
        try {
            InventoryChangeType type = InventoryChangeType.valueOf(changeType.toUpperCase());
            List<InventoryLog> logs = inventoryLogService.getLogsByChangeType(type);

            if (logs.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(logs);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Full access - Get logs between dates
    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<InventoryLog>> getLogsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<InventoryLog> logs = inventoryLogService.getLogsBetweenDates(startDate, endDate);

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ✅ Full access - Get logs by product and date range
    @GetMapping("/product/{productId}/date-range")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<InventoryLog>> getLogsByProductAndDateRange(
            @PathVariable Integer productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<InventoryLog> logs = inventoryLogService.getLogsByProductAndDateRange(productId, startDate, endDate);

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ✅ Full access - Get logs by user and date range
    @GetMapping("/user/{userId}/date-range")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<InventoryLog>> getLogsByUserAndDateRange(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<InventoryLog> logs = inventoryLogService.getLogsByUserAndDateRange(userId, startDate, endDate);

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ✅ Full access - Get logs by reference ID (order, return, etc.)
    @GetMapping("/reference/{referenceId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<InventoryLog>> getLogsByReferenceId(@PathVariable String referenceId) {
        List<InventoryLog> logs = inventoryLogService.getLogsByReferenceId(referenceId);

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ✅ Full access - Get system-generated logs
    @GetMapping("/system-generated")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<InventoryLog>> getSystemGeneratedLogs() {
        List<InventoryLog> logs = inventoryLogService.getSystemGeneratedLogs();

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ✅ Full access - Get manual logs
    @GetMapping("/manual")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<InventoryLog>> getManualLogs() {
        List<InventoryLog> logs = inventoryLogService.getManualLogs();

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ✅ Full access - Get inventory activity summary
    @GetMapping("/activity-summary")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getInventoryActivitySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<Map<String, Object>> summary = inventoryLogService.getInventoryActivitySummary(startDate, endDate);

        if (summary.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(summary);
    }

    // ✅ Full access - Get total quantity changes for product
    @GetMapping("/product/{productId}/total-changes")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getTotalQuantityChangesForProduct(
            @PathVariable Integer productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Integer totalChanges = inventoryLogService.getTotalQuantityChangesForProduct(productId, startDate, endDate);

        return ResponseEntity.ok(Map.of(
                "productId", productId,
                "totalQuantityChanges", totalChanges,
                "startDate", startDate,
                "endDate", endDate
        ));
    }

    // ================================================================================================
    // ✅ MANAGER: View inventory logs
    // ================================================================================================

    // ✅ View inventory logs - Get recent logs (limited access)
    @GetMapping("/recent")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<InventoryLog>> getRecentLogs(@RequestParam(defaultValue = "50") Integer limit) {
        List<InventoryLog> logs = inventoryLogService.getRecentLogs(limit);

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ✅ View inventory logs - Search logs by reason
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<InventoryLog>> searchLogsByReason(@RequestParam String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<InventoryLog> logs = inventoryLogService.searchLogsByReason(reason.trim());

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ✅ View inventory logs - Get logs by multiple change types
    @PostMapping("/change-types")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<InventoryLog>> getLogsByMultipleChangeTypes(
            @RequestBody Map<String, List<String>> request) {

        try {
            List<String> changeTypeStrings = request.get("changeTypes");
            List<InventoryChangeType> changeTypes = changeTypeStrings.stream()
                    .map(type -> InventoryChangeType.valueOf(type.toUpperCase()))
                    .toList();

            List<InventoryLog> logs = inventoryLogService.getLogsByMultipleChangeTypes(changeTypes);

            if (logs.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(logs);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ View inventory logs - Get logs for specific product (managers can view)
    @GetMapping("/product/{productId}/view")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<InventoryLog>> viewLogsByProductId(@PathVariable Integer productId) {
        List<InventoryLog> logs = inventoryLogService.getLogsByProductId(productId);

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }

    // ================================================================================================
    // ✅ UTILITY ENDPOINTS
    // ================================================================================================

    // ✅ Get available change types
    @GetMapping("/change-types")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<Map<String, Object>> getAvailableChangeTypes() {
        return ResponseEntity.ok(Map.of(
                "changeTypes", List.of(
                        "ADD", "REMOVE", "ADJUST", "SALE", "SALE_RETURN",
                        "PURCHASE", "PURCHASE_RETURN", "DAMAGE", "THEFT",
                        "EXPIRED", "TRANSFER_IN", "TRANSFER_OUT",
                        "INITIAL_STOCK", "AUDIT_ADJUSTMENT"
                ),
                "descriptions", Map.of(
                )
        ));
    }

    // ✅ Validate inventory log data
    @PostMapping("/validate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> validateInventoryLog(@RequestBody InventoryLog inventoryLog) {
        Boolean isValid = inventoryLogService.validateInventoryLog(inventoryLog);

        return ResponseEntity.ok(Map.of(
                "valid", isValid,
                "message", isValid ? "Inventory log data is valid" : "Inventory log data validation failed"
        ));
    }

    // ✅ Test endpoint to verify controller is working
    @GetMapping("/test")
    public ResponseEntity<String> testInventoryLogController() {
        return ResponseEntity.ok("Inventory Log Controller is working! Full access (track changes, add/remove stock) and view inventory logs available.");
    }
}