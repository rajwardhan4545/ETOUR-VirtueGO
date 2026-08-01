package com.example.controller;

import com.example.services.InvoicePdfService;
import com.example.services.PaymentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.BookingCreateRequestDTO;
import com.example.dto.BookingResponseDTO;
import com.example.services.BookingService;

@RestController
@RequestMapping("/api/Booking")
public class BookingController {

    private final BookingService bookingService;
    private final InvoicePdfService invoicePdfService;
    private final PaymentService paymentService;

    public BookingController(BookingService bookingService,
            InvoicePdfService invoicePdfService,
            PaymentService paymentService) {
        this.bookingService = bookingService;
        this.invoicePdfService = invoicePdfService;
        this.paymentService = paymentService;
    }

    // GET ALL BOOKINGS
    @GetMapping
    public List<BookingResponseDTO> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // CREATE BOOKING
    @PostMapping
    public BookingResponseDTO createBooking(
            @RequestBody BookingCreateRequestDTO dto) {

        return bookingService.saveBooking(dto);
    }

    // NEW: Get bookings by customer ID
    @GetMapping("/customer/{customerId}")
    public List<BookingResponseDTO> getBookingsByCustomerId1(@PathVariable Integer customerId) {
        return bookingService.getBookingsByCustomerId(customerId);
    }

    // GET BOOKING SUMMARY
    @GetMapping("/{bookingId}")
    public BookingResponseDTO getBooking(@PathVariable Integer bookingId) {

        return bookingService.getBookingById(bookingId);
    }

    // PAYMENT STATUS CHECK
    @GetMapping("/status/{bookingId}")
    public Integer getPaymentStatus(@PathVariable Integer bookingId) {
        return bookingService.getPaymentStatus(bookingId);
    }

    // GET INVOICE PDF
    @GetMapping("/invoice/{bookingId}")
    public ResponseEntity<byte[]> getBookingInvoice(@PathVariable Integer bookingId) {
        Integer paymentId = paymentService.findByBookingId(bookingId);
        byte[] pdf = invoicePdfService.generateInvoice(paymentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + paymentId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}