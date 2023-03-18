package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.cards.common.CommonGoalCard;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.distributable.DeckCommon;
import it.polimi.ingsw.model.game.exceptions.InvalidPlayerException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.tiles.ItemTile;
import it.polimi.ingsw.model.tokens.ScoringToken;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

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

    public Game(int numPlayers, Player host) {
        this.numPlayers = numPlayers;
        this.players = new ArrayList<Player>();

        this.players.add(host);
    }
    public void setIsStarted(boolean newState) {
        isStarted = newState;
    }

    public void start() {
    }

    public int getPlayerPoints() {
        return 0;
    }

    public ArrayList<ScoringToken> getPlayerTokens(String player) throws InvalidPlayerException {
        Optional<Player> current = searchPlayer(player);
        if(current.isEmpty())
            throw new InvalidPlayerException();

        return new ArrayList<ScoringToken>(current.get().getTokenAcquired());
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

    public void refillLivingRoom() {

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
            if(players.get(i).getUsername().equals(user)) // if there is a player with the same user
                return true;    // true: there is already a player with the same username
        }
        return false;   //there are no players with this user
    }

    public Optional<Player> searchPlayer(String username) {

        for(Player player : players) {
            if(player.getUsername().equals(username))
                return Optional.of(player);
        }

        return Optional.empty();
    }
    public boolean getIsStarted() {
        return isStarted;
    }

    private void setPlayerTurn() {}
    private void drawCommonGoalCards() {}

}
