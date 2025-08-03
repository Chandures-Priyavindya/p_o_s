package com.residuesolution.pos.service;

import com.residuesolution.pos.dto.Payment;
import com.residuesolution.pos.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PaymentService {

    // Core payment operations
    Boolean processPayment(Payment payment);

    Boolean updatePaymentStatus(Integer paymentId, PaymentStatus status);

    Payment getPaymentById(Integer paymentId);

    List<Payment> getAllPayments();

    // Customer-specific payments
    List<Payment> getPaymentsByCustomerId(Integer customerId);

    List<Payment> getPaymentsByCustomerAndStatus(Integer customerId, PaymentStatus status);

    // User-specific payments (for cashiers/managers to see their processed payments)
    List<Payment> getPaymentsByUserId(Integer userId);

    // Order-specific payments
    List<Payment> getPaymentsByOrderId(Integer orderId);

    // Payment status operations
    List<Payment> getPaymentsByStatus(PaymentStatus status);

    Boolean cancelPayment(Integer paymentId);

    Boolean refundPayment(Integer paymentId, BigDecimal refundAmount, String reason);

    // Gateway-specific operations
    List<Payment> getPaymentsByGateway(String gateway);

    List<Payment> getPaymentsByGatewayAndStatus(String gateway, PaymentStatus status);

    BigDecimal getTotalAmountByGateway(String gateway);

    List<Map<String, Object>> getPaymentStatsByGateway();

    // Analytics and reporting methods
    BigDecimal getTotalAmountByStatus(PaymentStatus status);

    BigDecimal getTotalPaidByCustomer(Integer customerId);

    List<Payment> getPaymentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    List<Payment> getPaymentsByUserAndDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate);

    // Advanced operations
    Boolean retryFailedPayment(Integer paymentId);

    List<Payment> getRefundablePayments();

    Boolean validatePayment(Payment payment);
}