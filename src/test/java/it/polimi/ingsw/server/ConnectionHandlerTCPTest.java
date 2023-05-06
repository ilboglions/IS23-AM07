package it.polimi.ingsw.server;

import it.polimi.ingsw.client.connection.ClientSocket;
import it.polimi.ingsw.client.connection.ConnectionType;
import it.polimi.ingsw.client.view.CliView;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
public class ConnectionHandlerTCPTest {
    private static final String ip = "127.0.0.1";
    private static final int port = 4567;

    @Test
    @DisplayName("Parser TCP Communications Test")
    void ParserTCPTester () throws IOException, ClassNotFoundException {
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
        Socket connection;

        try {
            connection = new Socket(ip, port);
            inputStream = new ObjectInputStream(connection.getInputStream());
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            // here we should create some task that listens the server!
        } catch ( UnknownHostException e) {
            System.out.println("Server this address is not reachable");
            throw new RuntimeException(e);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        NetMessage request = new JoinLobbyMessage("PlayerName");
        outputStream.writeObject(request);
        LoginReturnMessage response = (LoginReturnMessage) inputStream.readObject();
        assertTrue(response.getConfirmLogin());
        assertEquals("", response.getDetails());

        outputStream.writeObject(request);
        response = (LoginReturnMessage) inputStream.readObject();
        assertFalse(response.getConfirmLogin());

        request = new CreateGameMessage(9);
        outputStream.writeObject(request);
        ConfirmGameMessage responseg = (ConfirmGameMessage) inputStream.readObject();
        assertFalse(responseg.getConfirmGameCreation());
        assertEquals("PlayersNumberOutOfRange", responseg.getErrorType());
        assertEquals("", responseg.getDetails());

        request = new CreateGameMessage(4);
        outputStream.writeObject(request);
        responseg = (ConfirmGameMessage) inputStream.readObject();
        assertTrue(responseg.getConfirmGameCreation());
        assertEquals("", responseg.getErrorType());
        assertEquals("", responseg.getDetails());
    }

    @Test
    void ClientSimulatorTest(){
        ClientSocket client = new ClientSocket(ip, port, new CliView(ConnectionType.TCP));
        client.JoinLobby("Username");
        client.CreateGame(4);
        try {
            TimeUnit.SECONDS.sleep(40);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        client.sendMessage("MessageSendingTest");
        client.sendMessage("RecipientMessageTest", "OtherPlayer");
        client.close();
    }

    @Test
    void UpdateMessagesTest() throws InvalidCoordinatesException, IOException, ClassNotFoundException {
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
        Socket connection;

        try {
            connection = new Socket(ip, port);
            inputStream = new ObjectInputStream(connection.getInputStream());
            outputStream = new ObjectOutputStream(connection.getOutputStream());
        } catch ( UnknownHostException e) {
            System.out.println("Server this address is not reachable");
            throw new RuntimeException(e);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<Coordinates, ItemTile> tmpPattern = new HashMap<>();
        tmpPattern.put(new Coordinates(0,3), ItemTile.GAME);
        tmpPattern.put(new Coordinates(3,2), ItemTile.BOOK);
        tmpPattern.put(new Coordinates(2,1), ItemTile.PLANT);
        tmpPattern.put(new Coordinates(4,4), ItemTile.TROPHY);
        tmpPattern.put(new Coordinates(0,1), ItemTile.BOOK);
        tmpPattern.put(new Coordinates(1,1), ItemTile.CAT);
        NetMessage boardUpdate = new BoardUpdateMessage(tmpPattern);
        outputStream.writeObject(boardUpdate);
        BoardUpdateMessage boardUpdateMessage = (BoardUpdateMessage) inputStream.readObject();
        assertEquals(MessageType.BOARD_UPDATE, boardUpdateMessage.getMessageType());
        assertEquals(tmpPattern, boardUpdateMessage.getTilesInBoard());
    }
}
