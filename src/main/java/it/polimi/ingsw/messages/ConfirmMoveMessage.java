package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message is used to confirm a Move operation
 */
public class ConfirmMoveMessage extends ConfirmMessage {

    @Serial
    private static final long serialVersionUID = -2161316585011839258L;
    private final Boolean confirmSelection;

    /**
     *
     * @param confirmSelection true if the selection has been accepted
     * @param errorType type of error (if there's any)
     * @param details details of the message
     */
    public ConfirmMoveMessage(Boolean confirmSelection, String errorType, String details) {
        super(MessageType.CONFIRM_MOVE, errorType, details);
        this.confirmSelection = confirmSelection;


    }

    /**
     *
     * @return the result of the move
     */
    public Boolean getConfirmSelection() {
        return confirmSelection;
    }


}
