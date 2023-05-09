package it.polimi.ingsw.messages;

import java.io.Serial;

public class LoginReturnMessage extends ConfirmMessage {
    @Serial
    private static final long serialVersionUID = -8019058775724053002L;

    private final Boolean confirmLogin;
    private final Boolean confirmRejoined;

    public LoginReturnMessage(Boolean confirmLogin, String errorType, String details, Boolean rejoined) {
        super(MessageType.LOGIN_RETURN, errorType, details);
        this.confirmLogin = confirmLogin;

        this.confirmRejoined = rejoined;
    }
    public Boolean getConfirmLogin() {
        return confirmLogin;
    }
    public Boolean getConfirmRejoined() {
        return confirmRejoined;
    }
}
