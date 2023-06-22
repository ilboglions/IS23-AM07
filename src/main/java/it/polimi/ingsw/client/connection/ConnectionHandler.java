package it.polimi.ingsw.client.connection;
// da spostare le coordinates in un altro package (?)
import it.polimi.ingsw.server.model.coordinate.Coordinates;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ConnectionHandler {
    /**
     * the close() method permit to close the connection between client and server
     * @throws IOException
     */
    void close() throws IOException;

    /**
     * the client can perform the joinLobby method to join the lobby with a username
     * @param username the username used for joining the lobby
     * @throws RemoteException if a connection problem occurs
     */
    void JoinLobby(String username) throws RemoteException;

    /**
     * the createGame method perform the game creation in the server
     * @param nPlayers the number of player for the game
     * @throws RemoteException if a connection problem occurs
     */
    void CreateGame(int nPlayers) throws  RemoteException;

    /**
     * make it possible for the player to join an open, casual game
     * @throws RemoteException if a connection problem occurs
     */
    void JoinGame() throws RemoteException;

    /**
     * first method to be invoked during a turn, make it possible to evaluate a retrieve of tiles from the living room board
     * @param tiles the tiles to be selected
     * @throws RemoteException if a connection problem occurs
     */
    void checkValidRetrieve(ArrayList<Coordinates> tiles) throws RemoteException;

    /**
     * second method to invoke during a turn,
     * it make it possible to moves the selected tiles (ordered as the client wish)
     * in a column of the bookshelf
     * @param tiles the selected tiles
     * @param column the column of the bookshelf that has been chosen
     * @throws RemoteException if a connection problem occurs
     */
    void moveTiles( ArrayList<Coordinates> tiles, int column) throws RemoteException;

    /**
     * used to send heartbeat to the server, ensures the client is still active
     * @throws RemoteException if a connection problem occurs
     */
    void sendHeartBeat() throws RemoteException;

    /**
     * used to send a broadcast chat message
     * @param content content of the message
     */
    void sendMessage(String content);

    /**
     * used to send a private chat message
     * @param recipient recipient of the message
     * @param content content of the message
     */
    void sendMessage( String recipient, String content);

    String getServerIP();

    int getServerPort();

}
