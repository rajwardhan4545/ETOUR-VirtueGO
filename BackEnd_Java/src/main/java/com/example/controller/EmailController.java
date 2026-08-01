package com.example.controller;

import com.example.services.EmailService;
import com.example.services.InvoicePdfService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;
    private final InvoicePdfService invoicePdfService;

    public EmailController(EmailService emailService,
                           InvoicePdfService invoicePdfService) {
        this.emailService = emailService;
        this.invoicePdfService = invoicePdfService;
    }

    // ✅ Test booking confirmation
    @PostMapping("/booking-confirmation")
    public String sendBookingMail(@RequestParam Long paymentId) {

        emailService.sendBookingConfirmation(paymentId);

        return "Booking confirmation email sent for paymentId = " + paymentId;
    }

    // ✅ Test invoice mail
    @PostMapping("/invoice")
    public String sendInvoiceMail(@RequestParam Integer paymentId) {

        byte[] pdf = invoicePdfService.generateInvoice(paymentId);

        emailService.sendInvoiceWithAttachment(
                paymentId.longValue(),
                pdf
        );

        return "Invoice email sent for paymentId = " + paymentId;
    }
}
