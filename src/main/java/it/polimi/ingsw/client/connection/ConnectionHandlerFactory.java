package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.view.CliView;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ConnectionHandlerFactory {
    public ConnectionHandler createConnection(ConnectionType type){
        if( type.equals(ConnectionType.RMI)) {
            try {
                return new ClientRMI(new CliView());
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
        return new ClientSocket("127.0.0.1",4567, new CliView());
    }
}
