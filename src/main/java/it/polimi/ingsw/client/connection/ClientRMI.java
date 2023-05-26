package it.polimi.ingsw.client.connection;


import it.polimi.ingsw.client.localModel.Game;
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

        while(!connected){
            try{
                this.registry= LocateRegistry.getRegistry();
                String remoteObjectName = "lobby_controller";
                tempLobbyController =  (RemoteLobbyController) registry.lookup(remoteObjectName);
                connected = true;
            }catch(ConnectException e){
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        this.lobbyController = tempLobbyController;
        this.view = view;
        this.sendHeartBeat();
    }

    @Override
    public void close() {
        System.out.println("timer expired");
    }

    @Override
    public void JoinLobby(String username) throws RemoteException {
        this.username = username;
        try {
            gameController = lobbyController.enterInLobby(username);
            if( gameController == null){
                view.drawScene(SceneType.LOBBY);
                view.postNotification("joined in lobby!","Select what to do");
                lobbyController.triggerHeartBeat(this.username);
            } else {
                view.drawScene(SceneType.GAME);
                view.postNotification("welcome back!","Reconnected to your previous game");
                gameController.triggerHeartBeat(this.username);
                this.gameModel = new Game(this.view,this.username);
                this.subscribeListeners();
                gameController.triggerAllListeners(this.username);
            }
        } catch (NicknameAlreadyUsedException e) {
            view.postNotification("Nickname already used!", "Choose another nickname and retry");
        } catch (InvalidPlayerException e) {
            view.postNotification("Invalid player!","You can't use this username!");
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
            view.postNotification("Game created successfully","");
        } catch (InvalidPlayerException e) {
            view.postNotification("Invalid player!","You can't join a game without loggin in!");
        } catch (PlayersNumberOutOfRange e) {
            view.postNotification("The number of player is out of range!","Create the game with less players");
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
            view.postNotification("Game joined successfully","");
        } catch (NicknameAlreadyUsedException e) {
            throw new RuntimeException(e);
        } catch (NoAvailableGameException e) {
            view.postNotification("No game is available!",e.getMessage());
        } catch (InvalidPlayerException e) {
            view.postNotification("Invalid player!","You can't join a game without loggin in!");
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
                view.postNotification("Your Selection has been accepted!!","Choose the column to fit the selection!");
            else
                view.postNotification("Your selection is invalid", "");
        } catch (EmptySlotException e) {
            view.postNotification("The slot selected is empty!",e.getMessage());
        } catch (GameNotStartedException e) {
            view.postNotification("The game has not started yet!",e.getMessage());
        } catch (GameEndedException e) {
            view.postNotification("The game has already ended!",e.getMessage());
        } catch (PlayerNotInTurnException e) {
            view.postNotification("You're not in turn!",e.getMessage());
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
            view.postNotification("Move done!", "");
        } catch (GameNotStartedException e) {
            view.postNotification("The game is not started yet!",e.getMessage());
        } catch (GameEndedException e) {
            view.postNotification("The game is ended!",e.getMessage());
        } catch (EmptySlotException e) {
            view.postNotification("The slot selected is empty!",e.getMessage());
        } catch (NotEnoughSpaceException e) {
            view.postNotification("No space left",e.getMessage());
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }  catch (PlayerNotInTurnException e) {
            view.postNotification("That's not your turn!",e.getMessage());
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
        } catch (InvalidPlayerException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String content, String recipient){
        try {
            gameController.postDirectMessage(this.username,content,recipient);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (InvalidPlayerException e) {
            throw new RuntimeException(e);
        } catch (SenderEqualsRecipientException e) {
            view.postNotification("Sender equals recipient!","you can not send a message to yourself!");
        }
    }
}
