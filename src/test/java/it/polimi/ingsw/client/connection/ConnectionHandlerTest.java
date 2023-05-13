package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.view.CLI.CliView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConnectionHandlerTest {

    @Test
    @DisplayName("tests the connection factory")
    public void TestFactory(){
        ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
        assertTrue( factory.createConnection(ConnectionType.TCP, new CliView(ConnectionType.TCP)) instanceof ClientSocket );
        assertTrue( factory.createConnection(ConnectionType.RMI, new CliView(ConnectionType.RMI)) instanceof ClientRMI );
    }

}
