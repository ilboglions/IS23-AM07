package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.cards.common.CommonGoalCard;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.distributable.BagHolder;
import it.polimi.ingsw.model.distributable.DeckCommon;
import it.polimi.ingsw.model.distributable.DeckPersonal;
import it.polimi.ingsw.model.livingRoom.LivingRoomBoard;
import it.polimi.ingsw.model.livingRoom.exceptions.NotEnoughTilesException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.tiles.ItemTile;
import it.polimi.ingsw.model.tokens.ScoringToken;
import it.polimi.ingsw.model.tokens.TokenPoint;

import java.io.FileNotFoundException;
import java.util.*;

public class Game implements GameModelInterface {
    private final LivingRoomBoard livingRoom;
    private final ArrayList<Player> players;
    private final ArrayList<CommonGoalCard> commonGoalCards;
    private final Map<Integer, Integer> stdPointsReference;
    private final int numPlayers;
    private boolean isStarted;
    private int playerTurn;
    private boolean isLastTurn;
    private DeckPersonal deckPersonal;
    private DeckCommon deckCommon;
    private BagHolder bagHolder;

    public Game(int numPlayers, Player host) throws FileNotFoundException, NegativeFieldException, PlayersNumberOutOfRange, NotEnoughCardsException {
        this.numPlayers = numPlayers;
        this.players = new ArrayList<>();
        this.livingRoom = new LivingRoomBoard(numPlayers);
        this.deckCommon = new DeckCommon(numPlayers,"src/main/java/it/polimi/ingsw/model/cards/confFiles/commonCards.json");
        this.deckPersonal = new DeckPersonal("src/main/java/it/polimi/ingsw/model/cards/confFiles/personalCards.json", "src/main/java/it/polimi/ingsw/model/cards/confFiles/pointsReference.json");
        this.bagHolder = new BagHolder();
        this.isStarted = false;
        this.isLastTurn = false;
        this.playerTurn = -1; //game not started
        this.stdPointsReference = new HashMap<>();

        this.stdPointsReference.put(3, 2); //3 adjacent 2 points
        this.stdPointsReference.put(4, 3); //4 adjacent 3 points
        this.stdPointsReference.put(5, 5); //5 adjacent 5 points
        this.stdPointsReference.put(6, 8); //6 or more adjacent 8 points
        this.commonGoalCards = deckCommon.draw(2);
        this.players.add(host);
    }

    public void start() throws NotAllPlayersHaveJoinedException{
        if (players.size() < numPlayers) throw new NotAllPlayersHaveJoinedException("player connected: "+players.size()+" players required: "+numPlayers);

        Random random = new Random();
        int firstPlayerIndex = random.nextInt(this.numPlayers);

        Collections.rotate(players, -firstPlayerIndex);
        this.isStarted = true;
        this.playerTurn = 0;
    }

    public void updatePlayerPoints(String username) throws InvalidPlayerException {
        Optional<Player> player = searchPlayer(username);

        if(player.isEmpty())
            throw new InvalidPlayerException();

        player.get().updatePoints(stdPointsReference);
    }


    /*public ArrayList<ScoringToken> getPlayerTokens(String player) throws InvalidPlayerException {
        Optional<Player> current = searchPlayer(player);
        if(current.isEmpty())
            throw new InvalidPlayerException();

        return new ArrayList<>(current.get().getTokenAcquired());
    }*/

    public String getPlayerInTurn() throws GameEndedException {
        if (isLastTurn && this.playerTurn == this.players.size() - 1) throw new GameEndedException();
        return players.get(playerTurn).getUsername();
    }

    public void moveTiles(ArrayList<Coordinates> source, int column) throws InvalidCoordinatesException, EmptySlotException, NotEnoughSpaceException {
        /*
            checks done:
             - source LivingRoomBoard slot actually have a tile
             - column is in the proper range
             - destination coordinates refer to only a common column
         */

        ArrayList<ItemTile> temp = new ArrayList<>(); // tile to be added to the playerBookshelf
        Optional<ItemTile> tile;
        Player currPlayer = players.get(playerTurn); // current player
        if(source == null || source.contains(null)) {
            throw new NullPointerException("Source is/contains null");
        } else if (source.isEmpty()) {
            throw new InvalidCoordinatesException("Source list is empty");
        }
        if(!(column >= 0 && column < currPlayer.getBookshelf().getColumns())) {
            throw new InvalidCoordinatesException("Selected column is out of range");
        }

        for(int i=0; i<source.size(); i++) {
            tile = livingRoom.getTile(source.get(i));
            if(tile.isEmpty())
                throw new EmptySlotException("Trying to retrieve a tile from a empty slot");
            else
                temp.add(i, tile.get()); //we are assured that the value is present by the previous if statement
        }
        /*
            now we should have validated the source coordinates
            and have the requested tiles in the temp list
         */
        currPlayer.getBookshelf().insertItemTile(column,temp);
    }


    public boolean getItemTiles(ArrayList<Coordinates> coords) throws EmptySlotException {
        return  livingRoom.checkValidRetrieve(coords);
    }

    public boolean checkRefill(){
        return livingRoom.checkRefill();
    }

    public void refillLivingRoom() {

        // here we don't do the check if the livingBoard actually needs to be refilled, before changing the turn the controller calls for the check
        ArrayList<ItemTile> removed, useToRefill, fromBag;
        removed = livingRoom.emptyBoard();
        try {
            useToRefill = bagHolder.draw(livingRoom.getNumCells() - removed.size());
            useToRefill.addAll(0,removed);
            livingRoom.refillBoard(useToRefill);
        } catch (NegativeFieldException e) {
            throw new RuntimeException(e.getMessage()+ "\n Removed tiles are more than the number of livingBoard cells");
        } catch (NotEnoughTilesException e) {
            throw new RuntimeException(e.getMessage() + "\n refillBoard did not receive enough tile to refill the board");
        }
    }

    public boolean checkBookshelfComplete() {

        if ( isLastTurn ) return true;

        if(players.get(this.playerTurn).getBookshelf().checkComplete()) {
            this.isLastTurn = true;
            players.get(this.playerTurn).addToken(new ScoringToken(TokenPoint.FIRSTPLAYER));
            return true;
        }

        return false;
    }

    public String getWinner() throws GameNotEndedException {
        if(!this.isLastTurn)
            throw new GameNotEndedException("No one has completed the bookshelf");

        if(playerTurn != players.size()-1)
            //If the playerTurn is not the last index of players arraylist it means that the game is not ended, because the arraylist is ordered from the first player to the last
            throw new GameNotEndedException("The last turn has not completed yet");

        for(Player player : players) {
            player.updatePoints(stdPointsReference);
        }

        return players.stream().max(Comparator.comparing(Player::getPoints)).get().getUsername();
    }

    public boolean addPlayer(Player newPlayer) {
        if(newPlayer == null) {
            throw new NullPointerException();
        } else {
            if(!userUsed(newPlayer.getUsername())) {
                players.add(newPlayer); // player added to game active player
                return true;    // true means that the player has been added and no problem occurred
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

    public boolean setPlayerTurn(){

        if(this.isLastTurn && this.playerTurn == this.players.size() - 1){
            return false;
        }

        this.playerTurn = (this.playerTurn + 1) % this.numPlayers;


        return true;
    }

}
