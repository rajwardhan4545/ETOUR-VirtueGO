package com.example.services.impl;


import com.example.dto.PaymentDTO;
import com.example.entities.BookingHeader;
import com.example.entities.PaymentMaster;
import com.example.repositories.BookingRepository;
import com.example.repositories.PaymentRepository;
import com.example.services.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public PaymentMaster makePayment(Integer bookingId,
                                     String paymentMode,
                                     String transactionRef,
                                     String paymentStatus,
                                     String paymentAmount) {

       
        BookingHeader booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        
        if (paymentRepository.existsByBooking_IdAndPaymentStatus(bookingId, "SUCCESS")) {
            throw new RuntimeException("Payment already completed for this booking");
        }

        
        paymentRepository.findByTransactionRef(transactionRef)
                .ifPresent(p -> {
                    throw new RuntimeException("Duplicate transaction reference");
                });

        BigDecimal payAmount = new BigDecimal(paymentAmount);
        if (booking.getTotalAmount() == null ||
                payAmount.compareTo(booking.getTotalAmount()) != 0) {
            throw new RuntimeException("Payment amount mismatch");
        }

        
        PaymentMaster payment = new PaymentMaster();
        payment.setBooking(booking);
        payment.setPaymentMode(paymentMode);
        payment.setTransactionRef(transactionRef);
        payment.setPaymentStatus(paymentStatus);
        payment.setPaymentAmount(payAmount);
        payment.setPaymentDate(Instant.now());

        
        return paymentRepository.save(payment);
    }

    @Override
    public PaymentMaster getPaymentById(Integer paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Override
    public PaymentMaster getSuccessfulPayment(Integer bookingId) {
        return paymentRepository.findByBooking_Id(bookingId)
                .stream()
                .filter(p -> "SUCCESS".equalsIgnoreCase(p.getPaymentStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No successful payment found"));
    }

    @Override
    public Integer findByBookingId(Integer bookingId) {

        return paymentRepository.findByBookingId(bookingId).orElseThrow(()->new RuntimeException("Payment Not Found")).getId();
    }


    public PaymentDTO mapToDTO(PaymentMaster payment) {

        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getId());
        dto.setPaymentAmount(payment.getPaymentAmount());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentMode(payment.getPaymentMode());
        dto.setTransactionRef(payment.getTransactionRef());
        dto.setPaymentDate(payment.getPaymentDate());

        if (payment.getBooking() != null) {
            dto.setBookingId(payment.getBooking().getId()); 
        }

        return dto;
    }
}
