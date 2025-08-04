package com.residuesolution.pos.controller;

import com.residuesolution.pos.dto.AnalyticsReport;
import com.residuesolution.pos.service.AnalyticsReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * AnalyticsReportController - Role-based access control as per requirements:
 *
 * ✅ ADMIN: Full access to all reports (sales, inventory, customer, performance)
 * ✅ MANAGER: View sales, inventory, and performance reports (NO access to analytics reports)
 * ❌ CASHIER: No access to analytics reports
 */
@RestController
@RequestMapping("/api/analytics-reports")
@CrossOrigin
@RequiredArgsConstructor
public class AnalyticsReportController {

    private final AnalyticsReportService analyticsReportService;

    // ================================================================================================
    // ✅ ADMIN ONLY: Full access to all reports (sales, inventory, customer, performance)
    // ================================================================================================

    /**
     * Generate Sales Report - ADMIN ONLY
     */
    @PostMapping("/generate/sales")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AnalyticsReport> generateSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "admin") String generatedBy,
            @RequestBody(required = false) Map<String, Object> filters) {

        if (filters == null) {
            filters = Map.of("reportType", "sales");
        }

        AnalyticsReport report = analyticsReportService.generateSalesReport(generatedBy, startDate, endDate, filters);

        if (report != null) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Generate Inventory Report - ADMIN ONLY
     */
    @PostMapping("/generate/inventory")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AnalyticsReport> generateInventoryReport(
            @RequestParam(defaultValue = "admin") String generatedBy,
            @RequestBody(required = false) Map<String, Object> filters) {

        if (filters == null) {
            filters = Map.of("reportType", "inventory");
        }

        AnalyticsReport report = analyticsReportService.generateInventoryReport(generatedBy, filters);

        if (report != null) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Generate Customer Report - ADMIN ONLY
     */
    @PostMapping("/generate/customer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AnalyticsReport> generateCustomerReport(
            @RequestParam(defaultValue = "admin") String generatedBy,
            @RequestBody(required = false) Map<String, Object> filters) {

        if (filters == null) {
            filters = Map.of("reportType", "customer");
        }

        AnalyticsReport report = analyticsReportService.generateCustomerReport(generatedBy, filters);

        if (report != null) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Generate Performance Report - ADMIN ONLY
     */
    @PostMapping("/generate/performance")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AnalyticsReport> generatePerformanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "admin") String generatedBy,
            @RequestBody(required = false) Map<String, Object> filters) {

        if (filters == null) {
            filters = Map.of("reportType", "performance");
        }

        AnalyticsReport report = analyticsReportService.generatePerformanceReport(generatedBy, startDate, endDate, filters);

        if (report != null) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get All Reports - ADMIN ONLY
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AnalyticsReport>> getAllReports() {
        List<AnalyticsReport> reports = analyticsReportService.getAllReports();

        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reports);
    }

    /**
     * Get Report by ID - ADMIN ONLY
     */
    @GetMapping("/{reportId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AnalyticsReport> getReportById(@PathVariable Integer reportId) {
        AnalyticsReport report = analyticsReportService.getReportById(reportId);

        if (report != null) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete Report - ADMIN ONLY
     */
    @DeleteMapping("/delete/{reportId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteReport(@PathVariable Integer reportId) {
        Boolean isDeleted = analyticsReportService.deleteReport(reportId);

        if (isDeleted) {
            return ResponseEntity.ok("Analytics report deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ================================================================================================
    // ✅ MANAGER: View sales, inventory, and performance reports (NO access to analytics reports)
    // ================================================================================================

    /**
     * View Sales Reports - ADMIN & MANAGER
     */
    @GetMapping("/sales")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<AnalyticsReport>> getSalesReports() {
        List<AnalyticsReport> reports = analyticsReportService.getReportsByType("sales");

        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reports);
    }

    /**
     * View Inventory Reports - ADMIN & MANAGER
     */
    @GetMapping("/inventory")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<AnalyticsReport>> getInventoryReports() {
        List<AnalyticsReport> reports = analyticsReportService.getReportsByType("inventory");

        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reports);
    }

    /**
     * View Performance Reports - ADMIN & MANAGER
     */
    @GetMapping("/performance")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<AnalyticsReport>> getPerformanceReports() {
        List<AnalyticsReport> reports = analyticsReportService.getReportsByType("performance");

        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reports);
    }

    /**
     * Get Sales Analytics Data - ADMIN & MANAGER
     */
    @GetMapping("/analytics/sales")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> getSalesAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Map<String, Object> analytics = analyticsReportService.getSalesAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get Inventory Analytics Data - ADMIN & MANAGER
     */
    @GetMapping("/analytics/inventory")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> getInventoryAnalytics() {
        Map<String, Object> analytics = analyticsReportService.getInventoryAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get Performance Analytics Data - ADMIN & MANAGER
     */
    @GetMapping("/analytics/performance")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> getPerformanceAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Map<String, Object> analytics = analyticsReportService.getPerformanceAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    // ================================================================================================
    // ❌ CASHIER: No access to analytics reports (as per requirement image)
    // ================================================================================================

    // ================================================================================================
    // ✅ UTILITY ENDPOINTS
    // ================================================================================================

    /**
     * Get Recent Reports - ADMIN & MANAGER
     */
    @GetMapping("/recent")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<AnalyticsReport>> getRecentReports() {
        List<AnalyticsReport> reports = analyticsReportService.getRecentReports();

        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reports);
    }

    /**
     * Get Reports by User - ADMIN & MANAGER (can view their own reports)
     */
    @GetMapping("/user/{generatedBy}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<AnalyticsReport>> getReportsByUser(@PathVariable String generatedBy) {
        List<AnalyticsReport> reports = analyticsReportService.getReportsByUser(generatedBy);

        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reports);
    }

    /**
     * Get Report Statistics - ADMIN ONLY
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getReportStatistics() {
        Map<String, Object> statistics = analyticsReportService.getReportStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Update Report Status - ADMIN ONLY
     */
    @PutMapping("/status/{reportId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateReportStatus(
            @PathVariable Integer reportId,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");

        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Status is required");
        }

        // Validate status values
        if (!Arrays.asList("GENERATING", "COMPLETED", "FAILED").contains(status.toUpperCase())) {
            return ResponseEntity.badRequest().body("Invalid status. Valid values: GENERATING, COMPLETED, FAILED");
        }

        Boolean isUpdated = analyticsReportService.updateReportStatus(reportId, status.toUpperCase());

        if (isUpdated) {
            return ResponseEntity.ok("Report status updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Export Report to PDF - ADMIN & MANAGER
     */
    @GetMapping("/export/pdf/{reportId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, String>> exportReportToPDF(@PathVariable Integer reportId) {
        String downloadUrl = analyticsReportService.exportReportToPDF(reportId);

        if (downloadUrl != null) {
            return ResponseEntity.ok(Map.of(
                    "message", "PDF export generated successfully",
                    "downloadUrl", downloadUrl,
                    "reportId", reportId.toString()
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Export Report to CSV - ADMIN & MANAGER
     */
    @GetMapping("/export/csv/{reportId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, String>> exportReportToCSV(@PathVariable Integer reportId) {
        String downloadUrl = analyticsReportService.exportReportToCSV(reportId);

        if (downloadUrl != null) {
            return ResponseEntity.ok(Map.of(
                    "message", "CSV export generated successfully",
                    "downloadUrl", downloadUrl,
                    "reportId", reportId.toString()
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get Available Report Types - ADMIN & MANAGER
     */
    @GetMapping("/types")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> getAvailableReportTypes() {
        return ResponseEntity.ok(Map.of(
                "reportTypes", Arrays.asList(
                        Map.of("type", "sales", "description", "Sales performance and revenue analysis", "adminOnly", false),
                        Map.of("type", "inventory", "description", "Stock levels and product performance", "adminOnly", false),
                        Map.of("type", "customer", "description", "Customer behavior and demographics", "adminOnly", true),
                        Map.of("type", "performance", "description", "System and staff performance metrics", "adminOnly", false)
                ),
                "accessLevels", Map.of(
                        "ADMIN", "Full access to all report types",
                        "MANAGER", "Access to sales, inventory, and performance reports",
                        "CASHIER", "No access to analytics reports"
                )
        ));
    }

    /**
     * Generate Quick Sales Summary - ADMIN & MANAGER
     */
    @GetMapping("/quick-summary/sales")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> getQuickSalesSummary() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7); // Last 7 days

        Map<String, Object> analytics = analyticsReportService.getSalesAnalytics(startDate, endDate);

        return ResponseEntity.ok(Map.of(
                "period", "Last 7 Days",
                "summary", analytics,
                "generatedAt", LocalDateTime.now()
        ));
    }

    /**
     * Generate Quick Inventory Summary - ADMIN & MANAGER
     */
    @GetMapping("/quick-summary/inventory")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> getQuickInventorySummary() {
        Map<String, Object> analytics = analyticsReportService.getInventoryAnalytics();

        return ResponseEntity.ok(Map.of(
                "period", "Current Status",
                "summary", analytics,
                "generatedAt", LocalDateTime.now()
        ));
    }

    /**
     * Generate Quick Performance Summary - ADMIN & MANAGER
     */
    @GetMapping("/quick-summary/performance")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> getQuickPerformanceSummary() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30); // Last 30 days

        Map<String, Object> analytics = analyticsReportService.getPerformanceAnalytics(startDate, endDate);

        return ResponseEntity.ok(Map.of(
                "period", "Last 30 Days",
                "summary", analytics,
                "generatedAt", LocalDateTime.now()
        ));
    }

    /**
     * Get Customer Analytics - ADMIN ONLY (Customer reports restricted for managers)
     */
    @GetMapping("/analytics/customer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getCustomerAnalytics() {
        Map<String, Object> analytics = analyticsReportService.getCustomerAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Generate All Reports Dashboard - ADMIN ONLY
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> generateDashboard() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30); // Last 30 days

        Map<String, Object> salesAnalytics = analyticsReportService.getSalesAnalytics(startDate, endDate);
        Map<String, Object> inventoryAnalytics = analyticsReportService.getInventoryAnalytics();
        Map<String, Object> customerAnalytics = analyticsReportService.getCustomerAnalytics();
        Map<String, Object> performanceAnalytics = analyticsReportService.getPerformanceAnalytics(startDate, endDate);
        Map<String, Object> reportStats = analyticsReportService.getReportStatistics();

        return ResponseEntity.ok(Map.of(
                "dashboard", Map.of(
                        "period", "Last 30 Days",
                        "sales", salesAnalytics,
                        "inventory", inventoryAnalytics,
                        "customers", customerAnalytics,
                        "performance", performanceAnalytics,
                        "reportStatistics", reportStats
                ),
                "generatedAt", LocalDateTime.now(),
                "accessLevel", "ADMIN"
        ));
    }

    /**
     * Generate Manager Dashboard - ADMIN & MANAGER
     */
    @GetMapping("/dashboard/manager")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> generateManagerDashboard() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30); // Last 30 days

        Map<String, Object> salesAnalytics = analyticsReportService.getSalesAnalytics(startDate, endDate);
        Map<String, Object> inventoryAnalytics = analyticsReportService.getInventoryAnalytics();
        Map<String, Object> performanceAnalytics = analyticsReportService.getPerformanceAnalytics(startDate, endDate);

        return ResponseEntity.ok(Map.of(
                "dashboard", Map.of(
                        "period", "Last 30 Days",
                        "sales", salesAnalytics,
                        "inventory", inventoryAnalytics,
                        "performance", performanceAnalytics
                        // Note: No customer analytics for managers
                ),
                "generatedAt", LocalDateTime.now(),
                "accessLevel", "MANAGER",
                "note", "Customer analytics not available for managers"
        ));
    }

    /**
     * Bulk Report Generation - ADMIN ONLY
     */
    @PostMapping("/generate/bulk")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> generateBulkReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "admin") String generatedBy,
            @RequestBody(required = false) Map<String, Object> filters) {

        if (filters == null) {
            filters = Map.of("reportType", "bulk");
        }

        // Generate all report types
        AnalyticsReport salesReport = analyticsReportService.generateSalesReport(generatedBy, startDate, endDate, filters);
        AnalyticsReport inventoryReport = analyticsReportService.generateInventoryReport(generatedBy, filters);
        AnalyticsReport customerReport = analyticsReportService.generateCustomerReport(generatedBy, filters);
        AnalyticsReport performanceReport = analyticsReportService.generatePerformanceReport(generatedBy, startDate, endDate, filters);

        return ResponseEntity.ok(Map.of(
                "message", "Bulk reports generated successfully",
                "reports", Map.of(
                        "sales", salesReport != null ? salesReport.getId() : "Failed",
                        "inventory", inventoryReport != null ? inventoryReport.getId() : "Failed",
                        "customer", customerReport != null ? customerReport.getId() : "Failed",
                        "performance", performanceReport != null ? performanceReport.getId() : "Failed"
                ),
                "generatedBy", generatedBy,
                "generatedAt", LocalDateTime.now()
        ));
    }

    /**
     * Test endpoint to verify authentication
     */
    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("Analytics Reports module authentication is working!");
    }
}