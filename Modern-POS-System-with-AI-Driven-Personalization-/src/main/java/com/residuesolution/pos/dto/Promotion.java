package com.residuesolution.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Promotion {
    private Integer id;
    private String discountType;  // PERCENTAGE, FIXED_AMOUNT, BUY_ONE_GET_ONE, etc.
    private String title;
    private String description;
    private BigDecimal discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive = true;
}