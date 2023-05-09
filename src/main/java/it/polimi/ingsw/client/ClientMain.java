package it.polimi.ingsw.client;

import it.polimi.ingsw.client.connection.ConnectionType;
import it.polimi.ingsw.client.view.CliView;
import it.polimi.ingsw.client.view.ViewInterface;

public class ClientMain {
    public static void main(String[] args) {
        ViewInterface cliView = new CliView(ConnectionType.RMI);

    }
}
