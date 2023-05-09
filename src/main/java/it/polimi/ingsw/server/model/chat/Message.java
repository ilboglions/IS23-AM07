package it.polimi.ingsw.server.model.chat;

import java.util.Optional;

/**
 * Message class represent a single message sent by a user privately or globally
 */
public class Message {

    /**
     * The username of the sender
     */
    private final String sender;
    /**
     * The username of the recipient
     */
    private final String recipient;
    /**
     * The content of the message
     */
    private final String content;

    /**
     * Constructor for a private message between two players
     * @param sender the username of the sender
     * @param recipient the username of the recipient
     * @param content the content of the message
     */
    public Message(String sender, String recipient, String content) {
        if(sender == null || recipient == null || content == null) {
            throw new NullPointerException("Message constructor parameter is null");
        }
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    /**
     * Constructor for a global chat message
     * @param sender the username of the sender
     * @param content the username of the recipient
     */
    public Message(String sender, String content){
        if(sender == null  || content == null) {
            throw new NullPointerException("Message constructor parameter is null");
        }
        this.sender = sender;
        this.recipient = null;
        this.content = content;
    }

    /**
     * The method used to retrieve the content of the message
     * @return the content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * The method used to retrieve the sender of the message
     * @return the username of the sender of the message
     */
    public String getSender() {
        return sender;
    }

    /**
     * The method used to retrive the recipient of the message, if present
     * @return an Optional String containing the username of the recipient if the message is a private message, an empty optional otherwise
     */
    public Optional<String> getRecipient() {
        return Optional.ofNullable(recipient);
    }
}
