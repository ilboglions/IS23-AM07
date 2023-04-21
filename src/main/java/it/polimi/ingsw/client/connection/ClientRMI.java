package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.remoteControllers.RemoteGameController;
import it.polimi.ingsw.remoteControllers.RemoteLobbyController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Optional;

public class ClientRMI implements ConnectionHandler{

    Registry registry;
    private RemoteLobbyController lobbyController;
    private String username;
    private RemoteGameController gameController;
    @Override
    public void init() throws RemoteException, NotBoundException {
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
        this.lobbyController.enterInLobby(username);
    }

    @Override
    public void CreateGame(int nPlayers) throws NotBoundException, RemoteException {
        Optional<RemoteGameController> rgc;

        rgc = this.lobbyController.createGame(username, nPlayers);
        rgc.ifPresent(remoteGameController -> this.gameController = remoteGameController);

    }

    @Override
    public void JoinGame() throws RemoteException, NotBoundException {
        Optional<RemoteGameController> rgc;

        rgc = this.lobbyController.addPlayerToGame(username);
        rgc.ifPresent(remoteGameController -> this.gameController = remoteGameController);

    }

    @Override
    public void checkValidRetrieve(ArrayList<Coordinates> tiles) throws RemoteException {

        this.checkGameIsSet();

        //here the view will be notified that the action has been executed correctly
        if(gameController.checkValidRetrieve(this.username,tiles)) return;

    }

    @Override
    public void moveTiles(ArrayList<Coordinates> tiles, int column) throws RemoteException {

        if(gameController.moveTiles(this.username,tiles, column)) return;

    }

    private void checkGameIsSet(){
        if( gameController == null) throw  new RuntimeException("the client has joined no game!");
    }
}
