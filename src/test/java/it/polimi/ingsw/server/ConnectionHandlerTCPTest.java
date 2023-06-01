/*package it.polimi.ingsw.server;

import it.polimi.ingsw.client.connection.ClientSocket;
import it.polimi.ingsw.client.connection.ConnectionType;
import it.polimi.ingsw.client.view.CLI.CliView;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
public class ConnectionHandlerTCPTest {
    private static final String ip = "127.0.0.1";
    private static final int port = 4567;

    @Test
    @DisplayName("Parser TCP Communications Test")
    void ParserTCPTester () throws IOException, ClassNotFoundException {
        ObjectOutputStream outputStream, outputStream1;
        ObjectInputStream inputStream, inputStream1;
        Socket connection, connection1;

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

        try {
            connection1 = new Socket(ip, port);
            inputStream1 = new ObjectInputStream(connection1.getInputStream());
            outputStream1 = new ObjectOutputStream(connection1.getOutputStream());
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

        outputStream1.writeObject(request);
        response = (LoginReturnMessage) inputStream1.readObject();
        assertFalse(response.getConfirmLogin());

        request = new CreateGameMessage(9);
        outputStream.writeObject(request);
        ConfirmGameMessage responseg = (ConfirmGameMessage) inputStream.readObject();
        assertFalse(responseg.getConfirmGameCreation());
        assertEquals("PlayersNumberOutOfRange", responseg.getErrorType());

        request = new CreateGameMessage(4);
        outputStream.writeObject(request);
        responseg = (ConfirmGameMessage) inputStream.readObject();
        assertTrue(responseg.getConfirmGameCreation());
        assertEquals("", responseg.getErrorType());
        assertEquals("", responseg.getDetails());
    }

    @Test
    @DisplayName("Test of a game turn")
    void GameTest () throws IOException, ClassNotFoundException, InvalidCoordinatesException {
        ObjectOutputStream outputStream1, outputStream2, outputStream3, tmpOutputStream;
        ObjectInputStream inputStream1, inputStream2, inputStream3, tmpInputStream;
        Socket firstPlayer, secondPlayer, thirdPlayer;
        NetMessage tmp;

        try {
            firstPlayer = new Socket(ip, port);
            inputStream1 = new ObjectInputStream(firstPlayer.getInputStream());
            outputStream1 = new ObjectOutputStream(firstPlayer.getOutputStream());
        } catch ( UnknownHostException e) {
            System.out.println("Server this address is not reachable");
            throw new RuntimeException(e);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            secondPlayer = new Socket(ip, port);
            inputStream2 = new ObjectInputStream(secondPlayer.getInputStream());
            outputStream2 = new ObjectOutputStream(secondPlayer.getOutputStream());
        } catch ( UnknownHostException e) {
            System.out.println("Server this address is not reachable");
            throw new RuntimeException(e);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            thirdPlayer = new Socket(ip, port);
            inputStream3 = new ObjectInputStream(thirdPlayer.getInputStream());
            outputStream3 = new ObjectOutputStream(thirdPlayer.getOutputStream());
        } catch ( UnknownHostException e) {
            System.out.println("Server this address is not reachable");
            throw new RuntimeException(e);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        NetMessage request = new JoinLobbyMessage("Player1");
        outputStream1.writeObject(request);
        LoginReturnMessage response1 = (LoginReturnMessage) inputStream1.readObject();
        assertTrue(response1.getConfirmLogin());

        request = new JoinLobbyMessage("Player2");
        outputStream2.writeObject(request);
        LoginReturnMessage response2 = (LoginReturnMessage) inputStream2.readObject();
        assertTrue(response2.getConfirmLogin());

        request = new JoinLobbyMessage("Player3");
        outputStream3.writeObject(request);
        LoginReturnMessage response3 = (LoginReturnMessage) inputStream3.readObject();
        assertTrue(response3.getConfirmLogin());

        request = new CreateGameMessage(3);
        outputStream1.writeObject(request);
        ConfirmGameMessage confirmGameMessage = (ConfirmGameMessage) inputStream1.readObject();
        assertTrue(confirmGameMessage.getConfirmGameCreation());

        request = new JoinGameMessage();
        outputStream2.writeObject(request);
        confirmGameMessage =  (ConfirmGameMessage) inputStream2.readObject();
        assertTrue(confirmGameMessage.getConfirmGameCreation());

        request = new JoinGameMessage();
        outputStream3.writeObject(request);
        confirmGameMessage =  (ConfirmGameMessage) inputStream3.readObject();
        assertTrue(confirmGameMessage.getConfirmGameCreation());

        do{
            tmp = (NetMessage) inputStream1.readObject();
        } while (!tmp.getMessageType().equals(MessageType.NOTIFY_PLAYER_IN_TURN));

        NotifyPlayerInTurnMessage playerTurn = (NotifyPlayerInTurnMessage)tmp;
        switch(playerTurn.getUserInTurn()){
            case "Player1" -> {
                tmpInputStream = inputStream1;
                tmpOutputStream = outputStream1;
            }
            case "Player2" -> {
                tmpInputStream = inputStream2;
                tmpOutputStream = outputStream2;
            }
            default -> {
                tmpInputStream = inputStream3;
                tmpOutputStream = outputStream3;
            }
        }

        ArrayList<Coordinates> tiles = new ArrayList<>();
        tiles.add(new Coordinates(1,3));
        tiles.add(new Coordinates(1,4));
        request = new TileSelectionMessage(tiles);
        tmpOutputStream.writeObject(request);
        do{
            tmp = (NetMessage) tmpInputStream.readObject();
        } while (!tmp.getMessageType().equals(MessageType.CONFIRM_SELECTION));
        ConfirmSelectionMessage confirmSelectionMessage = (ConfirmSelectionMessage) tmp;
        assertTrue(confirmSelectionMessage.getConfirmSelection());

        request = new MoveTilesMessage(tiles, 0);
        tmpOutputStream.writeObject(request);
        do{
            tmp = (NetMessage) tmpInputStream.readObject();
        } while (!tmp.getMessageType().equals(MessageType.CONFIRM_MOVE));
        ConfirmMoveMessage confirmMoveMessage = (ConfirmMoveMessage) tmp;
        assertTrue(confirmMoveMessage.getConfirmSelection());
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

}
*/