package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.remoteControllers.RemoteGameController;
import it.polimi.ingsw.remoteControllers.RemoteLobbyController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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
    public void JoinLobby(String username) {
        this.username = username;
        this.lobbyController.enterInLobby(username);
    }

    @Override
    public void CreateGame(int nPlayers) throws NotBoundException, RemoteException {
        String remoteGameName;
        if( !(remoteGameName = this.lobbyController.createGame(username, nPlayers)).isEmpty() ){
            this.gameController =  (RemoteGameController) registry.lookup(remoteGameName);
        }

    }

    @Override
    public void JoinGame() throws RemoteException, NotBoundException {
        String remoteGameName;
        if( !(remoteGameName = this.lobbyController.addPlayerToGame(this.username)).isEmpty() ){
            this.gameController =  (RemoteGameController) registry.lookup(remoteGameName);
        }

    }

    @Override
    public void checkValidRetrieve(ArrayList<Coordinates> tiles) {

        this.checkGameIsSetted();

        //here the view will be notified that the action has been executed correctly
        if(gameController.checkValidRetrieve(this.username,tiles)) return;

    }

    @Override
    public void moveTiles(ArrayList<Coordinates> tiles, int column) {

        if(gameController.moveTiles(this.username,tiles, column)) return;


    }

    private void checkGameIsSetted(){
        if( gameController == null) throw  new RuntimeException("the client has joined no game!");
    }
}
