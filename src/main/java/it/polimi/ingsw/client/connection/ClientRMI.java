package it.polimi.ingsw.client.connection;


import it.polimi.ingsw.server.ReschedulableTimer;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.remoteInterfaces.RemoteGameController;
import it.polimi.ingsw.remoteInterfaces.RemoteLobbyController;
import it.polimi.ingsw.server.model.exceptions.*;

import java.io.IOException;
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

    public ClientRMI() throws RemoteException, NotBoundException {
        this.timer = new ReschedulableTimer();
        this.heartBeatManager = Executors.newSingleThreadScheduledExecutor();
        this.registry= LocateRegistry.getRegistry();
        String remoteObjectName = "lobby_controller";
        this.lobbyController =  (RemoteLobbyController) registry.lookup(remoteObjectName);
    }

    @Override
    public void close() {

    }

    @Override
    public void JoinLobby(String username) throws RemoteException {
        this.username = username;
        try {
            this.lobbyController.enterInLobby(username);
        } catch (NicknameAlreadyUsedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void CreateGame(int nPlayers) throws RemoteException {

        try {
            this.gameController = this.lobbyController.createGame(username, nPlayers);
        } catch (InvalidPlayerException e) {
            throw new RuntimeException(e);
        } catch (BrokenInternalGameConfigurations e) {
            throw new RuntimeException(e);
        } catch (NotEnoughCardsException e) {
            throw new RuntimeException(e);
        } catch (PlayersNumberOutOfRange e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void JoinGame() throws RemoteException {

        try {
            this.gameController = this.lobbyController.addPlayerToGame(username);
        } catch (NicknameAlreadyUsedException e) {
            throw new RuntimeException(e);
        } catch (NoAvailableGameException e) {
            throw new RuntimeException(e);
        } catch (InvalidPlayerException e) {
            throw new RuntimeException(e);
        } catch (PlayersNumberOutOfRange e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void checkValidRetrieve(ArrayList<Coordinates> tiles) throws RemoteException {

        try {
            this.checkGameIsSet();
        } catch (NoAvailableGameException e) {
            throw new RuntimeException(e);
        }

        //here the view will be notified that the action has been executed correctly
        if(gameController.checkValidRetrieve(this.username,tiles)) return;

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
        } catch (GameNotStartedException e) {
            throw new RuntimeException(e);
        } catch (GameEndedException e) {
            throw new RuntimeException(e);
        } catch (EmptySlotException e) {
            throw new RuntimeException(e);
        } catch (NotEnoughSpaceException e) {
            throw new RuntimeException(e);
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        } catch (InvalidPlayerException e) {
            throw new RuntimeException(e);
        } catch (TokenAlreadyGivenException e) {
            throw new RuntimeException(e);
        } catch (PlayerNotInTurnException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void sendHeartBeat() {
        heartBeatManager.scheduleAtFixedRate(
                () -> {
                    try {
                        if(gameController == null && lobbyController != null){
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
                        throw new RuntimeException(e);
                    }
                },
                0, 5, TimeUnit.SECONDS);
    }

    private void checkGameIsSet() throws NoAvailableGameException {
        if( gameController == null) throw  new NoAvailableGameException("the client has joined no game!");
    }
}
