package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message is used to confirm a selection Operation
 */
public class ConfirmSelectionMessage extends ConfirmMessage {
    @Serial
    private static final long serialVersionUID = -2537603478256907125L;

    private final Boolean confirmSelection;


    /**
     * Constrcutor of a ConfirmSelectionMessage
     * @param confirmSelection result of the selection
     * @param errorType type of the error
     * @param details details of the message
     */
    public ConfirmSelectionMessage(Boolean confirmSelection, String errorType, String details) {
        super(MessageType.CONFIRM_SELECTION,errorType,details);
        this.confirmSelection = confirmSelection;

    }

    /**
     * @return the result of the selection
     */
    public Boolean getConfirmSelection() {
        return confirmSelection;
    }

}
