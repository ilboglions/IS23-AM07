package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.server.model.chat.Chat;
import it.polimi.ingsw.server.model.chat.Message;
import it.polimi.ingsw.server.model.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.cards.common.CommonGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.distributable.BagHolder;
import it.polimi.ingsw.server.model.distributable.DeckCommon;
import it.polimi.ingsw.server.model.distributable.DeckPersonal;
import it.polimi.ingsw.server.model.livingRoom.LivingRoomBoard;
import it.polimi.ingsw.server.model.livingRoom.exceptions.NotEnoughTilesException;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;
import it.polimi.ingsw.server.model.tokens.TokenPoint;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class is used to handle the main logic of a Game
 */
public class Game implements GameModelInterface {
    /**
     * The board of the game
     */
    private final LivingRoomBoard livingRoom;
    /**
     * The list of all the player that have joined the game
     */
    private final ArrayList<Player> players;
    /**
     * The list of the extracted CommonGoalCard for the game
     */
    private final ArrayList<CommonGoalCard> commonGoalCards;
    /**
     * The reference to the points for the adjacency groups inside the player's bookshelf, keys are the number of tiles for groups and the values are the relative points
     */
    private final Map<Integer, Integer> stdPointsReference;
    /**
     * The total number of the player for this game
     */
    private final int numPlayers;
    /**
     * Store if the game is started or is waiting for players to join
     */
    private boolean isStarted;
    /**
     * Store the index of the current player in the round
     */
    private int playerTurn;
    /**
     * Store if there is a player that have completed the bookshelf, so if it is the last round of the game
     */
    private boolean isLastTurn;
    /**
     * The reference to the DeckPersonal to draw PersonalGoalCard for each player
     */
    private final DeckPersonal deckPersonal;
    /**
     * The reference to the BagHolder to draw ItemTiles to refill the LivingRoomBoard
     */
    private final BagHolder bagHolder;

    /**
     * The reference to the Chat of the game
     */
    private final Chat chat;

    /**
     * Constructor of the Game objects, it initializes all the attributes, set stdPointsReference, draw the CommonGoalCard, add the host to the game and assign to him a PersonalGoalCard
     * @param numPlayers is the total number of the players for the game, the initialization of the board changes based on this
     * @param host is the Player that have created the game
     * @throws FileNotFoundException if there was a problem loading the JSON config files
     * @throws NegativeFieldException if the number of cards to draw is negative
     * @throws PlayersNumberOutOfRange if the numPlayers is less than 2 or more than 4
     * @throws NotEnoughCardsException if the cards to be drawn are more than the number of cards loaded with the JSON file configuration
     */
    public Game(int numPlayers, Player host) throws FileNotFoundException, NegativeFieldException, PlayersNumberOutOfRange, NotEnoughCardsException {
        Objects.requireNonNull(host);

        this.numPlayers = numPlayers;
        this.chat = new Chat();
        this.players = new ArrayList<>();
        this.livingRoom = new LivingRoomBoard(numPlayers);
        DeckCommon deckCommon = new DeckCommon(numPlayers, "src/main/java/it/polimi/ingsw/server/model/cards/confFiles/commonCards.json");
        this.deckPersonal = new DeckPersonal("src/main/java/it/polimi/ingsw/server/model/cards/confFiles/personalCards.json", "src/main/java/it/polimi/ingsw/server/model/cards/confFiles/pointsReference.json");
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
        host.assignPersonalCard(deckPersonal.draw(1).get(0));
    }

    /**
     * check if the game can be started
     * @return true, if the game have the right conditions to start, false otherwise
     */
    public boolean canStart() {
       return this.numPlayers == players.size();
    }

    /**
     * This method is used to start the game, it chooses randomly a starting player
     * @throws NotAllPlayersHaveJoinedException if the number of player that have joined is less than the number set when the game was created
     * @throws GameNotEndedException if the game has already started
     */
    public void start() throws NotAllPlayersHaveJoinedException, GameNotEndedException {
        if(players.size() < numPlayers) throw new NotAllPlayersHaveJoinedException("player connected: "+players.size()+" players required: "+numPlayers);
        if(this.isStarted) throw new GameNotEndedException("The game has already started");

        Random random = new Random();
        int firstPlayerIndex = random.nextInt(this.numPlayers);

        Collections.rotate(players, -firstPlayerIndex);
        this.isStarted = true;
        this.playerTurn = 0;
        this.refillLivingRoom();
    }

    /**
     * This method is used to update the points of the player passed as argument
     * @param username the username of the player whose points you wish to update
     * @throws InvalidPlayerException if there isn't a player with that username inside the game
     * @throws NotEnoughSpaceException if there was an error with the CommonGoalCard
     */
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
     * @throws GameNotStartedException if the game has not started yet
     */
    public String getPlayerInTurn() throws GameEndedException, GameNotStartedException {
        if(isLastTurn && this.playerTurn == this.players.size() - 1) throw new GameEndedException();
        if(!this.isStarted) throw new GameNotStartedException("The game has not started yet");

        return players.get(playerTurn).getUsername();
    }

    /**
     * make it possible to move tiles from the livingRoomBoard to a column of the current player in  turn
     * @param source the source coordinates
     * @param column the column chosen by the player
     * @throws InvalidCoordinatesException if the coordinates chosen don't follow the constraints
     * @throws EmptySlotException if one of the coordinate is empty
     * @throws NotEnoughSpaceException it the column has no enough space left
     * @throws GameNotStartedException if the game has not started yet
     */
    public void moveTiles(ArrayList<Coordinates> source, int column) throws InvalidCoordinatesException, EmptySlotException, NotEnoughSpaceException, GameNotStartedException {
        /*
            checks done:
             - source LivingRoomBoard slot actually have a tile
             - column is in the proper range
             - destination coordinates refer to only a common column
         */
        if(!this.isStarted)
            throw new GameNotStartedException("The game has not started yet");

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
            else {
                temp.add(i, tile.get()); //we are assured that the value is present by the previous if statement
                livingRoom.removeTile(source.get(i));
            }
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
    public boolean checkValidRetrieve(ArrayList<Coordinates> coords) throws EmptySlotException {
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
        ArrayList<ItemTile> removed, useToRefill;
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
        if(!this.isStarted) return false;

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
     * @throws GameNotStartedException if the game has not started yet
     */
    public String getWinner() throws GameNotEndedException, GameNotStartedException {
        if(!this.isStarted)
            throw new GameNotStartedException("The game has not started yet");

        if(!this.isLastTurn)
            throw new GameNotEndedException("No one has completed the bookshelf");

        if(!this.isLastPlayerTurn())
            //If the playerTurn is not the last index of players arraylist it means that the game is not ended, because the arraylist is ordered from the first player to the last
            throw new GameNotEndedException("The last turn has not completed yet");

        for(Player player : players) {
            player.updatePoints(stdPointsReference);
        }

        return players.stream().max(Comparator.comparing(Player::getPoints)).get().getUsername();
    }

    /**
     * make it possible to insert a new player in the game
     * @param newPlayer the Player to be inserted
     * @throws NicknameAlreadyUsedException if the nickname of the player have been already chosen
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
                } catch (NegativeFieldException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new NicknameAlreadyUsedException("A player with the same nickname is already present in the game");
            }
        }
    }

    /**
     * Checks if there is already a user in the game with the same username
     * @param user the username to check
     * @return if there is a player with the same username
     */
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
        Objects.requireNonNull(username);

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
    public void postMessage(String sender, String receiver, String message) throws SenderEqualsRecipientException, InvalidPlayerException {
        if(this.searchPlayer(sender).isEmpty()) throw new InvalidPlayerException();


        if( this.searchPlayer(receiver).isEmpty() ) throw new InvalidPlayerException();

        chat.postMessage(new Message(sender, receiver, message));

    }

    public void postMessage(String sender, String message) throws InvalidPlayerException {
        if(this.searchPlayer(sender).isEmpty()) throw new InvalidPlayerException();

        try {
            chat.postMessage(new Message(sender, message));
        } catch ( SenderEqualsRecipientException ignored ){

        }
    }

    /**
     * Checks if currently is the turn of the last player before the game ends
     * @return if it's the last player's turn
     */
    public boolean isLastPlayerTurn(){
        return this.isLastTurn && this.playerTurn == this.players.size() - 1;
    }
}
