package com.example.controller;

import com.example.services.InvoicePdfService;
import com.example.services.PaymentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoicePdfService invoicePdfService;
    private final PaymentService paymentService;

    public InvoiceController(InvoicePdfService invoicePdfService, PaymentService paymentService) {
        this.invoicePdfService = invoicePdfService;
        this.paymentService = paymentService;
    }

    @GetMapping("/{bookingId}/download")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer bookingId) {
        Integer paymentId = paymentService.findByBookingId(bookingId);
        byte[] pdf = invoicePdfService.generateInvoice(paymentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + paymentId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
