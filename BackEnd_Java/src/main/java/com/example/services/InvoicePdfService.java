package com.example.services;

public interface InvoicePdfService {
	
	byte[] generateInvoice(Integer paymentId);
    
}
