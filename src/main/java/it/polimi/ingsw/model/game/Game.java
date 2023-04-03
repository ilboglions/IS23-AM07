package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.chat.Chat;
import it.polimi.ingsw.model.chat.Message;
import it.polimi.ingsw.model.chat.exceptions.SenderEqualsRecipientException;
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
    private final DeckPersonal deckPersonal;
    private final BagHolder bagHolder;

    private final Chat chat;

    public Game(int numPlayers, Player host) throws FileNotFoundException, NegativeFieldException, PlayersNumberOutOfRange, NotEnoughCardsException {
        this.numPlayers = numPlayers;
        this.chat = new Chat();
        this.players = new ArrayList<>();
        this.livingRoom = new LivingRoomBoard(numPlayers);
        DeckCommon deckCommon = new DeckCommon(numPlayers, "src/main/java/it/polimi/ingsw/model/cards/confFiles/commonCards.json");
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

    /**
     * check if the game can be started
     * @return true, if the game have the right conditions to start, false otherwise
     */
    public boolean canStart() {
       return this.numPlayers == players.size();
    }



    public void start() throws NotAllPlayersHaveJoinedException{
        if (players.size() < numPlayers) throw new NotAllPlayersHaveJoinedException("player connected: "+players.size()+" players required: "+numPlayers);

        Random random = new Random();
        int firstPlayerIndex = random.nextInt(this.numPlayers);

        Collections.rotate(players, -firstPlayerIndex);
        this.isStarted = true;
        this.playerTurn = 0;
    }

    public void updatePlayerPoints(String username) throws InvalidPlayerException, NotEnoughSpaceException {
        Optional<Player> player = searchPlayer(username);

        if(player.isEmpty())
            throw new InvalidPlayerException();
        Player p = player.get();
        for ( CommonGoalCard c : this.commonGoalCards) {
            if( c.verifyConstraint(player.get().getBookshelf()) ){
                try{
                    p.addToken(c.popTokenTo(p.getUsername()));
                } catch (TokenAlreadyGivenException ignored){

                }

            }
        }

        player.get().updatePoints(stdPointsReference);
    }


    /*public ArrayList<ScoringToken> getPlayerTokens(String player) throws InvalidPlayerException {
        Optional<Player> current = searchPlayer(player);
        if(current.isEmpty())
            throw new InvalidPlayerException();

        return new ArrayList<>(current.get().getTokenAcquired());
    }*/

    /**
     * gets the player that is in the turn
     * @return a String representing the name of the player
     * @throws GameEndedException if the game is ended
     */
    public String getPlayerInTurn() throws GameEndedException {
        if (isLastTurn && this.playerTurn == this.players.size() - 1) throw new GameEndedException();
        return players.get(playerTurn).getUsername();
    }

    /**
     * make it possible to move tiles from the livingroomBoard to a column of the current player in  turn
     * @param source the source coordinates
     * @param column the column chosen by the player
     * @throws InvalidCoordinatesException if the coordinates chosen don't follow the constraints
     * @throws EmptySlotException if one of the coordinate is empty
     * @throws NotEnoughSpaceException it the column has no enough space left
     */
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

    /**
     * checks if the item tiles in a certain coordinate can be chosen by the player
     * @param coords the coordinates in the livingroomboard containing the interested tiles
     * @return true, if is possible to get that tiles, false otherwise
     * @throws EmptySlotException if one of the  coordinates referes to an empty slot
     */
    public boolean getItemTiles(ArrayList<Coordinates> coords) throws EmptySlotException {
        return  livingRoom.checkValidRetrieve(coords);
    }

    /**
     * checks if the livingroom need to be refilled
     * @return true, if is necessary to refill the livingroom, false otherwise
     */
    public boolean checkRefill(){
        return livingRoom.checkRefill();
    }


    /**
     * refills the living room
     */
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

    /**
     * checks if the bookshelf of the player in turn is completed
     * @return true, if the bookshelf is full, false otherwise
     */
    public boolean checkBookshelfComplete() {

        if ( isLastTurn ) return true;

        if(players.get(this.playerTurn).getBookshelf().checkComplete()) {
            this.isLastTurn = true;
            players.get(this.playerTurn).addToken(new ScoringToken(TokenPoint.FIRSTPLAYER));
            return true;
        }

        return false;
    }

    /**
     * if the game is ended, it returns the username of the winner player
     * @return the player that have won the game
     * @throws GameNotEndedException if the game is not yet ended
     */
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

    /**
     * make it possible to insert a new player in the game
     * @param newPlayer the Player to be insert
     * @throws NicknameAlreadyUsedException if the nickname of the player have been already choosen
     * @throws PlayersNumberOutOfRange if the game have reached the maximum number of players
     */
    public void addPlayer(Player newPlayer) throws NicknameAlreadyUsedException, PlayersNumberOutOfRange {

        if(players.size() >= this.numPlayers ) throw new PlayersNumberOutOfRange("max number of player reached! The player can not be added to the game!");

        if(newPlayer == null) {
            throw new NullPointerException();
        } else {
            if(!userUsed(newPlayer.getUsername())) {
                players.add(newPlayer); // player added to game active player
                try {
                    newPlayer.assignPersonalCard(deckPersonal.draw(1).get(0));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (NotEnoughCardsException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new NicknameAlreadyUsedException("A player with the same nickname is already present in the game");
            }
        }
    }

    private boolean userUsed(String user) {
        for (Player player : players) {
            if (player.getUsername().equals(user)) // if there is a player with the same user
                return true;    // true: there is already a player with the same username
        }
        return false;   //there are no players with this user
    }

    /**
     * used to search a player inside  the game
     * @param username the username used to search the player
     * @return the player, if exists, an empty optional, if no player with that username is present
     */
    public Optional<Player> searchPlayer(String username) {

        for(Player player : players) {
            if(player.getUsername().equals(username))
                return Optional.of(player);
        }

        return Optional.empty();
    }

    /**
     * get if the game is already started
     * @return true, if the game is started
     */
    public boolean getIsStarted() {
        return isStarted;
    }

    /**
     * switches the turn to the next player
     * @return true, if the switch can be done, false otherwise
     */
    public boolean setPlayerTurn(){

        if(this.isLastTurn && this.playerTurn == this.players.size() - 1){
            return false;
        }

        this.playerTurn = (this.playerTurn + 1) % this.numPlayers;

        return true;
    }


    @Override
    public ArrayList<Message> getPlayerMessages(String player) throws InvalidPlayerException {
        if(this.searchPlayer(player).isEmpty()) throw new InvalidPlayerException();

        return chat.getPlayerMessages(player);
    }

    @Override
    public void postMessage(String sender, Optional<String> receiver, String message) throws SenderEqualsRecipientException, InvalidPlayerException {
        if(this.searchPlayer(sender).isEmpty()) throw new InvalidPlayerException();

        if(receiver.isPresent() ){
            if( this.searchPlayer(receiver.get()).isEmpty() ) throw new InvalidPlayerException();
        }

        chat.postMessage(new Message(sender, receiver, message));

    }
}
