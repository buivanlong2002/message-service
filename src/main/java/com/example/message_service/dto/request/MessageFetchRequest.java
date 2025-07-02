package com.example.message_service.dto.request;


import lombok.Data;

@Data
public class MessageFetchRequest {
    private String conversationId;
    private int page = 0;
    private int size = 20;
}
