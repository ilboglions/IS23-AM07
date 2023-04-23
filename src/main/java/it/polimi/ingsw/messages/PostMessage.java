package it.polimi.ingsw.messages;

import java.io.Serial;

public class PostMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -3522448489625423937L;

    private final String recipient;
    private final String content;

    PostMessage(String username, String recipient, String content) {
        super(username, MessageType.POST_MESSAGE);
        this.recipient = recipient;
        this.content = content;
    }

    public String getSender() {
        return this.getUsername();
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }
}
