package com.residuesolution.pos.service.impl;

import com.residuesolution.pos.dto.InventoryLog;
import com.residuesolution.pos.entity.InventoryLogEntity;
import com.residuesolution.pos.enums.InventoryChangeType;
import com.residuesolution.pos.repository.InventoryLogRepository;
import com.residuesolution.pos.service.InventoryLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryLogServiceImpl implements InventoryLogService {

    private final InventoryLogRepository inventoryLogRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public Boolean logInventoryChange(InventoryLog inventoryLog) {
        try {
            if (!validateInventoryLog(inventoryLog)) {
                log.error("Invalid inventory log: {}", inventoryLog);
                return false;
            }

            InventoryLogEntity entity = mapper.map(inventoryLog, InventoryLogEntity.class);
            InventoryLogEntity savedLog = inventoryLogRepository.save(entity);

            if (savedLog != null) {
                log.info("Inventory change logged successfully: Product ID {}, Change Type {}, Quantity {}",
                        inventoryLog.getProductId(), inventoryLog.getChangeType(), inventoryLog.getQuantity());
                return true;
            }

            return false;

        } catch (Exception e) {
            log.error("Error logging inventory change: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean logInventoryChange(Integer productId, Integer changedBy, InventoryChangeType changeType,
                                      Integer quantity, String reason, Integer previousStock, Integer newStock) {

        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setProductId(productId);
        inventoryLog.setChangedBy(changedBy);
        inventoryLog.setChangeType(changeType);
        inventoryLog.setQuantity(quantity);
        inventoryLog.setReason(reason);
        inventoryLog.setPreviousStock(previousStock);
        inventoryLog.setNewStock(newStock);
        inventoryLog.setIsSystemGenerated(false);

        return logInventoryChange(inventoryLog);
    }

    @Override
    public List<InventoryLog> getAllInventoryLogs() {
        List<InventoryLogEntity> entities = inventoryLogRepository.findAll();
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public InventoryLog getInventoryLogById(Integer id) {
        Optional<InventoryLogEntity> entity = inventoryLogRepository.findById(id);
        return entity.map(inventoryLogEntity -> mapper.map(inventoryLogEntity, InventoryLog.class))
                .orElse(null);
    }

    @Override
    public List<InventoryLog> getLogsByProductId(Integer productId) {
        List<InventoryLogEntity> entities = inventoryLogRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public List<InventoryLog> getLogsByUserId(Integer userId) {
        List<InventoryLogEntity> entities = inventoryLogRepository.findByChangedByOrderByCreatedAtDesc(userId);
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public List<InventoryLog> getLogsByChangeType(InventoryChangeType changeType) {
        List<InventoryLogEntity> entities = inventoryLogRepository.findByChangeTypeOrderByCreatedAtDesc(changeType);
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public List<InventoryLog> getLogsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        List<InventoryLogEntity> entities = inventoryLogRepository.findLogsBetweenDates(startDate, endDate);
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public List<InventoryLog> getLogsByProductAndDateRange(Integer productId, LocalDateTime startDate, LocalDateTime endDate) {
        List<InventoryLogEntity> entities = inventoryLogRepository.findByProductAndDateRange(productId, startDate, endDate);
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public List<InventoryLog> getLogsByUserAndDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<InventoryLogEntity> entities = inventoryLogRepository.findByUserAndDateRange(userId, startDate, endDate);
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public List<InventoryLog> getLogsByReferenceId(String referenceId) {
        List<InventoryLogEntity> entities = inventoryLogRepository.findByReferenceIdOrderByCreatedAtDesc(referenceId);
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public List<InventoryLog> getSystemGeneratedLogs() {
        List<InventoryLogEntity> entities = inventoryLogRepository.findByIsSystemGeneratedOrderByCreatedAtDesc(true);
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public List<InventoryLog> getManualLogs() {
        List<InventoryLogEntity> entities = inventoryLogRepository.findByIsSystemGeneratedOrderByCreatedAtDesc(false);
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public Integer getTotalQuantityChangesForProduct(Integer productId, LocalDateTime startDate, LocalDateTime endDate) {
        Integer total = inventoryLogRepository.getTotalQuantityChangesForProduct(productId, startDate, endDate);
        return total != null ? total : 0;
    }

    @Override
    public List<Map<String, Object>> getInventoryActivitySummary(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = inventoryLogRepository.getInventoryActivitySummary(startDate, endDate);

        return results.stream()
                .map(result -> Map.of(
                        "changeType", result[0] != null ? result[0].toString() : "UNKNOWN",
                        "count", result[1] != null ? result[1] : 0L,
                        "totalQuantity", result[2] != null ? result[2] : 0
                ))
                .toList();
    }

    @Override
    public List<InventoryLog> getRecentLogs(Integer limit) {
        // Use PageRequest to limit results
        List<InventoryLogEntity> entities = inventoryLogRepository.findRecentLogs();

        return entities.stream()
                .limit(limit != null ? limit : 50) // Default to 50 if no limit specified
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public List<InventoryLog> searchLogsByReason(String reason) {
        List<InventoryLogEntity> entities = inventoryLogRepository.findByReasonContainingIgnoreCase(reason);
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    public List<InventoryLog> getLogsByMultipleChangeTypes(List<InventoryChangeType> changeTypes) {
        List<InventoryLogEntity> entities = inventoryLogRepository.findByChangeTypeInOrderByCreatedAtDesc(changeTypes);
        return entities.stream()
                .map(entity -> mapper.map(entity, InventoryLog.class))
                .toList();
    }

    @Override
    @Transactional
    public Boolean addStock(Integer productId, Integer quantity, Integer userId, String reason, String referenceId) {
        try {
            // You would typically get the current stock from a Product entity
            // For now, we'll use 0 as previous stock (you should implement product stock tracking)
            Integer previousStock = getCurrentStock(productId);
            Integer newStock = previousStock + quantity;

            InventoryLog log = new InventoryLog();
            log.setProductId(productId);
            log.setChangedBy(userId);
            log.setChangeType(InventoryChangeType.ADD);
            log.setQuantity(quantity);
            log.setReason(reason);
            log.setReferenceId(referenceId);
            log.setPreviousStock(previousStock);
            log.setNewStock(newStock);
            log.setIsSystemGenerated(false);

            Boolean logged = logInventoryChange(log);

            if (logged) {
                // Here you would update the actual product stock in the database
                updateProductStock(productId, newStock);
                log.info("Stock added successfully: Product ID {}, Quantity {}", productId, quantity);
            }

            return logged;

        } catch (Exception e) {
            log.error("Error adding stock: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean removeStock(Integer productId, Integer quantity, Integer userId, String reason, String referenceId) {
        try {
            Integer previousStock = getCurrentStock(productId);

            if (previousStock < quantity) {
                log.warn("Insufficient stock for product ID {}: Available {}, Requested {}",
                        productId, previousStock, quantity);
                return false;
            }

            Integer newStock = previousStock - quantity;

            InventoryLog log = new InventoryLog();
            log.setProductId(productId);
            log.setChangedBy(userId);
            log.setChangeType(InventoryChangeType.REMOVE);
            log.setQuantity(-quantity); // Negative quantity for removal
            log.setReason(reason);
            log.setReferenceId(referenceId);
            log.setPreviousStock(previousStock);
            log.setNewStock(newStock);
            log.setIsSystemGenerated(false);

            Boolean logged = logInventoryChange(log);

            if (logged) {
                updateProductStock(productId, newStock);
                log.info("Stock removed successfully: Product ID {}, Quantity {}", productId, quantity);
            }

            return logged;

        } catch (Exception e) {
            log.error("Error removing stock: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean adjustStock(Integer productId, Integer newQuantity, Integer userId, String reason) {
        try {
            Integer previousStock = getCurrentStock(productId);
            Integer quantityChange = newQuantity - previousStock;

            InventoryLog log = new InventoryLog();
            log.setProductId(productId);
            log.setChangedBy(userId);
            log.setChangeType(InventoryChangeType.ADJUST);
            log.setQuantity(quantityChange);
            log.setReason(reason);
            log.setPreviousStock(previousStock);
            log.setNewStock(newQuantity);
            log.setIsSystemGenerated(false);

            Boolean logged = logInventoryChange(log);

            if (logged) {
                updateProductStock(productId, newQuantity);
                log.info("Stock adjusted successfully: Product ID {}, From {} to {}",
                        productId, previousStock, newQuantity);
            }

            return logged;

        } catch (Exception e) {
            log.error("Error adjusting stock: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Boolean validateInventoryLog(InventoryLog inventoryLog) {
        if (inventoryLog == null) {
            return false;
        }

        if (inventoryLog.getProductId() == null) {
            log.error("Product ID is required for inventory log");
            return false;
        }

        if (inventoryLog.getChangedBy() == null) {
            log.error("Changed by user ID is required for inventory log");
            return false;
        }

        if (inventoryLog.getChangeType() == null) {
            log.error("Change type is required for inventory log");
            return false;
        }

        if (inventoryLog.getQuantity() == null) {
            log.error("Quantity is required for inventory log");
            return false;
        }

        return true;
    }

    // Helper methods (you should implement these based on your Product entity)
    private Integer getCurrentStock(Integer productId) {
        // TODO: Implement this method to get current stock from Product entity
        // For now, returning a dummy value
        log.warn("getCurrentStock method needs to be implemented for Product entity integration");
        return 100; // Dummy value
    }

    private void updateProductStock(Integer productId, Integer newStock) {
        // TODO: Implement this method to update actual product stock
        log.warn("updateProductStock method needs to be implemented for Product entity integration");
        log.info("Would update Product ID {} stock to {}", productId, newStock);
    }
}