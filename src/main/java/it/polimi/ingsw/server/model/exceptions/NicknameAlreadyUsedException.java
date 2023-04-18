package it.polimi.ingsw.server.model.exceptions;

public class NicknameAlreadyUsedException extends Exception{
    public NicknameAlreadyUsedException(String message) {
        super(message);
    }
}
