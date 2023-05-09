package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.view.CliView;
import it.polimi.ingsw.client.view.ViewInterface;

import javax.swing.text.View;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ConnectionHandlerFactory {
    public ConnectionHandler createConnection(ConnectionType type, ViewInterface view){
        if( type.equals(ConnectionType.RMI)) {
            try {
                return new ClientRMI(view);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
        return new ClientSocket("127.0.0.1",4567,view);
    }
}
