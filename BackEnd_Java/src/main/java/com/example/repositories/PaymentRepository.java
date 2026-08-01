package com.example.repositories;

import com.example.entities.PaymentMaster;
import com.razorpay.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentMaster, Integer> {

    boolean existsByBooking_IdAndPaymentStatus(Integer bookingId, String status);

    Optional<PaymentMaster> findByBooking_IdAndPaymentStatus(Integer bookingId, String status);

    Optional<PaymentMaster> findByTransactionRef(String transactionRef);

    List<PaymentMaster> findByBooking_Id(Integer bookingId);

    Optional<PaymentMaster> findByBookingId(Integer bookingId);
}
