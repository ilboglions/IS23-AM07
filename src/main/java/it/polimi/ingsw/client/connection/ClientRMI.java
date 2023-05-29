package it.polimi.ingsw.client.connection;


import it.polimi.ingsw.client.localModel.Game;
import it.polimi.ingsw.Notifications;
import it.polimi.ingsw.client.view.SceneType;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.ReschedulableTimer;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.*;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    public ClientRMI(ViewInterface view) throws RemoteException, NotBoundException {
        boolean connected = false;
        RemoteLobbyController tempLobbyController = null;

        this.timer = new ReschedulableTimer();
        this.heartBeatManager = Executors.newSingleThreadScheduledExecutor();
        this.view = view;
        while(!connected){
            try{
                this.registry= LocateRegistry.getRegistry();
                String remoteObjectName = "lobby_controller";
                tempLobbyController =  (RemoteLobbyController) registry.lookup(remoteObjectName);
                connected = true;
            }catch(ConnectException e){
                try {
                    this.view.postNotification(Notifications.ERR_CONNECTION_NO_AVAILABLE);
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        this.lobbyController = tempLobbyController;
        this.view.postNotification(Notifications.CONNECTED_SUCCESSFULLY);
        this.sendHeartBeat();
    }

    @Override
    public void close() {
        System.out.println("timer expired");
    }

    @Override
    public void JoinLobby(String username) throws RemoteException {
        if(gameController != null ){
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
                view.postNotification(Notifications.GAME_RECONNECTION_SUCCEFFULLY);
                gameController.triggerHeartBeat(this.username);
                this.gameModel = new Game(this.view,this.username);
                this.subscribeListeners();
                gameController.triggerAllListeners(this.username);
            }
        } catch (NicknameAlreadyUsedException e) {
            view.postNotification(Notifications.ERR_USERNAME_ALREADY_TAKEN);
        } catch (InvalidPlayerException e) {
            view.postNotification(Notifications.ERR_INVALID_USERNAME);
        }
    }

    @Override
    public void CreateGame(int nPlayers) throws RemoteException {

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

    private void subscribeListeners(){

        try {
            gameController.subscribeToListener((ChatSubscriber) this.gameModel);
            gameController.subscribeToListener((GameSubscriber) this.gameModel);
            gameController.subscribeToListener((PlayerSubscriber) this.gameModel);

            gameController.subscribeToListener((BookshelfSubscriber) this.gameModel);
            gameController.subscribeToListener((BoardSubscriber) this.gameModel);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void JoinGame() throws RemoteException {

        try {
            this.gameController = this.lobbyController.addPlayerToGame(username);
            view.drawScene(SceneType.GAME);
            gameController.triggerHeartBeat(this.username);
            this.gameModel = new Game(this.view,this.username);
            this.subscribeListeners();
            gameController.triggerAllListeners(this.username);
            view.postNotification(Notifications.GAME_JOINED_SUCCESSFULLY);
        } catch (NicknameAlreadyUsedException e) {
            throw new RuntimeException(e);
        } catch (NoAvailableGameException e) {
            view.postNotification(Notifications.ERR_GAME_NO_AVAILABLE);
        } catch (InvalidPlayerException e) {
            view.postNotification(Notifications.ERR_PLAYER_NO_JOINED_IN_LOBBY);
        }

    }

    @Override
    public void checkValidRetrieve(ArrayList<Coordinates> tiles) {

        try {
            this.checkGameIsSet();
        } catch (NoAvailableGameException e) {
            throw new RuntimeException(e);
        }

        //here the view will be notified that the action has been executed correctly
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
        } catch (RemoteException e) {
            this.close();
        }
    }

    @Override
    public void moveTiles(ArrayList<Coordinates> tiles, int column) throws RemoteException {

        try {
            this.checkGameIsSet();
        } catch (NoAvailableGameException e) {
            throw new RuntimeException(e);
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

    @Override
    public void sendHeartBeat() {
        heartBeatManager.scheduleAtFixedRate(
            () -> {
                try {
                    if(gameController == null && lobbyController != null && this.username != null){
                        lobbyController.triggerHeartBeat(this.username);
                        if(!timer.isScheduled()){
                            timer.schedule(this::close,this.timerDelay);
                        }
                        timer.reschedule(this.timerDelay);
                    } else if(gameController != null){
                        gameController.triggerHeartBeat(this.username);
                        timer.reschedule(this.timerDelay);
                    }
                } catch (RemoteException e) {
                    System.out.println("ERRORE HEARTBEAT!");
                    this.close();
                }
            },
            0, 2, TimeUnit.SECONDS);
    }

    private void checkGameIsSet() throws NoAvailableGameException {
        if( gameController == null) throw  new NoAvailableGameException("The client hasn't joined a game!");
    }

    public void sendMessage(String content){
        try {
            gameController.postBroadCastMessage(this.username,content);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (InvalidPlayerException ignored) {
        }
    }

    public void sendMessage(String content, String recipient){
        try {
            gameController.postDirectMessage(this.username,content,recipient);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (InvalidPlayerException ignored) {
            /* to decide, if the written recipient does not exists, we could just ignore the chat message*/
        } catch (SenderEqualsRecipientException e) {
            view.postNotification(Notifications.CHAT_SENDER_EQUALS_RECIPIENT);
        }
    }
}
