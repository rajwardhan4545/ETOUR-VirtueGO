package com.example.services;

public interface EmailService {

    void sendSimpleEmail(String toEmail, String subject, String body);

    void sendBookingConfirmation(Long paymentId);

    void sendInvoiceWithAttachment(Long paymentId, byte[] pdfBytes);
}
