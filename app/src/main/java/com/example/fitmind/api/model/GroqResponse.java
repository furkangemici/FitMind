package com.example.fitmind.api.model;

import java.util.List;

public class GroqResponse {
    private List<Choice> choices;

    public String getText() {
        if (choices != null && !choices.isEmpty()) {
            Choice c = choices.get(0);
            if (c.message != null) {
                return c.message.content;
            }
        }
        return "";
    }

    public static class Choice {
        public Message message;
    }

    public static class Message {
        public String content;
    }
}
