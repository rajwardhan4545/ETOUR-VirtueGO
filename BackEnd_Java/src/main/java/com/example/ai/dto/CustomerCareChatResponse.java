package com.example.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCareChatResponse {
    private String reply;
    private String sessionId;
    private String assistantName;
    private LocalDateTime timestamp;
    private boolean success;
    private List<String> suggestedFollowUps;
    private String modelProvider;
}
