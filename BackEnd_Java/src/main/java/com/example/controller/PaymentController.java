package com.example.controller;


import com.example.dto.PaymentDTO;
import com.example.entities.PaymentMaster;
import com.example.services.PaymentService;
import com.example.services.impl.PaymentServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentServiceImpl paymentServiceImpl;

    // ✅ Constructor injection (BEST PRACTICE)
    public PaymentController(PaymentService paymentService,
                             PaymentServiceImpl paymentServiceImpl) {
        this.paymentService = paymentService;
        this.paymentServiceImpl = paymentServiceImpl;
    }

    // ✅ MAKE PAYMENT
    @PostMapping("/pay")
    public PaymentDTO pay(@RequestParam Integer bookingId,
                          @RequestParam String paymentMode,
                          @RequestParam String transactionRef,
                          @RequestParam String paymentStatus,
                          @RequestParam String paymentAmount) {

        PaymentMaster payment = paymentService.makePayment(
                bookingId,
                paymentMode,
                transactionRef,
                paymentStatus,
                paymentAmount
        );

        return paymentServiceImpl.mapToDTO(payment);
    }

    // ✅ GET PAYMENT BY ID
    @GetMapping("/{paymentId}")
    public PaymentDTO getPayment(@PathVariable Integer paymentId) {

        PaymentMaster payment = paymentService.getPaymentById(paymentId);
        return paymentServiceImpl.mapToDTO(payment);
    }

    // ✅ GET RECEIPT (SUCCESS PAYMENT)
    @GetMapping("/receipt/{bookingId}")
    public PaymentDTO getReceipt(@PathVariable Integer bookingId) {

        PaymentMaster payment = paymentService.getSuccessfulPayment(bookingId);
        return paymentServiceImpl.mapToDTO(payment);
    }
}
