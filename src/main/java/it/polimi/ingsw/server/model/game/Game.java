package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.GameState;
import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.ReschedulableTimer;
import it.polimi.ingsw.server.model.chat.Chat;
import it.polimi.ingsw.server.model.chat.Message;
import it.polimi.ingsw.server.model.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.cards.common.CommonGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.distributable.BagHolder;
import it.polimi.ingsw.server.model.distributable.DeckCommon;
import it.polimi.ingsw.server.model.distributable.DeckPersonal;
import it.polimi.ingsw.server.model.listeners.GameListener;
import it.polimi.ingsw.server.model.listeners.GameStateListener;
import it.polimi.ingsw.server.model.livingRoom.LivingRoomBoard;
import it.polimi.ingsw.server.model.exceptions.NotEnoughTilesException;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;
import it.polimi.ingsw.server.model.tokens.TokenPoint;

import java.rmi.RemoteException;
import java.util.*;

import static it.polimi.ingsw.server.ServerMain.logger;

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
     * The list of all the player that are currently crashed, so they are not in game effectively
     */
    private final ArrayList<Player> crashedPlayers;
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

    private GameState state;

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
     * the listener to the game status
     */
    private final GameListener gameListener;
    private final GameStateListener gameStateListener;
    private ReschedulableTimer crashTimer;
    private final long crashTimerDelay = 60000;

    /**
     * Constructor of the Game objects, it initializes all the attributes, set stdPointsReference, draw the CommonGoalCard, add the host to the game and assign to him a PersonalGoalCard
     * @param numPlayers is the total number of the players for the game, the initialization of the board changes based on this
     * @param host is the Player that have created the game
     * @throws NegativeFieldException if the number of cards to draw is negative
     * @throws PlayersNumberOutOfRange if the numPlayers is less than 2 or more than 4
     * @throws NotEnoughCardsException if the cards to be drawn are more than the number of cards loaded with the JSON file configuration
     */
    public Game(int numPlayers, Player host) throws NegativeFieldException, PlayersNumberOutOfRange, NotEnoughCardsException, IllegalFilePathException {
        Objects.requireNonNull(host);
        this.gameListener = new GameListener();
        this.gameStateListener = new GameStateListener();
        this.numPlayers = numPlayers;
        this.chat = new Chat();
        this.players = new ArrayList<>();
        this.crashedPlayers = new ArrayList<>();
        this.livingRoom = new LivingRoomBoard(numPlayers);
        DeckCommon deckCommon = new DeckCommon(numPlayers, "commonCards.json");
        this.deckPersonal = new DeckPersonal("personalCards.json", "pointsReference.json");
        this.bagHolder = new BagHolder();
        this.state = GameState.CREATED;
        this.isLastTurn = false;
        this.playerTurn = -1; //game not started
        this.stdPointsReference = new HashMap<>();

        this.stdPointsReference.put(3, 2); //3 adjacent 2 points
        this.stdPointsReference.put(4, 3); //4 adjacent 3 points
        this.stdPointsReference.put(5, 5); //5 adjacent 5 points
        this.stdPointsReference.put(6, 8); //6 or more adjacent 8 points
        try {
            this.commonGoalCards = deckCommon.draw(2);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        this.players.add(host);

    }

    @Override
    public void subscribeToListener(BoardSubscriber subscriber){
        livingRoom.subscribeToListener(subscriber);
    }

    @Override
    public void subscribeToListener(BookshelfSubscriber subscriber){
        players.forEach(p -> p.getBookshelf().subscribeToListener( subscriber));
    }

    @Override
    public void subscribeToListener(ChatSubscriber subscriber){
        chat.subscribeToListener(subscriber);
    }

    @Override
    public void subscribeToListener(PlayerSubscriber subscriber){
        players.forEach(p -> p.subscribeToListener(subscriber));
    }

    @Override
    public void subscribeToListener(GameSubscriber subscriber) {
        this.gameListener.addSubscriber(subscriber);
    }

    @Override
    public void subscribeToListener(GameStateSubscriber subscriber) {
        this.gameStateListener.addSubscriber(subscriber);
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
        if(this.isStarted()) throw new GameNotEndedException("The game has already started");

        Random random = new Random();
        int firstPlayerIndex = random.nextInt(this.numPlayers);

        Collections.rotate(players, -firstPlayerIndex);

        players.forEach(
                p -> {
                    try {
                        p.assignPersonalCard(deckPersonal.draw(1).get(0));
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    } catch (NegativeFieldException e) {
                        throw new RuntimeException(e);
                    } catch (NotEnoughCardsException e) {
                        throw new RuntimeException(e);
                    }
                    ArrayList<RemoteCommonGoalCard> remoteCards = new ArrayList<>(this.commonGoalCards);
                    this.gameListener.onCommonCardDraw(p.getUsername(), remoteCards);
                }
        );


        this.changeState(GameState.STARTED);
        this.playerTurn = 0;
        this.refillLivingRoom();
        ArrayList<String> tmp = new ArrayList<>();
        for(Player player : this.players){
            tmp.add(player.getUsername());
        }
        this.gameListener.notifyTurnOrder(tmp);
        this.gameListener.notifyPlayerInTurn(players.get(0).getUsername());
    }

    /**
     * This method is used to update the points of the player passed as argument
     * @param username the username of the player whose points you wish to update
     * @throws InvalidPlayerException if there isn't a player with that username inside the game
     * @throws NotEnoughSpaceException if there was an error with the CommonGoalCard
     * @throws GameNotStartedException if the game is not started yet
     */
    public void updatePlayerPoints(String username) throws InvalidPlayerException, NotEnoughSpaceException, GameNotStartedException {
        boolean toupdate = false;
        if (!this.isStarted()) throw new GameNotStartedException("the game has not started yet!");
        Optional<Player> player = searchPlayer(username);

        if(player.isEmpty())
            throw new InvalidPlayerException("Player is null");
        Player p = player.get();

        for ( int i = 0; i <  this.commonGoalCards.size(); i++) {
            if( this.commonGoalCards.get(i).verifyConstraint(player.get().getBookshelf()) ){
                try{
                    p.addToken(this.commonGoalCards.get(i).popTokenTo(p.getUsername()));
                    toupdate = true;
                    //logger.info("UPDATE STACK");
                    for(ScoringToken t: this.commonGoalCards.get(i).getTokenStack()){
                        //logger.info(String.valueOf(t.getScoreValue()));
                    }
                } catch (TokenAlreadyGivenException ignored){

                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        if(toupdate){
            ArrayList<RemoteCommonGoalCard> remoteCards = new ArrayList<>(this.commonGoalCards);
            this.gameListener.onCommonCardStateChange(remoteCards);
        }

        try {
            player.get().updatePoints(stdPointsReference);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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
        if(!this.isStarted()) throw new GameNotStartedException("The game has not started yet");

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
        if(!this.isRunning())
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


        if( currPlayer.getBookshelf().checkFreeSpace(column) < source.size()) throw new NotEnoughSpaceException("not enough space in the bookshelf!");

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
    public boolean checkValidRetrieve(ArrayList<Coordinates> coords) throws EmptySlotException, GameNotStartedException {
        if(!this.isRunning()) throw new GameNotStartedException("the game is not running!");
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
        if(!this.isStarted()) return false;

        if ( isLastTurn ) return true;

        if(players.get(this.playerTurn).getBookshelf().checkComplete()) {
            this.isLastTurn = true;
            try {
                players.get(this.playerTurn).addToken(new ScoringToken(TokenPoint.FIRSTPLAYER));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        return false;
    }

    /**
     * if the game is ended, it returns the username of the winner player
     * @throws GameNotEndedException if the game is not yet ended
     * @throws GameNotStartedException if the game has not started yet
     */
    public String getWinner() throws GameNotEndedException, GameNotStartedException {
        if(!this.isStarted())
            throw new GameNotStartedException("The game has not started yet");

        if(!this.isLastTurn)
            throw new GameNotEndedException("No one has completed the bookshelf");

        if(!this.isLastPlayerTurn())
            //If the playerTurn is not the last index of players arraylist it means that the game is not ended, because the arraylist is ordered from the first player to the last
            throw new GameNotEndedException("The last turn has not completed yet");

        for(Player player : players) {
            try {
                player.updatePoints(stdPointsReference);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        Player winner = players.stream().max(Comparator.comparing(Player::getPoints)).get();
        List<Player> tempPlayers = players.stream().sorted(Comparator.comparing(Player::getPoints)).toList();
        /* calls the listener */
        Map<String,Integer> scoreboard = new LinkedHashMap<>();
        for( Player player : tempPlayers){
            scoreboard.put(player.getUsername(),player.getPoints());
        }
        gameListener.onPlayerWins(winner.getUsername(), winner.getPoints(), scoreboard);

        return winner.getUsername();
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

                gameListener.onPlayerJoinGame(newPlayer.getUsername());
                this.updateListenerSubscriptions();
            } else {
                throw new NicknameAlreadyUsedException("A player with the same nickname is already present in the game");
            }
        }
    }

    private void updateListenerSubscriptions() {
        Set<PlayerSubscriber> psubs = new HashSet<>();
        Set<BookshelfSubscriber> bsubs = new HashSet<>();
        for( Player p : players){
            psubs.addAll(p.getSubs());
            bsubs.addAll(p.getBookshelf().getSubs());
        }
        for( PlayerSubscriber ps : psubs){
            this.subscribeToListener(ps);
        }

        for( BookshelfSubscriber bs : bsubs ){
            this.subscribeToListener(bs);
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
    public boolean isStarted() {
        return this.state != GameState.CREATED;
    }

    private boolean isRunning(){
        return this.state == GameState.STARTED || this.state == GameState.RESUMED;
    }

    /**
     * switches the turn to the next player
     * @return true, if the switch can be done, false otherwise
     */
    public boolean setPlayerTurn(){

        if(this.isLastTurn && this.playerTurn == this.players.size() - 1){
            try {
                this.getWinner();
                this.endGame();
            } catch (GameNotEndedException | GameNotStartedException ignored) {
            }
            return false;
        }

        do{
            this.playerTurn = (this.playerTurn + 1) % this.numPlayers;
        }
        while(crashedPlayers.contains(players.get(playerTurn)));

        this.gameListener.notifyPlayerInTurn(players.get(playerTurn).getUsername());

        return true;
    }

    /**
     * This is the method to retrieve all the messages relative to a player, either sent and received
     * @param player a String that represent the username of the player for which to search the messages
     * @return an Arraylist of messages relative to the player
     * @throws InvalidPlayerException if there isn't a player with that username inside the game
     */
    public ArrayList<Message> getPlayerMessages(String player) throws InvalidPlayerException {
        if(this.searchPlayer(player).isEmpty()) throw new InvalidPlayerException("Player is null");

        return chat.getPlayerMessages(player);
    }

    /**
     * This method is used to send a private message to another user
     * @param sender is the username of the sender
     * @param receiver is the username of the receiver
     * @param message is the message object that will be added to the list
     * @throws SenderEqualsRecipientException if the recipient and the sender of the message is the same user
     * @throws InvalidPlayerException if there isn't a player with that username inside the game
     */
    public void postMessage(String sender, String receiver, String message) throws SenderEqualsRecipientException, InvalidPlayerException {
        if(this.searchPlayer(sender).isEmpty()) throw new InvalidPlayerException("Player is null");
        if( this.searchPlayer(receiver).isEmpty() ) throw new InvalidPlayerException("Player is null");

        chat.postMessage(new Message(sender, receiver, message));
    }

    /**
     * This method is used to send a global chat message
     * @param sender is the username of the sender
     * @param message is the message object that will be added to the list
     * @throws InvalidPlayerException if there isn't a player with that username inside the game
     */
    public void postMessage(String sender, String message) throws InvalidPlayerException {
        if(this.searchPlayer(sender).isEmpty()) throw new InvalidPlayerException("Player is null");

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

    /**
     * This method is used to retrieve if a player is currently crashed or is active
     * @param player the username of the player
     * @return if the player is crashed
     */
    public boolean isCrashedPlayer(String player) {
        Optional<Player> realPlayer = this.searchPlayer(player);
        if( realPlayer.isEmpty() ) return false;
        return crashedPlayers.contains(realPlayer.get());
    }

    /**
     * Handle the changes in the game when a user crashes
     * @param username the username of the player that has crashed
     * @throws PlayerNotFoundException if there isn't a player with that username inside the game
     */
    public void handleCrashedPlayer(String username) throws PlayerNotFoundException {

        Optional<Player> tmpPlayer = this.searchPlayer(username);

        if(tmpPlayer.isPresent()){
            livingRoom.unsubscribeFromListener(username);
            players.forEach(p -> {
                p.getBookshelf().unsubscribeFromListener(username);
                p.unsubscribeFromListener(username);
            });
            chat.unsubscribeFromListener(username);
            this.gameListener.removeSubscriber(username);

            crashedPlayers.add(tmpPlayer.get());
            this.gameListener.notifyPlayerCrashed(tmpPlayer.get().getUsername());

            logger.info(username+" crashed!");
        }
        else
            throw new PlayerNotFoundException("The player with this username has not been found in the game");

        if(this.isStarted() && crashedPlayers.size() == numPlayers - 1) {
            this.changeState(GameState.PAUSED);
            this.crashTimer = new ReschedulableTimer();
            this.crashTimer.schedule(this::endGame, this.crashTimerDelay);

        }
    }

    private void endGame() {
        this.changeState(GameState.ENDED);
    }

    private void changeState(GameState gameState) {
        this.state = gameState;
        logger.info("GAME "+this+": GAME STATE CHANGED TO "+this.state);
        this.gameListener.notifyChangedGameState(this.state);
        try {
            this.gameStateListener.notifyChangedGameState(this.state, this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handle the changes in the game when a user rejoins the game after a crash
     * @param username the username of the player that has crashed
     * @throws PlayerNotFoundException if there isn't a player with that username inside the game
     */
    public void handleRejoinedPlayer(String username) throws PlayerNotFoundException {

        Optional<Player> tmpPlayer = this.searchPlayer(username);

        if(tmpPlayer.isPresent() && crashedPlayers.contains(tmpPlayer.get())){
            crashedPlayers.remove(tmpPlayer.get());
            logger.info(username+" rejoined!");
        }
        else
            throw new PlayerNotFoundException("The player with this username has not been found in the game");

        if(this.state.equals(GameState.PAUSED)) {
            this.crashTimer.cancel();
            this.changeState(GameState.RESUMED);
        }
    }

    /**
     * Method used to trigger all the listeners when a player joins or re-joins a game after a crash, to receive the complete status of the game such as players bookshelf's, points or livingRoomBoard
     * @param userToBeUpdated the username of the user that needs to receive the updates
     */
    public void triggerAllListeners(String userToBeUpdated) {
        Set<String> alreadyJoinedPlayers = new HashSet<>();
        for(Player player : players){
            try {
                player.triggerListener(userToBeUpdated);
                if(!player.getUsername().equals(userToBeUpdated))
                    alreadyJoinedPlayers.add(player.getUsername());

            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            player.getBookshelf().triggerListener(userToBeUpdated);
        }
        gameListener.notifyAlreadyJoinedPlayers(alreadyJoinedPlayers, userToBeUpdated);

        this.livingRoom.triggerListener(userToBeUpdated);
        ArrayList<RemoteCommonGoalCard> remoteCards = new ArrayList<>(this.commonGoalCards);
        this.gameListener.onCommonCardDraw(userToBeUpdated, remoteCards);

        if(this.isStarted()) {
            ArrayList<String> tmp = new ArrayList<>();
            for(Player player : this.players){
                tmp.add(player.getUsername());
            }

            this.gameListener.notifyTurnOrder(tmp);
            this.gameListener.notifyPlayerInTurn(players.get(playerTurn).getUsername());
            this.gameListener.notifyChangedGameState(GameState.STARTED);
        }

    }
}
