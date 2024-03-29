package it.polimi.ingsw.client.connection;


import it.polimi.ingsw.client.localModel.Game;
import it.polimi.ingsw.Notifications;
import it.polimi.ingsw.client.view.SceneType;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.ReschedulableTimer;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This is the class that implements the communication with the server through RMI
 */
public class ClientRMI implements ConnectionHandler{

    Registry registry;
    private final RemoteLobbyController lobbyController;
    private String username;
    private RemoteGameController gameController;
    private final ScheduledExecutorService heartBeatManager;
    private final ReschedulableTimer timer;
    private final long timerDelay = 15000;
    private final ViewInterface view;
    private Game gameModel;
    private final String ip;
    private final int port;

    private boolean connectionClosed;

    /**
     * Creates an instance of ClientRMI
     * @param view the view will be notified for updates
     * @param hostName the ip address of the server
     * @param portNumber the port number of the server
     */
    public ClientRMI(ViewInterface view, String hostName, int portNumber) {
        boolean connected = false;
        this.connectionClosed = false;
        RemoteLobbyController tempLobbyController = null;
        this.ip = hostName;
        this.port = portNumber;

        this.timer = new ReschedulableTimer();
        this.heartBeatManager = Executors.newSingleThreadScheduledExecutor();
        this.view = view;
        while(!connected){
            try{
                this.registry= LocateRegistry.getRegistry(hostName, portNumber);
                String remoteObjectName = "lobby_controller";
                tempLobbyController =  (RemoteLobbyController) registry.lookup(remoteObjectName );
                connected = true;
            }catch(RemoteException | NotBoundException e){
                try {
                    this.view.postNotification(Notifications.ERR_CONNECTION_NO_AVAILABLE);
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ignored) {
                }
            }
        }
        this.lobbyController = tempLobbyController;
        this.view.postNotification(Notifications.CONNECTED_SUCCESSFULLY);
        this.sendHeartBeat();
    }

    /**
     * Close the connection with the server
     */
    @Override
    public void close() {
        if(connectionClosed)
            return;
        connectionClosed = true;
        heartBeatManager.shutdownNow();
        if(gameController != null){
            try {
                gameController.handleCrashedPlayer(this.username);
            } catch (RemoteException | PlayerNotFoundException ignored) {}
        }
        gameController = null;
        gameModel = null;

    }

    /**
     * Adds player to the lobby
     * @param username the username used for joining the lobby
     */
    @Override
    public void JoinLobby(String username) {

        if( checkGameIsSet() ){
            view.postNotification(Notifications.ERR_ALREADY_PLAYING_A_GAME);
            return;
        }

        this.username = username;
        try {
            gameController = lobbyController.enterInLobby(username);
            if( gameController == null){
                view.drawScene(SceneType.LOBBY);
                view.postNotification(Notifications.JOINED_LOBBY_SUCCESSFULLY);
                lobbyController.triggerHeartBeat(this.username);
            } else {
                view.drawScene(SceneType.GAME);
                gameController.triggerHeartBeat(this.username);
                this.gameModel = new Game(this.view,this.username);
                this.subscribeListeners();
                gameController.triggerAllListeners(this.username);
                view.postNotification(Notifications.GAME_RECONNECTION_SUCCESSFULLY);
            }
        } catch (NicknameAlreadyUsedException e) {
            view.postNotification(Notifications.ERR_USERNAME_ALREADY_TAKEN);
        } catch (InvalidPlayerException e) {
            view.postNotification(Notifications.ERR_INVALID_USERNAME);
        } catch (RemoteException ignored){}
    }


    /**
     * Creates a new game
     * @param nPlayers the number of player for the game
     * @throws RemoteException RMI remote error
     */
    @Override
    public void CreateGame(int nPlayers) throws RemoteException {
        if( checkGameIsSet() ){
            view.postNotification(Notifications.ERR_ALREADY_PLAYING_A_GAME);
            return;
        }

        try {
            this.gameController = this.lobbyController.createGame(username, nPlayers);
            view.drawScene(SceneType.GAME);
            gameController.triggerHeartBeat(this.username);
            this.gameModel = new Game(this.view,this.username);
            this.subscribeListeners();
            this.gameController.triggerAllListeners(this.username);
            this.view.postNotification(Notifications.GAME_CREATED_SUCCESSFULLY);
        } catch (InvalidPlayerException e) {
            this.view.postNotification(Notifications.ERR_PLAYER_NO_JOINED_IN_LOBBY);
        } catch (PlayersNumberOutOfRange e) {
            view.postNotification(Notifications.ERR_GAME_N_PLAYER_OUT_OF_RANGE);
        }

    }


    /**
     * Subscribe the client gameModel as an observer for game notifications
     */
    private void subscribeListeners(){

        try {
            gameController.subscribeToListener((ChatSubscriber) this.gameModel);
            gameController.subscribeToListener((GameSubscriber) this.gameModel);
            gameController.subscribeToListener((PlayerSubscriber) this.gameModel);
            gameController.subscribeToListener((BookshelfSubscriber) this.gameModel);
            gameController.subscribeToListener((BoardSubscriber) this.gameModel);
        } catch (RemoteException ignored) {}

    }

    /**
     * Joins a Game
     */
    @Override
    public void JoinGame() {

        if( checkGameIsSet() ){
            view.postNotification(Notifications.ERR_ALREADY_PLAYING_A_GAME);
            return;
        }

        try {
            this.gameController = this.lobbyController.addPlayerToGame(username);
            view.drawScene(SceneType.GAME);
            gameController.triggerHeartBeat(this.username);
            this.gameModel = new Game(this.view,this.username);
            this.subscribeListeners();
            view.postNotification(Notifications.GAME_JOINED_SUCCESSFULLY);
            gameController.triggerAllListeners(this.username);
        } catch (NicknameAlreadyUsedException ignored) {
            //THIS EXCEPTION NEVER HAPPENS, THE PLAYER IN THIS CASE IS
            //NOT ALLOWED IN THE LOBBY IN THE FIRST PLACE
        } catch (NoAvailableGameException e) {
            view.postNotification(Notifications.ERR_GAME_NO_AVAILABLE);
        } catch (InvalidPlayerException e) {
            view.postNotification(Notifications.ERR_PLAYER_NO_JOINED_IN_LOBBY);
        }catch (RemoteException ignored){
        }

    }

    /**
     * Checks if a tiles retrieval is admissible
     * @param tiles the tiles to be selected
     */
    @Override
    public void checkValidRetrieve(ArrayList<Coordinates> tiles) {


        if(!this.checkGameIsSet()) {
            view.postNotification(Notifications.ERR_INVALID_ACTION);
            return;
        }

        try {
            if(gameController.checkValidRetrieve(this.username,tiles))
                view.postNotification(Notifications.TILES_SELECTION_ACCEPTED);
            else
                view.postNotification(Notifications.INVALID_TILES_SELECTION);
        } catch (EmptySlotException e) {
            view.postNotification(Notifications.ERR_EMPTY_SLOT_SELECTED);
        } catch (GameNotStartedException e) {
            view.postNotification(Notifications.ERR_GAME_NOT_STARTED);
        } catch (GameEndedException e) {
            view.postNotification(Notifications.ERR_GAME_ENDED);
        } catch (PlayerNotInTurnException e) {
            view.postNotification(Notifications.NOT_YOUR_TURN);
        } catch (RemoteException ignored) {
        }
    }

    /**
     * Moves tiles in a personal bookshelf
     * @param tiles the selected tiles
     * @param column the column of the bookshelf that has been chosen
     * @throws RemoteException RMI remote error
     */
    @Override
    public void moveTiles(ArrayList<Coordinates> tiles, int column) throws RemoteException {

        if(!this.checkGameIsSet()) {
            view.postNotification(Notifications.ERR_INVALID_ACTION);
            return;
        }

        try {
            gameController.moveTiles(this.username,tiles, column);
            view.postNotification(Notifications.TILES_MOVED_SUCCESSFULLY);
        } catch (GameNotStartedException e) {
            view.postNotification(Notifications.ERR_GAME_NOT_STARTED);
        } catch (GameEndedException e) {
            view.postNotification(Notifications.ERR_GAME_ENDED);
        } catch (EmptySlotException e) {
            view.postNotification(Notifications.ERR_EMPTY_SLOT_SELECTED);
        } catch (NotEnoughSpaceException e) {
            view.postNotification(Notifications.NO_SPACE_IN_BOOKSHELF_COLUMN);
        } catch (InvalidCoordinatesException e) {
            view.postNotification(Notifications.INVALID_TILES_SELECTION);
        }  catch (PlayerNotInTurnException e) {
            view.postNotification(Notifications.PLAYER_NOT_IN_TURN);
        }

    }

    /**
     * Sends periodic signals to the server for connection checking
     */
    @Override
    public void sendHeartBeat() {
        heartBeatManager.scheduleAtFixedRate(
            () -> {
                try {
                    if(gameController == null && lobbyController != null && this.username != null){
                        this.scheduleTimer();
                        lobbyController.triggerHeartBeat(this.username);
                        timer.reschedule(this.timerDelay);
                    } else if(gameController != null){
                        this.scheduleTimer();
                        gameController.triggerHeartBeat(this.username);

                        timer.reschedule(this.timerDelay);
                    }
                } catch (Exception e) {
                    this.view.backToLobby();
                }
            },
            0, 2, TimeUnit.SECONDS);
    }

    private void scheduleTimer() {
        if(!timer.isScheduled()){
            timer.schedule(()->{
                if(this.connectionClosed)
                    return;
                this.view.backToLobby();
            },this.timerDelay);
        }
    }

    /**
     * @return true if the game is set
     */
    private boolean checkGameIsSet() {
        return gameModel != null;
    }

    /**
     * Sends a broadcast message to the game chat
     * @param content content of the message
     */
    public void sendMessage(String content){

        if(!this.checkGameIsSet()){
            view.postNotification(Notifications.ERR_INVALID_ACTION);
            return;
        }
        try {
            gameController.postBroadCastMessage(this.username,content);
        } catch (RemoteException | InvalidPlayerException ignored) {
        }
    }

    /**
     * Sends a private message to a player in the game
     * @param content content of the message
     * @param recipient recipient of the message
     */

    public void sendMessage(String recipient, String content){
        if(!this.checkGameIsSet()){
            view.postNotification(Notifications.ERR_INVALID_ACTION);
            return;
        }
        try {
            gameController.postDirectMessage(this.username,recipient,content);
        } catch (RemoteException ignored) {
        } catch (InvalidPlayerException ignored) {
            /* to decide, if the written recipient does not exist, we could just ignore the chat message*/
        } catch (SenderEqualsRecipientException e) {
            view.postNotification(Notifications.CHAT_SENDER_EQUALS_RECIPIENT);
        }
    }

    @Override
    public String getServerIP() {
        return this.ip;
    }

    @Override
    public int getServerPort() {
        return this.port;
    }
}
