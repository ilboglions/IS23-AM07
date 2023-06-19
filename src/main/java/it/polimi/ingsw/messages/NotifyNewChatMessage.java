package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message notifies the client that a new chat message has been recived
 */
public class NotifyNewChatMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -5618185975472918790L;

    private final String sender;
    private final String content;
    private final String recipient;

    /**
     * Constructor of a NotifyNewChatMessage (on a broadcast message)
     * @param sender player sender of the message
     * @param content content of the message
     */
    public NotifyNewChatMessage(String sender, String content) {
        super(MessageType.NOTIFY_NEW_CHAT);
        this.sender = sender;
        this.content = content;
        this.recipient = "";
    }

    /**
     * Constructor of a NotifyNewChatMessage (on a private message)
     * @param sender player sender of the message
     * @param recipient recipient of the message
     * @param content content of the message
     */
    public NotifyNewChatMessage(String sender, String recipient, String content) {
        super(MessageType.NOTIFY_NEW_CHAT);
        this.sender = sender;
        this.content = content;
        this.recipient = recipient;
    }

    /**
     *
     * @return the username of the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     *
     * @return the content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @return the recipient of the message (or the blank string on a broadcast message)
     */
    public String getRecipient() {
        return recipient;
    }
}
