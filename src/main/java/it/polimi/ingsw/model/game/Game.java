package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.cards.common.CommonGoalCard;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.distributable.DeckCommon;
import it.polimi.ingsw.model.tokens.ScoringToken;

import java.util.ArrayList;
import java.util.Map;

public class Game implements GameController{

    private LivingRoom livingRoom;
    private ArrayList<Player> players;
    private ArrayList<CommonGoalCard> commonGoalCards;
    private Map<Integer, Integer> stdPointsReference;
    private int numPlayers;
    private boolean isStarted;
    private int playerTurn;
    private boolean isBookshelfComplete;
    private DeckPersonal deckPersonal;
    private DeckCommon deckCommon;
    private BagHolder bagHolder;
    public boolean getState() {
        return false;
    }

    public Game(int numPlayers) {
        this.numPlayers = numPlayers;
    }
    public void setState(boolean newState) {
    }

    public void start() {
    }

    public int getPlayerPoints() {
        return 0;
    }

    public ArrayList<ScoringToken> getPlayerTokens(String player) {
        Player current = searchPlayer(player);
        ArrayList<ScoringToken> currentToken = current.getTokenAcquired().clone();  // clone the scoring tokens of the player not to expose the rep
        return currentToken;
    }

    public int getPlayerTurn() {
        return 0;
    }

    public void moveTiles(ArrayList<Coordinates> source, ArrayList<Coordinates> destination) {

    }

    public ArrayList<Coordinates> getLivingRoomCoordinates() {
        return null;
    }

    public ArrayList<ItemTile> getItemTiles(ArrayList<Coordinates> coo) {
        return null;
    }

    public void refilLivingRoom() {

    }

    public boolean checkBookshelfComplete() {
        return false;
    }

    public Player getWinner() {
        return null;
    }

    public void endGame() {

    }

    public boolean addPlayer(Player newPlayer) {
        if(newPlayer == null) {
            throw new NullPointerException();
        } else {
            if(!userUsed(newPlayer.getUsername())) {
                players.add(newPlayer); // player added to game active player
                return true;    // true means that the player has been added and no problem occured
            } else {
                return false;   // false: the player has not been added to the game
            }
        }

    }
    private boolean userUsed(String user) {
        for(int i=0; i<players.size(); i++) {
            if(players.get(i).getUsername.equals(user)) // if there is a player with the same user
                return true;    // true: there is already a player with the same username
        }
        return false;   //there are no players with this user
    }

    private Player searchPlayer(String username) {}
    public boolean getIsStarted() {}

    private void setPlayerTurn() {}
    private void drawCommongGoalCards() {}

}
