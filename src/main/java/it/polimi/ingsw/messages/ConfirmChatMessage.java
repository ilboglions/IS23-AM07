package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmChatMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = 4011841211848568521L;

    private final Boolean result;

    ConfirmChatMessage(String username, Boolean result) {
        super(username, MessageType.CONFIRM_CHAT);
        this.result = result;
    }

    public Boolean getResult() {
        return result;
    }
}
