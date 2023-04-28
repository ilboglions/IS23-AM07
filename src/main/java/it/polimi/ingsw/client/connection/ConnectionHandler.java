package it.polimi.ingsw.client.connection;
// da spostare le coordinates in un altro package (?)
import it.polimi.ingsw.server.model.coordinate.Coordinates;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ConnectionHandler {

    void init() throws RemoteException, NotBoundException;
    void close() throws IOException;
    void JoinLobby(String username) throws RemoteException;
    void CreateGame(int nPlayers) throws NotBoundException, RemoteException;
    void JoinGame() throws RemoteException, NotBoundException;
    void checkValidRetrieve(ArrayList<Coordinates> tiles) throws RemoteException;
    void moveTiles( ArrayList<Coordinates> tiles, int column) throws RemoteException;
    void sendHeartBeat() throws RemoteException;

}
