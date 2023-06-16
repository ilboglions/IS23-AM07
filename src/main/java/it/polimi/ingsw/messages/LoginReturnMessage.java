package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message transfers the result of a login request
 */
public class LoginReturnMessage extends ConfirmMessage {
    @Serial
    private static final long serialVersionUID = -8019058775724053002L;

    private final Boolean confirmLogin;
    private final Boolean confirmRejoined;

    /**
     * Constructor of a LoginReturnMessage
     * @param confirmLogin result of the login
     * @param errorType type of the error, if there's any
     * @param details details of the message
     * @param rejoined true if the player is rejoining after a crash, false otherwise
     */
    public LoginReturnMessage(Boolean confirmLogin, String errorType, String details, Boolean rejoined) {
        super(MessageType.LOGIN_RETURN, errorType, details);
        this.confirmLogin = confirmLogin;

        this.confirmRejoined = rejoined;
    }

    /**
     *
     * @return the result of the login
     */
    public Boolean getConfirmLogin() {
        return confirmLogin;
    }

    /**
     *
     * @return true if the player is rejoining, false otherwise
     */
    public Boolean getConfirmRejoined() {
        return confirmRejoined;
    }
}
