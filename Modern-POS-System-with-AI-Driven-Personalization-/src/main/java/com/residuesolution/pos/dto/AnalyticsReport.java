package com.residuesolution.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnalyticsReport {
    private Integer id;
    private String reportType; // sales, inventory, customer, performance
    private String generatedBy; // user who generated the report
    private LocalDateTime generatedAt;
    private String reportData; // JSON string containing the actual report data
    private String reportTitle;
    private String reportDescription;
    private String filters; // JSON string containing applied filters
    private String status; // GENERATING, COMPLETED, FAILED
    private String fileUrl; // URL to downloadable report file (PDF/CSV)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}