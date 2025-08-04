package com.residuesolution.pos.service;

import com.residuesolution.pos.dto.AnalyticsReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AnalyticsReportService {

    // Core report generation methods
    AnalyticsReport generateSalesReport(String generatedBy, LocalDateTime startDate, LocalDateTime endDate, Map<String, Object> filters);

    AnalyticsReport generateInventoryReport(String generatedBy, Map<String, Object> filters);

    AnalyticsReport generateCustomerReport(String generatedBy, Map<String, Object> filters);

    AnalyticsReport generatePerformanceReport(String generatedBy, LocalDateTime startDate, LocalDateTime endDate, Map<String, Object> filters);

    // Report management methods
    AnalyticsReport getReportById(Integer reportId);

    List<AnalyticsReport> getAllReports();

    List<AnalyticsReport> getReportsByType(String reportType);

    List<AnalyticsReport> getReportsByUser(String generatedBy);

    List<AnalyticsReport> getRecentReports();

    Boolean deleteReport(Integer reportId);

    // Report data methods
    Map<String, Object> getSalesAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    Map<String, Object> getInventoryAnalytics();

    Map<String, Object> getCustomerAnalytics();

    Map<String, Object> getPerformanceAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    // Export methods
    String exportReportToPDF(Integer reportId);

    String exportReportToCSV(Integer reportId);

    // Utility methods
    Boolean updateReportStatus(Integer reportId, String status);

    Map<String, Object> getReportStatistics();
}