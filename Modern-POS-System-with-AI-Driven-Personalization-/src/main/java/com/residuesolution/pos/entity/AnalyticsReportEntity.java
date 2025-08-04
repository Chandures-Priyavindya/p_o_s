package com.residuesolution.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_reports")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType; // sales, inventory, customer, performance

    @Column(name = "generated_by", nullable = false, length = 100)
    private String generatedBy; // user who generated the report

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "report_data", columnDefinition = "TEXT")
    private String reportData; // JSON string containing the actual report data

    @Column(name = "report_title", length = 255)
    private String reportTitle;

    @Column(name = "report_description", columnDefinition = "TEXT")
    private String reportDescription;

    @Column(name = "filters", columnDefinition = "TEXT")
    private String filters; // JSON string containing applied filters

    @Column(name = "status", length = 20, nullable = false)
    private String status = "GENERATING"; // GENERATING, COMPLETED, FAILED

    @Column(name = "file_url", length = 500)
    private String fileUrl; // URL to downloadable report file

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.generatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "GENERATING";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}