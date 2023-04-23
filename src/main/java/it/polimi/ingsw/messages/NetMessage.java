package it.polimi.ingsw.messages;

import java.io.Serial;
import java.io.Serializable;

public abstract class NetMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1592537124969459098L;
    private final String username;
    private final MessageType messageType;

    NetMessage(String username, MessageType messageType) {
        this.username = username;
        this.messageType = messageType;
    }

    public String getUsername() {
        return username;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
