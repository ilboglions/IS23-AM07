package it.polimi.ingsw.model.chat;

import java.util.Optional;

public class Message {

    private final String sender;
    private final String recipient;
    private final String content;

    public Message(String sender, String recipient, String content) {
        if(sender == null || recipient == null || content == null) {
            throw new NullPointerException("Message constructor parameter is null");
        }
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    public Message(String sender, String content){
        if(sender == null  || content == null) {
            throw new NullPointerException("Message constructor parameter is null");
        }
        this.sender = sender;
        this.recipient = null;
        this.content = content;
    }

    public String getContent() {

        return content;
    }

    public String getSender() {

        return sender;
    }

    public Optional<String> getRecipient() {
        return Optional.ofNullable(recipient);
    }
}
