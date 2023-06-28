package it.polimi.ingsw.server;

import it.polimi.ingsw.GameState;
import it.polimi.ingsw.Notifications;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static it.polimi.ingsw.server.ServerMain.logger;


/**
 *  ConnectionHandlerTCP is a class used to manage the connection to a client when connected over TCP. It parses the messages over the network
 *  and forwards the responses to the client.
 */
public class ConnectionHandlerTCP implements Runnable, BoardSubscriber, BookshelfSubscriber, ChatSubscriber, PlayerSubscriber, GameSubscriber {

    private final Socket socket;
    private boolean closeConnectionFlag;
    private final LobbyController lobbyController;
    private String username;
    private RemoteGameController gameController;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    private final int timerDelay = 15000;

    private final Queue<NetMessage> lastReceivedMessages;

    private final ExecutorService parseExecutors = Executors.newCachedThreadPool();

    /**
     * Constructor of a ConnectionHandlerTCP
     * @param socket connection socket to the client
     * @param lobbyController actual lobbyController reference
     */
    public ConnectionHandlerTCP(Socket socket, LobbyController lobbyController) {
        this.socket = socket;
        this.lobbyController = lobbyController;
        this.username = "";
        this.gameController = null;
        this.lastReceivedMessages = new ArrayDeque<>();
        closeConnectionFlag = false;
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes two threads that read and parse the messages simultaneously
     */
    public void run() {
        this.startParserAgent();
        this.messagesHopper();
    }

    /**
     * The MessageHopper runs an independent thread that reads continuously from the inputStream and notifies
     * other threads waiting for incoming messages from the client
     */
    private void messagesHopper()  {
        parseExecutors.execute( () -> {
            ReschedulableTimer timer = new ReschedulableTimer();
            timer.schedule(this::handleCrash, timerDelay);
            while(true) {
                synchronized (lastReceivedMessages) {
                    try {
                        NetMessage incomingMessage = (NetMessage) inputStream.readObject();
                        lastReceivedMessages.add(incomingMessage);
                        timer.reschedule(timerDelay);
                        lastReceivedMessages.notifyAll();
                        lastReceivedMessages.wait(1);
                    } catch ( ClassNotFoundException | InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch ( IOException ignored){

                    }
                }
            }
        });
    }

    /**
     * Runs an independent parserAgent that, when a new message is received,
     * runs a Parse Method to make the correct call to the controller
     */

    private void startParserAgent(){
        parseExecutors.execute( () -> {
            NetMessage response;
            while (true)
                synchronized (lastReceivedMessages) {
                    while (lastReceivedMessages.isEmpty()) {
                        try {
                            lastReceivedMessages.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        response = this.messageParser(lastReceivedMessages.poll());
                        sendUpdate(response);
                    } catch (RemoteException ignored) {}
                }
        });
    }


    /**
     * This method parse a NetMessage forwarding to the specific mathod based on the type of the message
     * @param inputMessage incoming NetMessage
     * @return response message from the server
     * @throws RemoteException RMI Exception
     */
    private NetMessage messageParser(NetMessage inputMessage) throws RemoteException {
        NetMessage outputMessage;
        logger.info(inputMessage.getMessageType().toString());
        switch (inputMessage.getMessageType()) {
            case JOIN_LOBBY -> outputMessage =  this.parse((JoinLobbyMessage) inputMessage);
            case CREATE_GAME -> outputMessage = this.parse((CreateGameMessage) inputMessage);
            case JOIN_GAME -> outputMessage = this.parse((JoinGameMessage) inputMessage);
            case TILES_SELECTION -> outputMessage = this.parse((TileSelectionMessage) inputMessage);
            case MOVE_TILES -> outputMessage = this.parse((MoveTilesMessage) inputMessage);
            case POST_MESSAGE -> outputMessage = this.parse((PostMessage) inputMessage);
            case GAME_RECEIVED_MESSAGE -> outputMessage = this.parse((GameReceivedMessage) inputMessage);
            case STILL_ACTIVE -> outputMessage = new StillActiveMessage();
            default -> {
                if(this.gameController != null){
                    try {
                        gameController.handleCrashedPlayer(this.username);
                    } catch (PlayerNotFoundException ignored) {}
                }
                closeConnectionFlag = true;
                outputMessage = new CloseConnectionMessage();
            }
        }

        return outputMessage;
    }

    /**
     * This method manages a JoinLobbyMessage
     * @param joinLobbyMessage incoming message
     * @return the response message from the server to the request
     * @throws RemoteException RMI Exception
     */
    private NetMessage parse(JoinLobbyMessage joinLobbyMessage) throws RemoteException {
        boolean result;
        String errorType = "";
        String desc;
        boolean hasPlayerJoined;
        if( gameController != null){
            errorType = Notifications.ERR_ALREADY_PLAYING_A_GAME.getTitle();
            result = false;
            hasPlayerJoined = false;
            desc = Notifications.ERR_ALREADY_PLAYING_A_GAME.getDescription();
        } else {
            if (username != null && !username.isEmpty()) {
                try {
                    lobbyController.handleCrashedPlayer(username);
                } catch (PlayerNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                gameController = lobbyController.enterInLobby(joinLobbyMessage.getUsername());
                this.username = joinLobbyMessage.getUsername();
                if(gameController != null) {
                    this.subscribeToAllListeners();
                    hasPlayerJoined = true;
                    desc ="Reconnecting to game...";
                } else {
                    hasPlayerJoined = false;
                    desc="Welcome in lobby!";
                }
                result = true;
            } catch (NicknameAlreadyUsedException e) {
                errorType = Notifications.ERR_USERNAME_ALREADY_TAKEN.getTitle();
                result = false;
                hasPlayerJoined = false;
                desc = Notifications.ERR_USERNAME_ALREADY_TAKEN.getDescription();
            } catch (InvalidPlayerException e) {
                errorType = Notifications.ERR_INVALID_USERNAME.getTitle();
                result = false;
                hasPlayerJoined = false;
                desc = Notifications.ERR_INVALID_USERNAME.getDescription();
            }
        }
        return new  LoginReturnMessage(result,errorType, desc, hasPlayerJoined);

    }

    /**
     * This method manages a createGame Message
     * @param createGameMessage request message from the client
     * @return the response message from the server to the request
     * @throws RemoteException RMI Exception
     */
    private NetMessage parse(CreateGameMessage createGameMessage) throws RemoteException {
        boolean result;
        String errorType = "";
        String desc = "";
        try {
            if( checkGameIsSet() ){
                return new ConfirmGameMessage(false, Notifications.ERR_INVALID_ACTION.getTitle(), Notifications.ERR_INVALID_ACTION.getDescription(), false);
            }
        } catch (NoAvailableGameException ignored) {}
        try {
            gameController = lobbyController.createGame(username, createGameMessage.getPlayerNumber());
            result = true;
            logger.info("Game CREATED Successfully");
            this.subscribeToAllListeners();
        } catch (InvalidPlayerException e) {
            result = false;
            errorType = Notifications.ERR_PLAYER_NO_JOINED_IN_LOBBY.getTitle();
            desc = Notifications.ERR_PLAYER_NO_JOINED_IN_LOBBY.getDescription();
        } catch (PlayersNumberOutOfRange e) {
            result = false;
            errorType = Notifications.ERR_GAME_N_PLAYER_OUT_OF_RANGE.getTitle();
            desc = Notifications.ERR_GAME_N_PLAYER_OUT_OF_RANGE.getDescription();
        }

        return new ConfirmGameMessage(result, errorType, desc, false);
    }

    /**
     * This method manages a JoinGame Message
     * @param inputMessage request message from the client
     * @return the response message from the server to the request
     * @throws RemoteException RMI Exception
     */
    private NetMessage parse(JoinGameMessage inputMessage) throws RemoteException {
        boolean result;
        String errorType = "";
        String desc = "";

        try {
            if( checkGameIsSet() ){
                return new ConfirmGameMessage(false, Notifications.ERR_INVALID_ACTION.getTitle(), Notifications.ERR_INVALID_ACTION.getDescription(), false);
            }
        } catch (NoAvailableGameException ignored) {}
        try {
            gameController = lobbyController.addPlayerToGame(username);
            this.subscribeToAllListeners();
            result = true;
        } catch (NicknameAlreadyUsedException e) {
            result = false;
            errorType = Notifications.ERR_USERNAME_ALREADY_TAKEN.getTitle();
            desc = Notifications.ERR_USERNAME_ALREADY_TAKEN.getDescription();
        } catch (NoAvailableGameException e) {
            result = false;
            errorType = Notifications.ERR_GAME_NO_AVAILABLE.getTitle();
            desc = Notifications.ERR_GAME_NO_AVAILABLE.getDescription();
        } catch (InvalidPlayerException e) {
            result = false;
            errorType = Notifications.ERR_PLAYER_NO_JOINED_IN_LOBBY.getTitle();
            desc = Notifications.ERR_PLAYER_NO_JOINED_IN_LOBBY.getDescription();
        }

        return new ConfirmGameMessage(false, errorType, desc, result);
    }
    /**
     * This method manages a TileSelection Message
     * @param tileSelectionMessage request message from the client
     * @return the response message from the server to the request
     * @throws RemoteException RMI Exception
     */
    private NetMessage parse(TileSelectionMessage tileSelectionMessage) throws RemoteException {
        boolean result;
        String errorType = "";
        String desc = "";

        try {
            this.checkGameIsSet();
        } catch (NoAvailableGameException e) {
            return new ConfirmSelectionMessage(false, Notifications.ERR_INVALID_ACTION.getTitle(), Notifications.ERR_INVALID_ACTION.getDescription());
        }

        try {
            result = gameController.checkValidRetrieve(username, tileSelectionMessage.getTiles());
            if(!result) {
                errorType = Notifications.INVALID_TILES_SELECTION.getTitle();
                desc = Notifications.INVALID_TILES_SELECTION.getDescription();
                logger.info(desc);
            }

        } catch (PlayerNotInTurnException e) {
            result = false;
            errorType = Notifications.PLAYER_NOT_IN_TURN.getTitle();
            desc = Notifications.PLAYER_NOT_IN_TURN.getDescription();
        } catch (GameNotStartedException e) {
            result = false;
            errorType = Notifications.ERR_GAME_NOT_STARTED.getTitle();
            desc = Notifications.ERR_GAME_NOT_STARTED.getDescription();
        } catch (GameEndedException e) {
            result = false;
            errorType = Notifications.ERR_GAME_ENDED.getTitle();
            desc = Notifications.ERR_GAME_ENDED.getDescription();
        } catch (EmptySlotException e) {
            result = false;
            errorType = Notifications.ERR_EMPTY_SLOT_SELECTED.getTitle();
            desc = Notifications.ERR_EMPTY_SLOT_SELECTED.getDescription();
        }
        return new ConfirmSelectionMessage(result, errorType, desc);
    }
    /**
     * This method manages a Post Message
     * @param postMessage request message from the client
     * @return the response message from the server to the request
     * @throws RemoteException RMI Exception
     */
    private NetMessage parse(PostMessage postMessage) throws RemoteException {
        boolean result;
        String errorType = "";
        String desc = "";
        try {
            this.checkGameIsSet();
        } catch (NoAvailableGameException e) {
            return new ConfirmSelectionMessage(false, Notifications.ERR_INVALID_ACTION.getTitle(), Notifications.ERR_INVALID_ACTION.getDescription());
        }
        if (!postMessage.getRecipient().equals("")) {
            try {
                result = true;
                gameController.postDirectMessage(username, postMessage.getRecipient(), postMessage.getContent());
            } catch (InvalidPlayerException e) {
                result = false;
                errorType = "InvalidPlayerException";
                desc = e.getMessage();
            } catch (SenderEqualsRecipientException e) {
                result = false;
                errorType = Notifications.CHAT_SENDER_EQUALS_RECIPIENT.getTitle();
                desc = Notifications.CHAT_SENDER_EQUALS_RECIPIENT.getDescription();
            }
        } else {
            try {
                gameController.postBroadCastMessage(username, postMessage.getContent());
                result = true;
            } catch (InvalidPlayerException e) {
                result = false;
                errorType = "InvalidPlayerException";
                desc = e.getMessage();
            }
        }
        logger.info(postMessage.getContent() + " " + postMessage.getRecipient());
        return new ConfirmChatMessage(result, errorType, desc);
    }

    /**
     * This method manages a MoveTiles Message
     * @param moveTilesMessage request message from the client
     * @return the response message from the server to the request
     * @throws RemoteException RMI Exception
     */
    private NetMessage parse(MoveTilesMessage moveTilesMessage) throws RemoteException {
        boolean result;
        String errorType = "";
        String desc = "";

        try {
            this.checkGameIsSet();
        } catch (NoAvailableGameException e) {
            return new ConfirmSelectionMessage(false, Notifications.ERR_INVALID_ACTION.getTitle(), Notifications.ERR_INVALID_ACTION.getDescription());
        }

        try {
            gameController.moveTiles(username, moveTilesMessage.getTiles(), moveTilesMessage.getColumn());
            result = true;
        } catch (GameNotStartedException e) {
            result = false;
            errorType = Notifications.ERR_GAME_NOT_STARTED.getTitle();
            desc = Notifications.ERR_GAME_NOT_STARTED.getDescription();
        } catch (GameEndedException e) {
            result = false;
            errorType = Notifications.ERR_GAME_ENDED.getTitle();
            desc = Notifications.ERR_GAME_ENDED.getDescription();
        } catch (EmptySlotException e) {
            result = false;
            errorType = Notifications.ERR_EMPTY_SLOT_SELECTED.getTitle();
            desc = Notifications.ERR_EMPTY_SLOT_SELECTED.getDescription();
        } catch (NotEnoughSpaceException e) {
            result = false;
            errorType = Notifications.NO_SPACE_IN_BOOKSHELF_COLUMN.getTitle();
            desc = Notifications.NO_SPACE_IN_BOOKSHELF_COLUMN.getDescription();
        } catch (InvalidCoordinatesException e) {
            result = false;
            errorType = Notifications.INVALID_TILES_SELECTION.getTitle();
            desc = Notifications.INVALID_TILES_SELECTION.getDescription();
        } catch (PlayerNotInTurnException e) {
            result = false;
            errorType = Notifications.PLAYER_NOT_IN_TURN.getTitle();
            desc = Notifications.PLAYER_NOT_IN_TURN.getDescription();
        }
        return new ConfirmMoveMessage(result,errorType,desc);
    }

    /**
     * This method manages a GameReceived Message
     * @param inputMessage request message from the client
     * @return the response message from the server to the request
     */
    private NetMessage parse(GameReceivedMessage inputMessage) {
        if(!inputMessage.getErrorOccurred()) {
            try {
                gameController.triggerAllListeners(this.username);
            } catch (RemoteException ignored) {}
        }

        return new StillActiveMessage();
    }


    private void subscribeToAllListeners(){

        try {
            this.checkGameIsSet();
        } catch (NoAvailableGameException e) {
            return;
        }
        try {
            gameController.subscribeToListener((PlayerSubscriber) this);
            gameController.subscribeToListener((ChatSubscriber) this);
            gameController.subscribeToListener((BoardSubscriber) this);
            gameController.subscribeToListener((BookshelfSubscriber) this);
            gameController.subscribeToListener((GameSubscriber) this);
        } catch (RemoteException ignored){
        }
    }

    /**
     * This method manages a connection crash event
     */
    public void handleCrash() {
        logger.info("Connection lost");
        if(username == null || username.isEmpty()) return;
        if (gameController == null) {
            try {
                lobbyController.handleCrashedPlayer(this.username);
                logger.info("Handling crash in the lobby... player hasn't join any game!");
            } catch (PlayerNotFoundException | RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                gameController.handleCrashedPlayer(this.username);
                logger.info("Handling crash in the game!");
            } catch (RemoteException | PlayerNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called when there's an update on the board sending a TCP message
     * It updates the client
     * @param tilesInBoard all the tiles that are in the board
     */
    @Override
    public void updateBoardStatus(Map<Coordinates, ItemTile> tilesInBoard) {
        BoardUpdateMessage update = new BoardUpdateMessage(tilesInBoard);
        this.sendUpdate(update);
    }

    /**
     * This method is called when there's an update on a player's bookshelf.
     * It updates the client
     * @param player the username of the players that owns the bookshelf
     * @param tilesInserted the tiles inserted by the player
     * @param colChosen the column chosen for the insertion
     * @param currentTilesMap the updated bookshelf disposition of tiles
     */
    @Override
    public void updateBookshelfStatus(String player, ArrayList<ItemTile> tilesInserted, int colChosen, Map<Coordinates, ItemTile> currentTilesMap) {
        BookshelfUpdateMessage update = new BookshelfUpdateMessage(tilesInserted, colChosen, currentTilesMap, player);
        this.sendUpdate(update);
    }


    /**
     * This method is called when there's an update on a chat message that must be received by this user
     * It updates the client
     * @param from the sender of the message
     * @param msg the content of the message
     */
    @Override
    public void receiveMessage(String from, String msg) {
        logger.info("SENDING MESSAGE TO " + this.username);
        NotifyNewChatMessage update = new NotifyNewChatMessage(from, msg);
        this.sendUpdate(update);
    }

    /**
     * This method is called when there's an update on a private chat message that must be received by this user
     * @param from the sender of the message
     * @param recipient the recipient of the message
     * @param msg the content message
     */
    @Override
    public void receiveMessage(String from, String recipient, String msg) {
        logger.info("SENDING MESSAGE TO " + this.username);
        NotifyNewChatMessage update = new NotifyNewChatMessage(from, recipient, msg);
        this.sendUpdate(update);
    }

    /**
     * @return the username of the player that is related to this connection
     */
    @Override
    public String getSubscriberUsername() {
        return username;
    }

    /**
     * This method is called when there's a point update to a user
     * It updates the client
     * @param player the player that has a points update
     * @param overallPoints the overall points of the player
     * @param addedPoints the points added on this state change
     */
    @Override
    public void updatePoints(String player, int overallPoints, int addedPoints) {
        PointsUpdateMessage update = new PointsUpdateMessage(player, overallPoints, addedPoints);
        this.sendUpdate(update);
    }

    /**
     * This method is called when there's an update in the ScoringToken list received by user
     * It updates the client
     * @param player the username of the player
     * @param tokenPoints the updated List of  ScoringToken received by the player
     */
    @Override
    public void updateTokens(String player, ArrayList<ScoringToken> tokenPoints) {
        TokenUpdateMessage update = new TokenUpdateMessage(tokenPoints, player);
        this.sendUpdate(update);
    }

    /**
     * This method is called to update the player about their personalGoalCard
     * It updates the client
     * @param player username of the player
     * @param remotePersonal new PersonalGoalCard
     */
    @Override
    public void updatePersonalGoalCard(String player, RemotePersonalGoalCard remotePersonal) {
        PersonalGoalCardUpdateMessage update = new PersonalGoalCardUpdateMessage(player, remotePersonal);
        this.sendUpdate(update);
    }

    /**
     * This method is called to notify that a new player joined the game
     * It updates the client
     * @param username the username of the player that has joined
     */
    @Override
    public void notifyPlayerJoined(String username) {
        NewPlayerInGame update = new NewPlayerInGame(username);
        this.sendUpdate(update);

        try {
            gameController.subscribeToListener((PlayerSubscriber) this);
            gameController.subscribeToListener((BookshelfSubscriber) this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
}

    /**
     * This method is called to notify that a player won the game
     * It updates the client
     * @param username the username of the winning player
     * @param points the points of the player that have won the game
     * @param scoreboard the total scoreboard, the key is the username and the value the points of the user
     */
    @Override
    public void notifyWinningPlayer(String username, int points, Map<String,Integer> scoreboard) {
        NotifyWinnerPlayerMessage update = new NotifyWinnerPlayerMessage(username, points, scoreboard);
        this.sendUpdate(update);
    }

    /**
     * This method is called to notify an update in the list of commonGoalCards
     * It updates the client
     * @param commonGoalCards is the list of the common goals of the game
     */
    @Override
    public void notifyCommonGoalCards(ArrayList<RemoteCommonGoalCard> commonGoalCards){
        CommonGoalCardsUpdateMessage update = new CommonGoalCardsUpdateMessage(commonGoalCards);
        this.sendUpdate(update);
    }

    /**
     * This method is called to notify that it's change in the turn
     * It updates the client
     * @param username username of the player now in turn
     */
    @Override
    public void notifyPlayerInTurn(String username) {
        NotifyPlayerInTurnMessage update = new NotifyPlayerInTurnMessage(username, this.username.equals(username));
        this.sendUpdate(update);
    }

    /**
     * This method is called to notify that a player crashed
     * It updates the client
     * @param userCrashed username of the crashed player
     */
    @Override
    public void notifyPlayerCrashed(String userCrashed){
        NotifyPlayerCrashedMessage update = new NotifyPlayerCrashedMessage(userCrashed);
        this.sendUpdate(update);
    }

    /**
     * This method is called to notify the turn order of the players
     * It updates the client
     * @param playerOrder list of the players of the game in order of turn
     */
    @Override
    public void notifyTurnOrder(ArrayList<String> playerOrder){
        NotifyTurnOrder update = new NotifyTurnOrder(playerOrder);
        this.sendUpdate(update);
    }


    /**
     * This method specifies the list of the players that are already in the game when a client joins
     * It updates the client
     * @param alreadyJoinedPlayers set of the players already in the game
     * @throws RemoteException RMI Exception
     */
    @Override
    public void notifyAlreadyJoinedPlayers(Set<String> alreadyJoinedPlayers) throws RemoteException {
        this.sendUpdate( new AlreadyJoinedPlayersMessage(alreadyJoinedPlayers));
    }

    /**
     * This method is called to notify a change in the state of the game
     * @param newState new state of the game
     * @throws RemoteException RMI Exception
     */
    @Override
    public void notifyChangedGameState(GameState newState) throws RemoteException {
        GameStatusMessage update = new GameStatusMessage(newState);
        this.sendUpdate(update);
    }

    /**
     * This method is called to actually forward a NetMessage to the client
     * @param update NetMessage to be sent
     */
    private void sendUpdate(NetMessage update){
        synchronized (outputStream) {
            try {
                logger.info("SENDING..." + update.getMessageType()+ " to "+this.username );
                outputStream.writeObject(update);
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private boolean checkGameIsSet() throws NoAvailableGameException {
        if( gameController == null) throw  new NoAvailableGameException("The client hasn't joined a game!");
        return true;
    }

}



