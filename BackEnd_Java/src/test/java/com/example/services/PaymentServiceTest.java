package com.example.services;

import com.example.entities.BookingHeader;
import com.example.entities.PaymentMaster;
import com.example.repositories.BookingRepository;
import com.example.repositories.PaymentRepository;
import com.example.services.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private BookingHeader booking;
    private PaymentMaster payment;

    @BeforeEach
    void setUp() {
        booking = new BookingHeader();
        booking.setId(201);
        booking.setTotalAmount(new BigDecimal("10000.00"));

        payment = new PaymentMaster();
        payment.setId(1);
        payment.setBooking(booking);
        payment.setPaymentMode("UPI");
        payment.setPaymentStatus("SUCCESS");
        payment.setPaymentAmount(new BigDecimal("10000.00"));
        payment.setTransactionRef("TXN_12345");
    }

    @Test
    @DisplayName("makePayment: Valid details persist payment successfully")
    void testMakePayment_Success() {
        when(bookingRepository.findById(201)).thenReturn(Optional.of(booking));
        when(paymentRepository.existsByBooking_IdAndPaymentStatus(201, "SUCCESS")).thenReturn(false);
        when(paymentRepository.findByTransactionRef("TXN_12345")).thenReturn(Optional.empty());
        when(paymentRepository.save(any(PaymentMaster.class))).thenReturn(payment);

        PaymentMaster result = paymentService.makePayment(201, "UPI", "TXN_12345", "SUCCESS", "10000.00");

        assertNotNull(result);
        assertEquals("UPI", result.getPaymentMode());
        assertEquals("SUCCESS", result.getPaymentStatus());
        verify(paymentRepository, times(1)).save(any(PaymentMaster.class));
    }

    @Test
    @DisplayName("makePayment: Booking not found throws RuntimeException")
    void testMakePayment_BookingNotFound_ThrowsException() {
        when(bookingRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.makePayment(999, "UPI", "TXN_123", "SUCCESS", "5000"));
        assertEquals("Booking not found", ex.getMessage());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("makePayment: Already completed payment throws RuntimeException")
    void testMakePayment_AlreadyCompleted_ThrowsException() {
        when(bookingRepository.findById(201)).thenReturn(Optional.of(booking));
        when(paymentRepository.existsByBooking_IdAndPaymentStatus(201, "SUCCESS")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.makePayment(201, "UPI", "TXN_123", "SUCCESS", "10000.00"));
        assertEquals("Payment already completed for this booking", ex.getMessage());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("makePayment: Amount mismatch throws RuntimeException")
    void testMakePayment_AmountMismatch_ThrowsException() {
        when(bookingRepository.findById(201)).thenReturn(Optional.of(booking));
        when(paymentRepository.existsByBooking_IdAndPaymentStatus(201, "SUCCESS")).thenReturn(false);
        when(paymentRepository.findByTransactionRef("TXN_12345")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.makePayment(201, "UPI", "TXN_12345", "SUCCESS", "5000.00"));
        assertEquals("Payment amount mismatch", ex.getMessage());
    }

    @Test
    @DisplayName("getPaymentById: Non-existent ID throws RuntimeException")
    void testGetPaymentById_NotFound_ThrowsException() {
        when(paymentRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.getPaymentById(999));
        assertEquals("Payment not found", ex.getMessage());
    }
}
