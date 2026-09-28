package com.example.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCareChatRequest {
    private String message;
    private String sessionId;
    private String customerEmail;
    private String context; // e.g. "BOOKING_INQUIRY", "PACKAGE_SEARCH", "PAYMENT_ISSUE"
}
