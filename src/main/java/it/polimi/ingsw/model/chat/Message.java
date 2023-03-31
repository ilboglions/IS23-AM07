package it.polimi.ingsw.model.chat;

import java.util.Optional;

public class Message {

    private String sender;
    private Optional<String> recipient;
    private String content;

    public Message(String sender, Optional<String> recipient, String content) {
        if(sender == null || recipient == null || content == null) {
            throw new NullPointerException("Message constructor parameter is null");
        }
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public Optional<String> getRecipient() {
        return recipient;
    }
}
