package it.polimi.ingsw.client.connection;

public class ConnectionHandlerFactory {
    public ConnectionHandler createConnection(ConnectionType type){
        if( type.equals(ConnectionType.RMI)) {
            return new ConnectionRMI();
        }
        return new ConnectionTCP();
    }
}
