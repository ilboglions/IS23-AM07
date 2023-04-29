package it.polimi.ingsw.remoteInterfaces;

public interface GameSubscriber extends ListenerSubscriber {
    void notifyPlayerJoined(String username);
    void notifyWinningPlayer(String username);
}
