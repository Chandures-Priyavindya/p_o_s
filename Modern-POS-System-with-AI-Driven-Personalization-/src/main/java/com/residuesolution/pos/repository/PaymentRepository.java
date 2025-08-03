package com.residuesolution.pos.repository;

import com.residuesolution.pos.entity.PaymentEntity;
import com.residuesolution.pos.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {

    // Find payments by customer ID
    List<PaymentEntity> findByCustomerId(Integer customerId);

    // Find payments by order ID
    List<PaymentEntity> findByOrderId(Integer orderId);

    // Find payments processed by specific user
    List<PaymentEntity> findByProcessedByUserId(Integer userId);

    // Find payments by status
    List<PaymentEntity> findByPaymentStatus(PaymentStatus status);

    // Find payments by customer and status
    List<PaymentEntity> findByCustomerIdAndPaymentStatus(Integer customerId, PaymentStatus status);

    // Find payments by transaction reference
    Optional<PaymentEntity> findByTransactionReference(String transactionReference);

    // Find payments by gateway
    List<PaymentEntity> findByGateway(String gateway);

    // Find payments by gateway and status
    List<PaymentEntity> findByGatewayAndPaymentStatus(String gateway, PaymentStatus status);

    // Custom queries for analytics and reporting
    @Query("SELECT SUM(p.amount) FROM PaymentEntity p WHERE p.paymentStatus = :status")
    BigDecimal getTotalAmountByStatus(@Param("status") PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM PaymentEntity p WHERE p.customerId = :customerId AND p.paymentStatus = 'COMPLETED'")
    BigDecimal getTotalPaidByCustomer(@Param("customerId") Integer customerId);

    @Query("SELECT p FROM PaymentEntity p WHERE p.paymentDateTime BETWEEN :startDate AND :endDate")
    List<PaymentEntity> findPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM PaymentEntity p WHERE p.processedByUserId = :userId AND p.paymentDateTime BETWEEN :startDate AND :endDate")
    List<PaymentEntity> findPaymentsByUserAndDateRange(@Param("userId") Integer userId,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);

    // Find refundable payments
    @Query("SELECT p FROM PaymentEntity p WHERE p.isRefundable = true AND p.paymentStatus = 'COMPLETED'")
    List<PaymentEntity> findRefundablePayments();

    @Query("SELECT SUM(p.amount) FROM PaymentEntity p WHERE p.gateway = :gateway AND p.paymentStatus = 'COMPLETED'")
    BigDecimal getTotalAmountByGateway(@Param("gateway") String gateway);

    @Query("SELECT p.gateway, COUNT(p), SUM(p.amount) FROM PaymentEntity p WHERE p.paymentStatus = 'COMPLETED' GROUP BY p.gateway")
    List<Object[]> getPaymentStatsByGateway();

    // Get payments that need processing (for background jobs)
    List<PaymentEntity> findByPaymentStatusIn(List<PaymentStatus> statuses);
}