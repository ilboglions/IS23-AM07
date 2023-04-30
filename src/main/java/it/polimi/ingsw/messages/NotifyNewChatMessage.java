package it.polimi.ingsw.messages;

import java.io.Serial;

public class NotifyNewChatMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -5618185975472918790L;

    private final String sender;
    private final String content;
    public NotifyNewChatMessage(String sender, String content) {
        super(MessageType.NOTIFY_NEW_CHAT);
        this.sender = sender;
        this.content = content;
    }
    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}
