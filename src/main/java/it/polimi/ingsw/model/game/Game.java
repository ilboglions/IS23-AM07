package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.cards.CommonGoalCard;
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

    public void setState(boolean newState) {

    }

    public void start() {

    }

    public int getPlayerPoints() {
        return 0;
    }

    public ArrayList<ScoringToken> getPlayerTokens(String player) {
        return null;
    }

    public int getPlayerTurn() {
        return 0;
    }

    public void moveTile(ArrayList<Coordinates> source, ArrayList<Coordinates> destination) {

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

    public boolean addPlayer(String username) {}
    private Player searchPlayer(String username) {}
    public boolean getIsStarted() {}

    private void setPlayerTurn() {}
    private void drawCommongGoalCards() {}

}
