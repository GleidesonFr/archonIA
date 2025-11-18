package com.backend.archonia.models;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    private String sessionId;
    private List<ChatMessage> history = new ArrayList<>();

    public void addMessage(ChatMessage message){
        this.history.add(message);
    }
}
