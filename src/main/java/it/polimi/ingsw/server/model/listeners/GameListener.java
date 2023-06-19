package it.polimi.ingsw.server.model.listeners;
import it.polimi.ingsw.GameState;
import it.polimi.ingsw.remoteInterfaces.GameSubscriber;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * the GameListener is used to notify the client about updates concerning the game
 */
public class GameListener extends Listener<GameSubscriber> {

    /**
     * Notifies that a new player joined the game
     * @param username username of the new player
     */
    public void onPlayerJoinGame(String username){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        for (GameSubscriber sub : subscribers) {
            try {
                sub.notifyPlayerJoined(username);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Notifies that new CommonGoalCards have been drawn
     * @param username username of the player to be updated
     * @param commonGoalCards ArrayList of all the RemoteCommonGoalCards
     */
    public void onCommonCardDraw(String username, ArrayList<RemoteCommonGoalCard> commonGoalCards) {
        Set<GameSubscriber> subscribers = this.getSubscribers();
        for( GameSubscriber sub :  subscribers ){
            try {
                if(sub.getSubscriberUsername().equals(username)){
                    sub.notifyCommonGoalCards(commonGoalCards);
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Notifies the players when one of them wins
     * @param username username of the winner player
     * @param points total points of the winner
     * @param scoreboard final scoreboard of the game
     */
    public void onPlayerWins(String username, int points, Map<String,Integer> scoreboard){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                sub.notifyWinningPlayer(username, points, scoreboard);
            } catch (RemoteException ignored) {
            }
        });
    }

    /**
     * Notifies the players the turn of a new player
     * @param username username of the player in turn
     */
    public void notifyPlayerInTurn(String username){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                sub.notifyPlayerInTurn(username);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Notifies the other players when one of them crashes
     * @param userCrashed username of the crashed player
     */
    public void notifyPlayerCrashed(String userCrashed){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                if(!sub.getSubscriberUsername().equals(userCrashed))
                    sub.notifyPlayerCrashed(userCrashed);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Updates all the players about the turn order
     * @param playerOrder ArrayList with each player in order of turn
     */
    public void notifyTurnOrder(ArrayList<String> playerOrder){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                sub.notifyTurnOrder(playerOrder);
            } catch (RemoteException ignored) {}
        });
    }


    /**
     * Notifies a player about the players that already joined the game
     * @param alreadyJoinedPlayers set of all the players already in the game
     * @param userToBeUpdated username of the player to be updated
     */
    public void notifyAlreadyJoinedPlayers(Set<String> alreadyJoinedPlayers, String userToBeUpdated) {
        Set<GameSubscriber> subscribers = this.getSubscribers();
        for(GameSubscriber sub : subscribers){
            try {
                if( sub.getSubscriberUsername().equals(userToBeUpdated))
                    sub.notifyAlreadyJoinedPlayers(alreadyJoinedPlayers);
            } catch (RemoteException ignored) {}
        }
    }

    /**
     * Notifies a new change in the state of the game
     * @param newState new state of the game
     */
    public void notifyChangedGameState(GameState newState) {
        Set<GameSubscriber> subscribers = this.getSubscribers();
        for(GameSubscriber sub : subscribers){
            try {
                sub.notifyChangedGameState(newState);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Notifies a change in the state of the CommonGoalCards
     * @param c updated list of the RemoteCommonGoalCards
     */
    public void onCommonCardStateChange(ArrayList<RemoteCommonGoalCard> c) {
        Set<GameSubscriber> subscribers = this.getSubscribers();
        for(GameSubscriber sub : subscribers){
            try{
                sub.notifyCommonGoalCards(c);
            } catch ( RemoteException ignored){

            }
        }
    }
}
