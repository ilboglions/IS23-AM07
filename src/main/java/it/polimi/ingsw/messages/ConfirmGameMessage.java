package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 *  This message is sent to inform the client about the creation of a game
 */
public class ConfirmGameMessage extends ConfirmMessage {
    @Serial
    private static final long serialVersionUID = -4175469055000899739L;

    private final Boolean confirmGameCreation;
    private final Boolean confirmJoinedGame;

    /**
     * Constructor of
     * @param confirmGameCreation true if the game has been created successfully, false otherwise
     * @param errorType if the game has some error this string gives the errorType
     * @param details details of the error
     * @param confirmJoinedGame true if the player has also successfully joined the created game, false otherwise
     */
    public ConfirmGameMessage(Boolean confirmGameCreation, String errorType, String details, Boolean confirmJoinedGame) {
        super(MessageType.CONFIRM_GAME,errorType,details);
        this.confirmGameCreation = confirmGameCreation;
        this.confirmJoinedGame = confirmJoinedGame;
    }

    /**
     *
     * @return the result of the GameCreation
     */
    public Boolean getConfirmGameCreation() {
        return confirmGameCreation;
    }

    /**
     *
     * @return the result of the JoinGame
     */
    public Boolean getConfirmJoinedGame() {
        return confirmJoinedGame;
    }
}
