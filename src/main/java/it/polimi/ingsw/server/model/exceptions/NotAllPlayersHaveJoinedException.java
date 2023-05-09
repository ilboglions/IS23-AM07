package it.polimi.ingsw.server.model.exceptions;

public class NotAllPlayersHaveJoinedException extends Exception{
    public NotAllPlayersHaveJoinedException(String msg){
        super(msg);
    }
}
