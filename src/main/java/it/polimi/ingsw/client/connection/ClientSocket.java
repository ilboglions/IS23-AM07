package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.model.coordinate.Coordinates;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClientSocket implements ConnectionHandler{

    private String ip;
    private int port;
    private Socket connection;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;
    public ClientSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    @Override
    public void init() {

        try {
            connection = new Socket(ip, port);
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            inputStream = new ObjectInputStream(connection.getInputStream());
            // here we should create some task that listens the server!

        } catch ( UnknownHostException e) {
            System.out.println("Server this address is not reachable");
            System.exit(0);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    }

    @Override
    public void close() throws IOException {
        outputStream.close();
        inputStream.close();
        connection.close();
    }

    @Override
    public void JoinLobby(String username) {
        //here the socket should send the message JoinLobbyMessage asking the server to join the lobby
    }

    @Override
    public void CreateGame(int nPlayers) {
        //here the socket will send a CreateGameMessage message
    }

    @Override
    public void JoinGame() {

    }

    @Override
    public void checkValidRetrieve(ArrayList<Coordinates> tiles) {

    }

    @Override
    public void moveTiles(ArrayList<Coordinates> tiles, int column) {

    }
}
