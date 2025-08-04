package com.residuesolution.pos.repository;

import com.residuesolution.pos.entity.AnalyticsReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsReportRepository extends JpaRepository<AnalyticsReportEntity, Integer> {

    // Find reports by type
    List<AnalyticsReportEntity> findByReportType(String reportType);

    // Find reports by generated user
    List<AnalyticsReportEntity> findByGeneratedBy(String generatedBy);

    // Find reports by status
    List<AnalyticsReportEntity> findByStatus(String status);

    // Find reports by type and status
    List<AnalyticsReportEntity> findByReportTypeAndStatus(String reportType, String status);

    // Find reports between dates
    @Query("SELECT r FROM AnalyticsReportEntity r WHERE r.generatedAt BETWEEN :startDate AND :endDate")
    List<AnalyticsReportEntity> findReportsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    // Find recent reports (last 30 days)
    @Query("SELECT r FROM AnalyticsReportEntity r WHERE r.generatedAt >= :thirtyDaysAgo ORDER BY r.generatedAt DESC")
    List<AnalyticsReportEntity> findRecentReports(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    // Find reports by user and type
    List<AnalyticsReportEntity> findByGeneratedByAndReportType(String generatedBy, String reportType);

    // Count reports by type
    @Query("SELECT COUNT(r) FROM AnalyticsReportEntity r WHERE r.reportType = :reportType")
    Long countReportsByType(@Param("reportType") String reportType);

    // Find completed reports only
    List<AnalyticsReportEntity> findByStatusOrderByGeneratedAtDesc(String status);
}