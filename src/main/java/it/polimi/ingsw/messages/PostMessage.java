package it.polimi.ingsw.messages;

import java.io.Serial;

public class PostMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -3522448489625423937L;

    private final String recipient;
    private final String content;

    public PostMessage(String recipient, String content) {
        super(MessageType.POST_MESSAGE);
        this.content = content;
        this.recipient = recipient;
    }
    public PostMessage(String content) {
        super(MessageType.POST_MESSAGE);
        this.content = content;
        this.recipient = "";
    }



    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }
}
