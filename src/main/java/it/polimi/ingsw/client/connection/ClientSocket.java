package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.localModel.Game;
import it.polimi.ingsw.Notifications;
import it.polimi.ingsw.client.view.SceneType;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.server.ReschedulableTimer;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientSocket implements ConnectionHandler{

    private final String ip;
    private final int port;
    private final Socket connection;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private final Queue<NetMessage> lastReceivedMessages;
    private final ExecutorService threadManager;
    private final ReschedulableTimer timer;
    private final ScheduledExecutorService heartBeatManager;
    private String username;
    private final ViewInterface view;
    private final long timerDelay = 15000;
    private Game gameModel;

    /**
     * Creates an instance of ClientSocket
     * @param ip ip of the server
     * @param port port of the server
     * @param view the view will be notified for updates
     */
    public ClientSocket(String ip, int port, ViewInterface view) {
        this.ip = ip;
        this.port = port;
        this.lastReceivedMessages = new ArrayDeque<>();
        this.timer = new ReschedulableTimer();
        this.threadManager = Executors.newCachedThreadPool();
        this.heartBeatManager = Executors.newSingleThreadScheduledExecutor();
        this.view = view;

        boolean connected = false;
        Socket tempConnection = null;

        while(!connected){
            /* game port */
            try {
                tempConnection = new Socket(ip, port);
                connected = true;

                //TODO: This postNotification broke things when using the GUI because they call the GuiController before it is initializated
                this.view.postNotification(Notifications.CONNECTED_SUCCESSFULLY);
            } catch ( UnknownHostException | ConnectException e) {
                view.postNotification(Notifications.ERR_CONNECTION_NO_AVAILABLE);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.connection = tempConnection;
        try {
            this.outputStream = new ObjectOutputStream(connection.getOutputStream());
            this.inputStream = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        timer.schedule(this::handleCrash, this.timerDelay);
        this.messagesHopper();
        this.sendHeartBeat();
        this.startParserAgent();
    }

    /**
     * Handles connection crash closing the connection
     */
    private void handleCrash() {
        view.postNotification(Notifications.ERR_CONNECTION_NO_LONGER_AVAILABLE);
        this.close();
    }


    /**
     * Closes the client connection to the server
     */
    @Override
    public void close(){
        synchronized (outputStream) {
            try {
                this.sendUpdate(new CloseConnectionMessage());
                heartBeatManager.shutdownNow();
                threadManager.shutdownNow();
                outputStream.close();
                inputStream.close();
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Adds player to the lobby
     * @param username the username used for joining the lobby
     */
    @Override
    public void JoinLobby(String username) {
        this.username = username;
        JoinLobbyMessage requestMessage = new JoinLobbyMessage(username);
        this.sendUpdate(requestMessage);
    }

    /**
     * Creates a new game
     * @param nPlayers the number of player for the game
     */
    @Override
    public void CreateGame(int nPlayers) {
        CreateGameMessage requestMessage = new CreateGameMessage(nPlayers);
        this.sendUpdate(requestMessage);
    }

    /**
     * Joins a Game
     */
    @Override
    public void JoinGame() {
        JoinGameMessage requestMessage = new JoinGameMessage();
        this.sendUpdate(requestMessage);
    }

    /**
     * Checks if a tiles retrieval is admissible
     * @param tiles the tiles to be selected
     */

    @Override
    public void checkValidRetrieve(ArrayList<Coordinates> tiles) {
        TileSelectionMessage requestMessage = new TileSelectionMessage(tiles);
        this.sendUpdate(requestMessage);
    }

    /**
     * Moves tiles in a personal bookshelf
     * @param tiles the selected tiles
     * @param column the column of the bookshelf that has been chosen
     */

    @Override
    public void moveTiles(ArrayList<Coordinates> tiles, int column) {
        MoveTilesMessage requestMessage = new MoveTilesMessage(tiles, column);
        this.sendUpdate(requestMessage);
    }

    /**
     * Sends a broadcast message to the game chat
     * @param content content of the message
     */
    public void sendMessage(String content){
        PostMessage message = new PostMessage(content);
        this.sendUpdate(message);
    }

    /**
     * Sends a private message to a player in the game
     * @param content content of the message
     * @param recipient recipient of the message
     */

    public void sendMessage(String recipient, String content){
        PostMessage message = new PostMessage(recipient, content);
        this.sendUpdate(message);
    }

    private void sendReceivedGame(Boolean errorOccurred) {
        GameReceivedMessage message = new GameReceivedMessage(errorOccurred);
        this.sendUpdate(message);
    }

    /**
     * Sends periodic signals to the server for connection checking
     */
    @Override
    public void sendHeartBeat()  {
        heartBeatManager.scheduleAtFixedRate(
            () -> {
                StillActiveMessage requestMessage = new StillActiveMessage();
                this.sendUpdate(requestMessage);
            },
            0, 5, TimeUnit.SECONDS);
    }


    /**
     * Continuously reads from the inputStream for new messages and notifies threads waiting for messages
     */
    private void messagesHopper()  {
        threadManager.execute( () -> {

            while(true) {
                synchronized (lastReceivedMessages) {
                    try {
                        NetMessage incomingMessage = (NetMessage) inputStream.readObject();
                        lastReceivedMessages.add(incomingMessage);
                        timer.reschedule(this.timerDelay);
                        //System.out.println(incomingMessage.getMessageType());
                        lastReceivedMessages.notifyAll();
                        lastReceivedMessages.wait(1);
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }


    private void startParserAgent(){
        threadManager.execute( () -> {
            while(this.connection.isConnected()){
                synchronized (lastReceivedMessages){
                    while(lastReceivedMessages.isEmpty()){
                        try {
                            lastReceivedMessages.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    this.parse(lastReceivedMessages.poll());
                }
            }
        });
    }
 /*   private NetMessage getMessageFromBuffer(MessageType type){
        NetMessage result;
        synchronized (lastReceivedMessages) {
            while (lastReceivedMessages.stream().noneMatch(message -> message.getMessageType().equals(type))) {
                try {
                    lastReceivedMessages.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            result =  lastReceivedMessages.stream().filter(message -> message.getMessageType().equals(type)).findFirst().get();
            lastReceivedMessages.remove(result);
        }
        return result;
    } */

    /**
     * Parser of NetMessage, calls specific method for every message type
     * @param responseMessage message to be parsed
     */
    private void parse(NetMessage responseMessage){
        if(responseMessage == null) return;
        switch (responseMessage.getMessageType()){
            case LOGIN_RETURN -> this.parse((LoginReturnMessage) responseMessage);
            case CONFIRM_GAME -> this.parse((ConfirmGameMessage) responseMessage);
            case CONFIRM_SELECTION -> this.parse((ConfirmSelectionMessage) responseMessage);
            case CONFIRM_MOVE -> this.parse((ConfirmMoveMessage) responseMessage);
            case NOTIFY_NEW_CHAT -> this.parse((NotifyNewChatMessage) responseMessage);
            case BOARD_UPDATE -> this.parse((BoardUpdateMessage) responseMessage);
            case BOOKSHELF_UPDATE -> this.parse((BookshelfUpdateMessage) responseMessage);
            case PERSONAL_CARD_UPDATE -> this.parse((PersonalGoalCardUpdateMessage) responseMessage);
            case COMMON_CARDS_UPDATE -> this.parse((CommonGoalCardsUpdateMessage) responseMessage);
            case NOTIFY_PLAYER_CRASHED -> this.parse((NotifyPlayerCrashedMessage) responseMessage);
            case NOTIFY_WINNING_PLAYER -> this.parse((NotifyWinnerPlayerMessage) responseMessage);
            case POINTS_UPDATE -> this.parse((PointsUpdateMessage) responseMessage);
            case CONFIRM_CHAT -> this.parse((ConfirmChatMessage) responseMessage);
            case TOKEN_UPDATE -> this.parse((TokenUpdateMessage) responseMessage);
            case NEW_PLAYER -> this.parse((NewPlayerInGame) responseMessage);
            case NOTIFY_PLAYER_IN_TURN -> this.parse((NotifyPlayerInTurnMessage) responseMessage);
            case NOTIFY_TURN_ORDER -> this.parse((NotifyTurnOrder) responseMessage);
            case GAME_STATUS -> this.parse((GameStatusMessage) responseMessage);
            case ALREADY_JOINED_PLAYERS -> this.parse((AlreadyJoinedPlayersMessage) responseMessage);
            default -> {
            }
        }
    }

    /**
     * Manages the login return message notifying the view
     * @param message response message
     */
    private void parse(LoginReturnMessage message){
        if(message.getConfirmLogin()){

            if (message.getConfirmRejoined()) {
                view.drawScene(SceneType.GAME);
                try {
                    this.gameModel = new Game(this.view,this.username);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                this.sendReceivedGame(false);
                view.postNotification(Notifications.GAME_RECONNECTION_SUCCESSFULLY);
            }else {
                view.drawScene(SceneType.LOBBY);
                view.postNotification(Notifications.JOINED_LOBBY_SUCCESSFULLY);
            }
        } else {
            /* an error occurred */
            view.postNotification(message.getErrorType(),message.getDetails());
            this.username = null;
        }
    }

    /**
     * Manages the game confirmation message notifying the view
     * @param message response message
     */
    private void parse(ConfirmGameMessage message){
        boolean errorOccurred = false;

        if(message.getConfirmGameCreation()){
            view.drawScene(SceneType.GAME);
            try {
                this.gameModel = new Game(this.view,this.username);

            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            view.postNotification(Notifications.GAME_CREATED_SUCCESSFULLY);
        } else if(message.getConfirmJoinedGame()){
            view.drawScene(SceneType.GAME);
            try {
                this.gameModel = new Game(this.view,this.username);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            view.postNotification(Notifications.GAME_JOINED_SUCCESSFULLY);
        }
        else{
            view.postNotification(message.getErrorType(),message.getDetails());
            errorOccurred = true;
        }

        this.sendReceivedGame(errorOccurred);
    }

    /**
     * Manages the confirm selection message notifying the view
     * @param message response message
     */
    private void parse(ConfirmSelectionMessage message){
        if(message.getConfirmSelection()){
            view.postNotification(Notifications.TILES_SELECTION_ACCEPTED);
        } else{
            view.postNotification(message.getErrorType(),message.getDetails());
        }
    }

    /**
     * Manages the confirm move message notifying the view
     * @param message response the message
     */
    private void parse(ConfirmMoveMessage message){
        if(message.getConfirmSelection()){
            view.postNotification(Notifications.TILES_MOVED_SUCCESSFULLY);
        }
    }

    /**
     * Adds a message to the chat notifying the view
     * @param message chat notification message
     */
    private void parse(NotifyNewChatMessage message){
        try {
            if(message.getRecipient().isEmpty())
                gameModel.receiveMessage(message.getSender(),message.getContent());
            else
                gameModel.receiveMessage(message.getSender(), message.getRecipient(), message.getContent());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Manages an update on the board notifying the view
     * @param message update message
     */
    private void parse(BoardUpdateMessage message) {
        try {
            gameModel.updateBoardStatus(message.getTilesInBoard());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Manages an update on the bookshelf notifying the view
     * @param message update message
     */
    private void parse(BookshelfUpdateMessage message){
        try {
            gameModel.updateBookshelfStatus(message.getUsername(), message.getInsertedTiles(),message.getColumn(), message.getCurrentMap());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Manages an update on the personal goal card notifying the view
     * @param message update message
     */
    private void parse(PersonalGoalCardUpdateMessage message){
        try {
            gameModel.updatePersonalGoalCard(message.getPlayer(), message.getCard());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Manages an update on the common goal card notifying the view
     * @param message update message
     */
    private void parse(CommonGoalCardsUpdateMessage message){
        try {
            gameModel.notifyCommonGoalCards(message.getCommonGoalCards());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Notifies the view that another player crashed
     * @param message notification message
     */
    private void parse(NotifyPlayerCrashedMessage message){
        try {
            this.gameModel.notifyPlayerCrashed(message.getUserCrashed());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Notifies the view that a player won the game
     * @param message notification message
     */
    private void parse(NotifyWinnerPlayerMessage message){
        try {
            gameModel.notifyWinningPlayer(message.getWinnerUser(),message.getWinnerPoints(),message.getScoreboard());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Manages an update in the points
     * @param message update message
     */
    private  void parse(PointsUpdateMessage message){
        try {
            gameModel.updatePoints(message.getUsername(), message.getTotalPoints(), message.getAddedPoints());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Manages a confirmchat message
     * @param message notification message
     */
    private void parse(ConfirmChatMessage message){
        if(!message.getResult())
            view.postNotification("Error while posting your message", "Your message was not posted in the chat due to an error, please retry.");
    }

    private void parse(TokenUpdateMessage message){
        try {
            gameModel.updateTokens(message.getPlayer(), message.getTokens());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    private void parse(NewPlayerInGame message){
        try {
            if(!this.username.equals(message.getNewPlayerUsername()))
                gameModel.notifyPlayerJoined(message.getNewPlayerUsername());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void parse(AlreadyJoinedPlayersMessage message){
        try {
            gameModel.notifyAlreadyJoinedPlayers(message.getAlreadyJoinedPlayers());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void parse(NotifyPlayerInTurnMessage message){
        try {
            gameModel.notifyPlayerInTurn(message.getUserInTurn());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void parse(NotifyTurnOrder message){
        try {
            gameModel.notifyTurnOrder(message.getPlayerOrder());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void parse(GameStatusMessage message) {
        try {
            gameModel.notifyChangedGameState(message.getState());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    private void sendUpdate(NetMessage update) {
        synchronized (outputStream) {
            try {
                outputStream.writeObject(update);
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}



