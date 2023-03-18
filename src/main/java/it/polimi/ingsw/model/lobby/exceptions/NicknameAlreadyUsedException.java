package it.polimi.ingsw.model.lobby.exceptions;

public class NicknameAlreadyUsedException extends Exception{
    public NicknameAlreadyUsedException(String message) {
        super(message);
    }
}
