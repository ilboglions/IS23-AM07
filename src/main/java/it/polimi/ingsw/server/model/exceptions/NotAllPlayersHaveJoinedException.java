package it.polimi.ingsw.server.model.exceptions;

/**
 * Exception launched if it's asked for the game to start but not all the players have joined yet
 */
public class NotAllPlayersHaveJoinedException extends Exception{
    /**
     * Constructor of a NotAllPlayersHaveJoinedException
     * @param msg details of the exception
     */
    public NotAllPlayersHaveJoinedException(String msg){
        super(msg);
    }
}
