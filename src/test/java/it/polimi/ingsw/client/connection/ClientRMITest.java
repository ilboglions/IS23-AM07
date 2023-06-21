package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.view.CLI.CliView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientRMITest {
    ConnectionHandler client;
    ClientRMITest() throws NotBoundException, RemoteException {
        client = new ClientRMI(new CliView(ConnectionType.RMI),"127.0.0.1",1099);
    }
    @Test
    @DisplayName("Test the connection to the lobby")
    void testJoin() throws RemoteException {
        client.JoinLobby("piero");
        client.JoinGame();

        client.CreateGame(5);
        client.CreateGame(2);


    }
}
