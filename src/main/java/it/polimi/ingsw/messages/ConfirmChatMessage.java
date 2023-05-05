package it.polimi.ingsw.messages;

import java.io.Serial;

public class ConfirmChatMessage extends ConfirmMessage {
    @Serial
    private static final long serialVersionUID = 4011841211848568521L;

    private final Boolean result;

    public ConfirmChatMessage(Boolean result, String errorType, String details) {
        super(MessageType.CONFIRM_CHAT,errorType,details);
        this.result = result;
    }

    public Boolean getResult() {
        return result;
    }


}
