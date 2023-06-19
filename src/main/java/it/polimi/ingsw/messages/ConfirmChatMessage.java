package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message acts as a confirmation message that a chat communication has been sent successfully/ has failed
 */
public class ConfirmChatMessage extends ConfirmMessage {
    @Serial
    private static final long serialVersionUID = 4011841211848568521L;

    private final Boolean result;

    /**
     * Constructor of a ConfirmChatMessage
     * @param result result of the sending process
     * @param errorType error type if the sending process failed
     * @param details details of the error
     */
    public ConfirmChatMessage(Boolean result, String errorType, String details) {
        super(MessageType.CONFIRM_CHAT,errorType,details);
        this.result = result;
    }

    /**
     *
     * @return the result of the sending process
     */
    public Boolean getResult() {
        return result;
    }


}
