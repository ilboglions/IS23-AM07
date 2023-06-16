package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message is used to send a message a chat message from the client
 */
public class PostMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -3522448489625423937L;

    private final String recipient;
    private final String content;

    /**
     * Constructor of a PostMessage (private messages)
     * @param recipient recipient of the message
     * @param content content of the message
     */
    public PostMessage(String recipient, String content) {
        super(MessageType.POST_MESSAGE);
        this.content = content;
        this.recipient = recipient;
    }

    /**
     * Constructor of a PostMessage
     * @param content content of the message
     */
    public PostMessage(String content) {
        super(MessageType.POST_MESSAGE);
        this.content = content;
        this.recipient = "";
    }


    /**
     *
     * @return the recipient of the message if it's a private message, the blank string otherwise
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     *
     * @return the content of the message
     */
    public String getContent() {
        return content;
    }
}
