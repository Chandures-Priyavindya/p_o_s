package com.residuesolution.pos.dto;

import com.residuesolution.pos.enums.PaymentMethod;
import com.residuesolution.pos.enums.PaymentStatus;
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
public class Payment {
    private Integer paymentId;
    private Integer orderId;
    private Integer customerId;
    private Integer processedByUserId; // Which user processed this paymment
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionReference; // External payment gateway reference
    private String gateway; // Payment gateway used (e.g., "STRIPE", "PAYPAL", "SQUARE", "INTERNAL")
    private String cardLast4Digits; // For card payments (security)
    private BigDecimal changeAmount; // For cash payments
    private String notes;
    private LocalDateTime paymentDateTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional fields for comprehensive payment tracking
    private String gatewayResponse; // Response from payment gateway
    private BigDecimal refundedAmount; // Track refunds
    private Boolean isRefundable = true;
}