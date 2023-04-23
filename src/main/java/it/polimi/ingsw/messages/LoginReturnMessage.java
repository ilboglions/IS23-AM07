package it.polimi.ingsw.messages;

import java.io.Serial;

public class LoginReturnMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -8019058775724053002L;

    private final Boolean confirmLogin;
    private final String details;
    public LoginReturnMessage(String username, Boolean confirmLogin, String details) {
        super(username, MessageType.LOGIN_RETURN);
        this.confirmLogin = confirmLogin;
        this.details = details;
    }

    public Boolean getConfirmLogin() {
        return confirmLogin;
    }

    public String getDetails() {
        return details;
    }
}
