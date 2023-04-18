package it.polimi.ingsw.client.connection;

public class ConnectionHandlerFactory {
    public ConnectionHandler createConnection(ConnectionType type){
        if( type.equals(ConnectionType.RMI)) {
            return new ClientRMI();
        }
        return new ClientSocket("127.0.0.1",4567);
    }
}
