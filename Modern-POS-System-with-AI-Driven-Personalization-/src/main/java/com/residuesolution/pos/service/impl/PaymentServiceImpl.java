package com.residuesolution.pos.service.impl;

import com.residuesolution.pos.dto.Payment;
import com.residuesolution.pos.entity.PaymentEntity;
import com.residuesolution.pos.enums.PaymentMethod;
import com.residuesolution.pos.enums.PaymentStatus;
import com.residuesolution.pos.repository.PaymentRepository;
import com.residuesolution.pos.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ModelMapper mapper;

    @Override
    public List<Payment> getPaymentsByGateway(String gateway) {
        List<PaymentEntity> paymentEntities = paymentRepository.findByGateway(gateway);

        return paymentEntities.stream()
                .map(entity -> mapper.map(entity, Payment.class))
                .toList();
    }

    @Override
    public List<Payment> getPaymentsByGatewayAndStatus(String gateway, PaymentStatus status) {
        List<PaymentEntity> paymentEntities = paymentRepository.findByGatewayAndPaymentStatus(gateway, status);

        return paymentEntities.stream()
                .map(entity -> mapper.map(entity, Payment.class))
                .toList();
    }

    @Override
    public BigDecimal getTotalAmountByGateway(String gateway) {
        BigDecimal total = paymentRepository.getTotalAmountByGateway(gateway);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<Map<String, Object>> getPaymentStatsByGateway() {
        List<Object[]> results = paymentRepository.getPaymentStatsByGateway();

        return results.stream()
                .map(result -> Map.of(
                        "gateway", result[0] != null ? result[0].toString() : "UNKNOWN",
                        "transactionCount", result[1] != null ? result[1] : 0L,
                        "totalAmount", result[2] != null ? result[2] : BigDecimal.ZERO
                ))
                .toList();
    }

    @Override
    @Transactional
    public Boolean processPayment(Payment payment) {
        try {
            // Validate payment data
            if (!validatePayment(payment)) {
                log.error("Payment validation failed for payment: {}", payment);
                return false;
            }

            // Generate transaction reference if not provided
            if (payment.getTransactionReference() == null || payment.getTransactionReference().isEmpty()) {
                payment.setTransactionReference(generateTransactionReference());
            }

            // Set default gateway if not provided
            if (payment.getGateway() == null || payment.getGateway().isEmpty()) {
                payment.setGateway(determineDefaultGateway(payment.getPaymentMethod()));
            }

            // Set initial status
            payment.setPaymentStatus(PaymentStatus.PROCESSING);
            payment.setPaymentDateTime(LocalDateTime.now());

            // Map to entity and save
            PaymentEntity paymentEntity = mapper.map(payment, PaymentEntity.class);
            PaymentEntity savedPayment = paymentRepository.save(paymentEntity);

            if (savedPayment != null) {
                log.info("Payment processed successfully with ID: {}", savedPayment.getPaymentId());

                // Simulate payment processing logic here
                // In real implementation, you'd integrate with payment gateways
                simulatePaymentProcessing(savedPayment);

                return true;
            }

            return false;

        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean updatePaymentStatus(Integer paymentId, PaymentStatus status) {
        try {
            Optional<PaymentEntity> paymentEntity = paymentRepository.findById(paymentId);

            if (paymentEntity.isPresent()) {
                PaymentEntity payment = paymentEntity.get();
                payment.setPaymentStatus(status);
                payment.setUpdatedAt(LocalDateTime.now());

                paymentRepository.save(payment);

                log.info("Payment status updated to {} for payment ID: {}", status, paymentId);
                return true;
            }

            log.warn("Payment not found with ID: {}", paymentId);
            return false;

        } catch (Exception e) {
            log.error("Error updating payment status: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Payment getPaymentById(Integer paymentId) {
        Optional<PaymentEntity> paymentEntity = paymentRepository.findById(paymentId);

        if (paymentEntity.isPresent()) {
            return mapper.map(paymentEntity.get(), Payment.class);
        }

        return null;
    }

    @Override
    public List<Payment> getAllPayments() {
        List<PaymentEntity> paymentEntities = paymentRepository.findAll();

        return paymentEntities.stream()
                .map(entity -> mapper.map(entity, Payment.class))
                .toList();
    }

    @Override
    public List<Payment> getPaymentsByCustomerId(Integer customerId) {
        List<PaymentEntity> paymentEntities = paymentRepository.findByCustomerId(customerId);

        return paymentEntities.stream()
                .map(entity -> mapper.map(entity, Payment.class))
                .toList();
    }

    @Override
    public List<Payment> getPaymentsByCustomerAndStatus(Integer customerId, PaymentStatus status) {
        List<PaymentEntity> paymentEntities = paymentRepository.findByCustomerIdAndPaymentStatus(customerId, status);

        return paymentEntities.stream()
                .map(entity -> mapper.map(entity, Payment.class))
                .toList();
    }

    @Override
    public List<Payment> getPaymentsByUserId(Integer userId) {
        List<PaymentEntity> paymentEntities = paymentRepository.findByProcessedByUserId(userId);

        return paymentEntities.stream()
                .map(entity -> mapper.map(entity, Payment.class))
                .toList();
    }

    @Override
    public List<Payment> getPaymentsByOrderId(Integer orderId) {
        List<PaymentEntity> paymentEntities = paymentRepository.findByOrderId(orderId);

        return paymentEntities.stream()
                .map(entity -> mapper.map(entity, Payment.class))
                .toList();
    }

    @Override
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        List<PaymentEntity> paymentEntities = paymentRepository.findByPaymentStatus(status);

        return paymentEntities.stream()
                .map(entity -> mapper.map(entity, Payment.class))
                .toList();
    }

    @Override
    @Transactional
    public Boolean cancelPayment(Integer paymentId) {
        try {
            Optional<PaymentEntity> paymentEntity = paymentRepository.findById(paymentId);

            if (paymentEntity.isPresent()) {
                PaymentEntity payment = paymentEntity.get();

                // Only allow cancellation of pending or processing payments
                if (payment.getPaymentStatus() == PaymentStatus.PENDING ||
                        payment.getPaymentStatus() == PaymentStatus.PROCESSING) {

                    payment.setPaymentStatus(PaymentStatus.CANCELLED);
                    payment.setUpdatedAt(LocalDateTime.now());
                    payment.setNotes(payment.getNotes() + " | Payment cancelled");

                    paymentRepository.save(payment);

                    log.info("Payment cancelled successfully for ID: {}", paymentId);
                    return true;
                }

                log.warn("Cannot cancel payment with status: {} for ID: {}",
                        payment.getPaymentStatus(), paymentId);
                return false;
            }

            return false;

        } catch (Exception e) {
            log.error("Error cancelling payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean refundPayment(Integer paymentId, BigDecimal refundAmount, String reason) {
        try {
            Optional<PaymentEntity> paymentEntity = paymentRepository.findById(paymentId);

            if (paymentEntity.isPresent()) {
                PaymentEntity payment = paymentEntity.get();

                // Validate refund conditions
                if (payment.getPaymentStatus() != PaymentStatus.COMPLETED || !payment.getIsRefundable()) {
                    log.warn("Payment is not refundable. Status: {}, Refundable: {}",
                            payment.getPaymentStatus(), payment.getIsRefundable());
                    return false;
                }

                // Check refund amount
                BigDecimal totalRefunded = payment.getRefundedAmount();
                BigDecimal maxRefundable = payment.getAmount().subtract(totalRefunded);

                if (refundAmount.compareTo(maxRefundable) > 0) {
                    log.warn("Refund amount {} exceeds maximum refundable amount {}",
                            refundAmount, maxRefundable);
                    return false;
                }

                // Process refund
                payment.setRefundedAmount(totalRefunded.add(refundAmount));
                payment.setUpdatedAt(LocalDateTime.now());
                payment.setNotes(payment.getNotes() + " | Refund: " + refundAmount + " - Reason: " + reason);

                // Update status based on refund amount
                if (payment.getRefundedAmount().compareTo(payment.getAmount()) == 0) {
                    payment.setPaymentStatus(PaymentStatus.REFUNDED);
                } else {
                    payment.setPaymentStatus(PaymentStatus.PARTIALLY_REFUNDED);
                }

                paymentRepository.save(payment);

                log.info("Refund processed successfully for payment ID: {} - Amount: {}",
                        paymentId, refundAmount);
                return true;
            }

            return false;

        } catch (Exception e) {
            log.error("Error processing refund: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public BigDecimal getTotalAmountByStatus(PaymentStatus status) {
        BigDecimal total = paymentRepository.getTotalAmountByStatus(status);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalPaidByCustomer(Integer customerId) {
        BigDecimal total = paymentRepository.getTotalPaidByCustomer(customerId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<Payment> getPaymentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        List<PaymentEntity> paymentEntities = paymentRepository.findPaymentsBetweenDates(startDate, endDate);

        return paymentEntities.stream()
                .map(entity -> mapper.map(entity, Payment.class))
                .toList();
    }

    @Override
    public List<Payment> getPaymentsByUserAndDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<PaymentEntity> paymentEntities = paymentRepository.findPaymentsByUserAndDateRange(userId, startDate, endDate);

        return paymentEntities.stream()
                .map(entity -> mapper.map(entity, Payment.class))
                .toList();
    }

    @Override
    @Transactional
    public Boolean retryFailedPayment(Integer paymentId) {
        try {
            Optional<PaymentEntity> paymentEntity = paymentRepository.findById(paymentId);

            if (paymentEntity.isPresent()) {
                PaymentEntity payment = paymentEntity.get();

                if (payment.getPaymentStatus() == PaymentStatus.FAILED) {
                    payment.setPaymentStatus(PaymentStatus.PROCESSING);
                    payment.setUpdatedAt(LocalDateTime.now());
                    payment.setNotes(payment.getNotes() + " | Payment retry attempted");

                    PaymentEntity savedPayment = paymentRepository.save(payment);

                    // Simulate retry processing
                    simulatePaymentProcessing(savedPayment);

                    log.info("Payment retry initiated for ID: {}", paymentId);
                    return true;
                }

                log.warn("Cannot retry payment with status: {}", payment.getPaymentStatus());
                return false;
            }

            return false;

        } catch (Exception e) {
            log.error("Error retrying payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Payment> getRefundablePayments() {
        List<PaymentEntity> paymentEntities = paymentRepository.findRefundablePayments();

        return paymentEntities.stream()
                .map(entity -> mapper.map(entity, Payment.class))
                .toList();
    }

    @Override
    public Boolean validatePayment(Payment payment) {
        // Basic validation
        if (payment == null) {
            return false;
        }

        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid payment amount: {}", payment.getAmount());
            return false;
        }

        if (payment.getPaymentMethod() == null) {
            log.error("Payment method is required");
            return false;
        }

        if (payment.getOrderId() == null) {
            log.error("Order ID is required");
            return false;
        }

        if (payment.getProcessedByUserId() == null) {
            log.error("Processed by user ID is required");
            return false;
        }

        // Payment method specific validation
        if (payment.getPaymentMethod() == PaymentMethod.CASH) {
            // For cash payments, change amount should be calculated
            if (payment.getChangeAmount() != null && payment.getChangeAmount().compareTo(BigDecimal.ZERO) < 0) {
                log.error("Change amount cannot be negative for cash payments");
                return false;
            }
        }

        if (payment.getPaymentMethod() == PaymentMethod.CARD) {
            // For card payments, we might want to validate card details in the future
            // Currently just ensuring it's not cash-specific validation
        }

        if (payment.getPaymentMethod() == PaymentMethod.WALLET) {
            // For wallet payments, transaction reference might be required from wallet provider
            // This can be enhanced based on wallet integration requirements
        }

        return true;
    }

    // Helper methods
    private String generateTransactionReference() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String determineDefaultGateway(PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case CASH -> "INTERNAL";           // Cash payments processed internally
            case CARD -> "STRIPE";             // Card payments via Stripe gateway
            case WALLET -> "PAYPAL";           // Wallet payments via PayPal gateway
            default -> "INTERNAL";
        };
    }

    private void simulatePaymentProcessing(PaymentEntity payment) {
        // Simulate async payment processing
        // In real implementation, this would be handled by payment gateway integration

        try {
            Thread.sleep(1000); // Simulate processing delay

            // 90% success rate simulation
            boolean success = Math.random() < 0.9;

            if (success) {
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                payment.setGatewayResponse("Payment processed successfully");
            } else {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setGatewayResponse("Payment failed - insufficient funds");
            }

            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setGatewayResponse("Payment processing interrupted");
            paymentRepository.save(payment);
        }
    }
}