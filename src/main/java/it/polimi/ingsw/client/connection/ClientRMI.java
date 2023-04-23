package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.remoteInterfaces.RemoteGameController;
import it.polimi.ingsw.remoteInterfaces.RemoteLobbyController;
import it.polimi.ingsw.server.model.exceptions.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

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
