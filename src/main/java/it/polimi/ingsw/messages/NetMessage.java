package it.polimi.ingsw.messages;

import java.io.Serial;
import java.io.Serializable;

public abstract class NetMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1592537124969459098L;
    private final MessageType messageType;

    NetMessage(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
