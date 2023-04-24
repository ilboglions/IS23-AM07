package it.polimi.ingsw.messages;

import java.io.Serial;

public class NotifyNewChatMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -5618185975472918790L;

    private final String recipient;
    private final String content;
    NotifyNewChatMessage(String recipient, String content) {
        super(MessageType.NOTIFY_NEW_CHAT);
        this.recipient = recipient;
        this.content = content;
    }
    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }
}
