package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.server.model.coordinate.Coordinates;

import java.io.IOException;
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
    private NetMessage requestMessage;
    private NetMessage responseMessage;
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



    @Override
    public void close() throws IOException {
        outputStream.close();
        inputStream.close();
        connection.close();
    }

    @Override
    public void JoinLobby(String username) {
        requestMessage = new JoinLobbyMessage(username);
        try {
            outputStream.writeObject(requestMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            responseMessage = (NetMessage) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void CreateGame(int nPlayers) {
        requestMessage = new CreateGameMessage(nPlayers);
        try {
            outputStream.writeObject(requestMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            responseMessage = (NetMessage) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void JoinGame() {
        requestMessage = new JoinGameMessage();
        try {
            outputStream.writeObject(requestMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            responseMessage = (NetMessage) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void checkValidRetrieve(ArrayList<Coordinates> tiles) {
        requestMessage = new TileSelectionMessage(tiles);
        try {
            outputStream.writeObject(requestMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            responseMessage = (NetMessage) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void moveTiles(ArrayList<Coordinates> tiles, int column) {
        requestMessage = new MoveTilesMessage(tiles,column);
        try {
            outputStream.writeObject(requestMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            responseMessage = (NetMessage) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
