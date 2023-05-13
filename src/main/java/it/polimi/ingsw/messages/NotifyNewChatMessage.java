package it.polimi.ingsw.messages;

import java.io.Serial;

public class NotifyNewChatMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -5618185975472918790L;

    private final String sender;
    private final String content;
    private final Boolean privateMessage;
    public NotifyNewChatMessage(String sender, String content, Boolean privateMessage) {
        super(MessageType.NOTIFY_NEW_CHAT);
        this.sender = sender;
        this.content = content;
        this.privateMessage = privateMessage;
    }
    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Boolean getPrivateMessage() {
        return privateMessage;
    }
}
