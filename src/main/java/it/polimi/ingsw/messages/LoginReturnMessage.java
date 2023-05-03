package it.polimi.ingsw.messages;

import java.io.Serial;

public class LoginReturnMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -8019058775724053002L;

    private final Boolean confirmLogin;
    private final Boolean confirmRejoined;
    private final String details;
    private final String errorType;
    public LoginReturnMessage(Boolean confirmLogin, String errorType, String details, Boolean rejoined) {
        super(MessageType.LOGIN_RETURN);
        this.confirmLogin = confirmLogin;
        this.details = details;
        this.errorType = errorType;
        this.confirmRejoined = rejoined;
    }

    public Boolean getConfirmLogin() {
        return confirmLogin;
    }

    public String getDetails() {
        return details;
    }
    public String getErrorType() {
        return errorType;
    }

    public Boolean getConfirmRejoined() {
        return confirmRejoined;
    }
}
