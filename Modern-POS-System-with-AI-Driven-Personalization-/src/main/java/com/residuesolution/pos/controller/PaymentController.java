package com.residuesolution.pos.controller;

import com.residuesolution.pos.dto.Payment;
import com.residuesolution.pos.enums.PaymentMethod;
import com.residuesolution.pos.enums.PaymentStatus;
import com.residuesolution.pos.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ✅ ADMIN: Full access (track and manage all payments)
    @PostMapping("/process")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> processPayment(@RequestBody Payment payment) {
        Boolean isProcessed = paymentService.processPayment(payment);

        if (isProcessed) {
            return ResponseEntity.ok("Payment processed successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to process payment");
        }
    }

    // ✅ ADMIN: Full access - Get all payments
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();

        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(payments);
    }

    // ✅ ADMIN: Full access - Get payment by ID
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Integer paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);

        if (payment != null) {
            return ResponseEntity.ok(payment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ ADMIN: Full access - Update payment status
    @PutMapping("/status/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updatePaymentStatus(
            @PathVariable Integer paymentId,
            @RequestBody Map<String, String> request) {

        try {
            PaymentStatus status = PaymentStatus.valueOf(request.get("status"));
            Boolean isUpdated = paymentService.updatePaymentStatus(paymentId, status);

            if (isUpdated) {
                return ResponseEntity.ok("Payment status updated successfully");
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid payment status");
        }
    }

    // ✅ ADMIN: Full access - Cancel payment
    @PutMapping("/cancel/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> cancelPayment(@PathVariable Integer paymentId) {
        Boolean isCancelled = paymentService.cancelPayment(paymentId);

        if (isCancelled) {
            return ResponseEntity.ok("Payment cancelled successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to cancel payment");
        }
    }

    // ✅ ADMIN: Full access - Process refund
    @PostMapping("/refund/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> refundPayment(
            @PathVariable Integer paymentId,
            @RequestBody Map<String, Object> request) {

        try {
            BigDecimal refundAmount = new BigDecimal(request.get("refundAmount").toString());
            String reason = request.get("reason").toString();

            Boolean isRefunded = paymentService.refundPayment(paymentId, refundAmount, reason);

            if (isRefunded) {
                return ResponseEntity.ok("Refund processed successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to process refund");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid refund request: " + e.getMessage());
        }
    }

    // ✅ ADMIN: Full access - Get payments by status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            List<Payment> payments = paymentService.getPaymentsByStatus(paymentStatus);

            if (payments.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(payments);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ ADMIN: Full access - Get payments between dates
    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Payment>> getPaymentsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<Payment> payments = paymentService.getPaymentsBetweenDates(startDate, endDate);

        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(payments);
    }

    // ✅ ADMIN: Full access - Get payments by gateway
    @GetMapping("/gateway/{gateway}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Payment>> getPaymentsByGateway(@PathVariable String gateway) {
        List<Payment> payments = paymentService.getPaymentsByGateway(gateway.toUpperCase());

        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(payments);
    }

    // ✅ ADMIN: Full access - Get payments by gateway and status
    @GetMapping("/gateway/{gateway}/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Payment>> getPaymentsByGatewayAndStatus(
            @PathVariable String gateway,
            @PathVariable String status) {

        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            List<Payment> payments = paymentService.getPaymentsByGatewayAndStatus(gateway.toUpperCase(), paymentStatus);

            if (payments.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(payments);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ ADMIN: Full access - Get total amount by gateway
    @GetMapping("/analytics/total-by-gateway/{gateway}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getTotalAmountByGateway(@PathVariable String gateway) {
        BigDecimal total = paymentService.getTotalAmountByGateway(gateway.toUpperCase());

        return ResponseEntity.ok(Map.of(
                "gateway", gateway.toUpperCase(),
                "totalAmount", total,
                "currency", "USD"
        ));
    }

    // ✅ ADMIN: Full access - Get payment statistics by gateway
    @GetMapping("/analytics/stats-by-gateway")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getPaymentStatsByGateway() {
        List<Map<String, Object>> stats = paymentService.getPaymentStatsByGateway();

        if (stats.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(stats);
    }

    // ✅ ADMIN: Full access - Get total amount by status
    @GetMapping("/analytics/total-by-status/{status}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getTotalAmountByStatus(@PathVariable String status) {
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            BigDecimal total = paymentService.getTotalAmountByStatus(paymentStatus);

            return ResponseEntity.ok(Map.of(
                    "status", status,
                    "totalAmount", total,
                    "currency", "USD"
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ ADMIN: Full access - Retry failed payment
    @PostMapping("/retry/{paymentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> retryFailedPayment(@PathVariable Integer paymentId) {
        Boolean isRetried = paymentService.retryFailedPayment(paymentId);

        if (isRetried) {
            return ResponseEntity.ok("Payment retry initiated successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to retry payment");
        }
    }

    // ✅ ADMIN: Full access - Get refundable payments
    @GetMapping("/refundable")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Payment>> getRefundablePayments() {
        List<Payment> payments = paymentService.getRefundablePayments();

        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(payments);
    }

    // ================================================================================================
    // ✅ MANAGER: View and manage payments for customers
    // ================================================================================================

    // ✅ MANAGER: View payments for specific customer
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<Payment>> getPaymentsByCustomerId(@PathVariable Integer customerId) {
        List<Payment> payments = paymentService.getPaymentsByCustomerId(customerId);

        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(payments);
    }

    // ✅ MANAGER: View customer payments by status
    @GetMapping("/customer/{customerId}/status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<Payment>> getPaymentsByCustomerAndStatus(
            @PathVariable Integer customerId,
            @PathVariable String status) {

        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            List<Payment> payments = paymentService.getPaymentsByCustomerAndStatus(customerId, paymentStatus);

            if (payments.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(payments);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ MANAGER: Get total paid by customer
    @GetMapping("/customer/{customerId}/total-paid")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> getTotalPaidByCustomer(@PathVariable Integer customerId) {
        BigDecimal totalPaid = paymentService.getTotalPaidByCustomer(customerId);

        return ResponseEntity.ok(Map.of(
                "customerId", customerId,
                "totalPaid", totalPaid,
                "currency", "USD"
        ));
    }

    // ✅ MANAGER: View payments for specific order
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(@PathVariable Integer orderId) {
        List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);

        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(payments);
    }

    // ✅ MANAGER: Process payment for customer orders (with validation)
    @PostMapping("/process-for-customer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<String> processPaymentForCustomer(@RequestBody Payment payment) {
        // Additional validation for managers - they can only process payments for existing customers
        if (payment.getCustomerId() == null) {
            return ResponseEntity.badRequest().body("Customer ID is required for payment processing");
        }

        Boolean isProcessed = paymentService.processPayment(payment);

        if (isProcessed) {
            return ResponseEntity.ok("Customer payment processed successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to process customer payment");
        }
    }

    // ================================================================================================
    // ✅ CASHIER: Process payments for own orders
    // ================================================================================================

    // ✅ CASHIER: Process payment for their own orders
    @PostMapping("/process-own-order")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<String> processPaymentForOwnOrder(@RequestBody Payment payment) {
        // In a real implementation, you would get the current user ID from the security context
        // For now, we'll assume the processedByUserId is set correctly

        if (payment.getProcessedByUserId() == null) {
            return ResponseEntity.badRequest().body("Processed by user ID is required");
        }

        Boolean isProcessed = paymentService.processPayment(payment);

        if (isProcessed) {
            return ResponseEntity.ok("Order payment processed successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to process order payment");
        }
    }

    // ✅ CASHIER: View their own processed payments
    @GetMapping("/my-payments/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<List<Payment>> getMyProcessedPayments(@PathVariable Integer userId) {
        // In a real implementation, you would validate that the userId matches the current user
        // or allow admins/managers to view any user's payments

        List<Payment> payments = paymentService.getPaymentsByUserId(userId);

        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(payments);
    }

    // ✅ CASHIER: View their payments within date range
    @GetMapping("/my-payments/{userId}/date-range")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<List<Payment>> getMyPaymentsByDateRange(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<Payment> payments = paymentService.getPaymentsByUserAndDateRange(userId, startDate, endDate);

        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(payments);
    }

    // ✅ CASHIER: Update payment status for their own processed payments (limited statuses)
    @PutMapping("/update-own-payment-status/{paymentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<String> updateOwnPaymentStatus(
            @PathVariable Integer paymentId,
            @RequestBody Map<String, String> request) {

        try {
            String statusStr = request.get("status");
            PaymentStatus status = PaymentStatus.valueOf(statusStr);

            // Cashiers can only update to limited statuses
            if (status != PaymentStatus.COMPLETED && status != PaymentStatus.FAILED) {
                return ResponseEntity.badRequest().body("Cashiers can only update status to COMPLETED or FAILED");
            }

            // In real implementation, validate that this payment was processed by the current user
            Boolean isUpdated = paymentService.updatePaymentStatus(paymentId, status);

            if (isUpdated) {
                return ResponseEntity.ok("Payment status updated successfully");
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid payment status");
        }
    }

    // ================================================================================================
    // ✅ UTILITY ENDPOINTS (Available to all roles as needed)
    // ================================================================================================

    // ✅ ALL ROLES: Validate payment data
    @PostMapping("/validate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<Map<String, Object>> validatePayment(@RequestBody Payment payment) {
        Boolean isValid = paymentService.validatePayment(payment);

        return ResponseEntity.ok(Map.of(
                "valid", isValid,
                "message", isValid ? "Payment data is valid" : "Payment data validation failed"
        ));
    }

    // ✅ ALL ROLES: Get payment methods and gateways (enum values)
    @GetMapping("/payment-methods")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<Map<String, Object>> getPaymentMethods() {
        return ResponseEntity.ok(Map.of(
                "paymentMethods", List.of(
                        "WALLET",    // Mobile wallets, digital payments
                        "CARD",      // Credit/debit cards, contactless
                        "CASH"       // Physical cash payments
                ),
                "paymentStatuses", List.of(
                        "PENDING", "PROCESSING", "COMPLETED",
                        "FAILED", "CANCELLED", "REFUNDED", "PARTIALLY_REFUNDED"
                ),
                "paymentGateways", List.of(
                        "INTERNAL", "STRIPE", "PAYPAL", "SQUARE", "RAZORPAY",
                        "AUTHORIZE_NET", "BRAINTREE", "ADYEN", "WORLDPAY", "PAYU",
                        "MOLLIE", "KLARNA", "APPLE_PAY", "GOOGLE_PAY", "SAMSUNG_PAY",
                        "BANK_TRANSFER", "CUSTOM"
                ),
                "methodDescriptions", Map.of(
                        "WALLET", "Mobile wallets, digital payments (Apple Pay, Google Pay, PayPal, etc.)",
                        "CARD", "Credit cards, debit cards, contactless payments",
                        "CASH", "Physical cash payments processed internally"
                )
        ));
    }

    // ✅ CASHIER: Process cash payment with change calculation
    @PostMapping("/process-cash-payment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<Map<String, Object>> processCashPayment(@RequestBody Map<String, Object> request) {
        try {
            // Extract cash payment details
            Payment payment = new Payment();
            payment.setOrderId(Integer.valueOf(request.get("orderId").toString()));
            payment.setCustomerId(request.get("customerId") != null ?
                    Integer.valueOf(request.get("customerId").toString()) : null);
            payment.setProcessedByUserId(Integer.valueOf(request.get("processedByUserId").toString()));
            payment.setAmount(new BigDecimal(request.get("amount").toString()));
            payment.setPaymentMethod(PaymentMethod.CASH);
            payment.setGateway("INTERNAL");

            // Calculate change for cash payments
            BigDecimal amountReceived = new BigDecimal(request.get("amountReceived").toString());
            BigDecimal changeAmount = amountReceived.subtract(payment.getAmount());

            if (changeAmount.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Insufficient cash received",
                        "required", payment.getAmount(),
                        "received", amountReceived,
                        "shortage", changeAmount.abs()
                ));
            }

            payment.setChangeAmount(changeAmount);
            payment.setNotes("Cash payment - Change: " + changeAmount);

            Boolean isProcessed = paymentService.processPayment(payment);

            if (isProcessed) {
                return ResponseEntity.ok(Map.of(
                        "message", "Cash payment processed successfully",
                        "change", changeAmount,
                        "amountReceived", amountReceived,
                        "totalAmount", payment.getAmount()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Failed to process cash payment"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid cash payment request: " + e.getMessage()));
        }
    }

    // ✅ CASHIER: Process card payment
    @PostMapping("/process-card-payment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<Map<String, Object>> processCardPayment(@RequestBody Map<String, Object> request) {
        try {
            Payment payment = new Payment();
            payment.setOrderId(Integer.valueOf(request.get("orderId").toString()));
            payment.setCustomerId(request.get("customerId") != null ?
                    Integer.valueOf(request.get("customerId").toString()) : null);
            payment.setProcessedByUserId(Integer.valueOf(request.get("processedByUserId").toString()));
            payment.setAmount(new BigDecimal(request.get("amount").toString()));
            payment.setPaymentMethod(PaymentMethod.CARD);
            payment.setGateway(request.get("gateway") != null ?
                    request.get("gateway").toString() : "STRIPE");

            // Set card details if provided
            if (request.get("cardLast4Digits") != null) {
                payment.setCardLast4Digits(request.get("cardLast4Digits").toString());
            }

            payment.setNotes("Card payment via " + payment.getGateway());

            Boolean isProcessed = paymentService.processPayment(payment);

            if (isProcessed) {
                return ResponseEntity.ok(Map.of(
                        "message", "Card payment processed successfully",
                        "gateway", payment.getGateway(),
                        "amount", payment.getAmount(),
                        "method", "CARD"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Failed to process card payment"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid card payment request: " + e.getMessage()));
        }
    }

    // ✅ CASHIER: Process wallet payment
    @PostMapping("/process-wallet-payment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<Map<String, Object>> processWalletPayment(@RequestBody Map<String, Object> request) {
        try {
            Payment payment = new Payment();
            payment.setOrderId(Integer.valueOf(request.get("orderId").toString()));
            payment.setCustomerId(request.get("customerId") != null ?
                    Integer.valueOf(request.get("customerId").toString()) : null);
            payment.setProcessedByUserId(Integer.valueOf(request.get("processedByUserId").toString()));
            payment.setAmount(new BigDecimal(request.get("amount").toString()));
            payment.setPaymentMethod(PaymentMethod.WALLET);
            payment.setGateway(request.get("gateway") != null ?
                    request.get("gateway").toString() : "PAYPAL");

            // Set wallet-specific transaction reference if provided
            if (request.get("walletTransactionId") != null) {
                payment.setTransactionReference(request.get("walletTransactionId").toString());
            }

            payment.setNotes("Wallet payment via " + payment.getGateway());

            Boolean isProcessed = paymentService.processPayment(payment);

            if (isProcessed) {
                return ResponseEntity.ok(Map.of(
                        "message", "Wallet payment processed successfully",
                        "gateway", payment.getGateway(),
                        "amount", payment.getAmount(),
                        "method", "WALLET"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Failed to process wallet payment"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid wallet payment request: " + e.getMessage()));
        }
    }

    // ✅ Test endpoint to verify authentication
    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("Payment module authentication is working!");
    }
}