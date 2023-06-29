package it.polimi.ingsw.client.connection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.view.ViewInterface;

import java.io.InputStreamReader;
import java.util.Objects;

/**
 * This is the class that is in charge of creating the desired connection type, either TCP or RMI
 */
public class ConnectionHandlerFactory {
    /**
     * Creates a ConnectionHandler of the type specified (RMI/TCP)
     * @param type type of the connection
     * @param view view interface used
     * @return a ConnectionHandler of the desired type
     */
    public ConnectionHandler createConnection(ConnectionType type, ViewInterface view){
        Gson gson = new Gson();
        JsonObject job = gson.fromJson(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("HostAndPort.json"))), JsonObject.class);


        if( type.equals(ConnectionType.RMI)) {
                int rmiPortNumber = gson.fromJson(job.get("rmiPortNumber"), Integer.class);
                String rmiHostName = gson.fromJson(job.get("rmiHostName"), String.class);
                return new ClientRMI(view, rmiHostName,rmiPortNumber);
        }

        int portNumber = gson.fromJson(job.get("portNumber"), Integer.class);
        String hostName = gson.fromJson(job.get("hostName"), String.class);

        return new ClientSocket(hostName,portNumber,view);
    }

    /**
     * Creates a ConnectionHandler of the type specified (RMI/TCP)
     * @param connectionType type of the connection
     * @param view view interface used
     * @param hostName the ip of the server
     * @param portNumber the port of the server
     * @return a ConnectionHandler of the desired type
     */
    public ConnectionHandler createConnection(ConnectionType connectionType, ViewInterface view, String hostName, int portNumber) {
        if(connectionType.equals(ConnectionType.RMI)){
            return new ClientRMI(view, hostName, portNumber);
        }
        return new ClientSocket(hostName,portNumber,view);
    }
}
