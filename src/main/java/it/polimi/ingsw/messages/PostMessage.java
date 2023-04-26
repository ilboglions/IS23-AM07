package it.polimi.ingsw.messages;

import java.io.Serial;
import java.util.Optional;

public class PostMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -3522448489625423937L;

    private final Optional<String> recipient;
    private final String content;

    PostMessage(String username, String recipient, String content) {
        super(MessageType.POST_MESSAGE);
        this.content = content;
        this.recipient = Optional.of(recipient);
    }
    PostMessage(String content) {
        super(MessageType.POST_MESSAGE);
        this.content = content;
        this.recipient = Optional.empty();
    }



    public Optional<String> getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }
}