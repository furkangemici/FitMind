package com.example.fitmind.api.model;

import java.util.List;

public class GroqRequest {
    private String model;
    private List<Message> messages;

    public GroqRequest(String prompt) {
        this.model = "llama-3.3-70b-versatile"; 
        this.messages = List.of(new Message("user", prompt));
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
