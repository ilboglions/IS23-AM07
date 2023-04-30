package it.polimi.ingsw.client.connection;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ConnectionHandlerFactory {
    public ConnectionHandler createConnection(ConnectionType type){
        if( type.equals(ConnectionType.RMI)) {
            try {
                return new ClientRMI();
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }
        return new ClientSocket("127.0.0.1",4567);
    }
}
