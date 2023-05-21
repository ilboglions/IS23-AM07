package it.polimi.ingsw.messages;

import java.io.Serial;

public class NotifyNewChatMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -5618185975472918790L;

    private final String sender;
    private final String content;
    private final String recipient;
    public NotifyNewChatMessage(String sender, String content) {
        super(MessageType.NOTIFY_NEW_CHAT);
        this.sender = sender;
        this.content = content;
        this.recipient = "broadcast";
    }
    public NotifyNewChatMessage(String sender, String recipient, String content) {
        super(MessageType.NOTIFY_NEW_CHAT);
        this.sender = sender;
        this.content = content;
        this.recipient = recipient;
    }
    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getRecipient() {
        return recipient;
    }
}
