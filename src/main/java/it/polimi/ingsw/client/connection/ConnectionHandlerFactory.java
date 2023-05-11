package it.polimi.ingsw.client.connection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.view.CliView;
import it.polimi.ingsw.client.view.ViewInterface;

import javax.swing.text.View;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;

public class ConnectionHandlerFactory {
    public ConnectionHandler createConnection(ConnectionType type, ViewInterface view){
        if( type.equals(ConnectionType.RMI)) {
            try {
                return new ClientRMI(view);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        }

        Gson gson = new Gson();
        JsonObject job = gson.fromJson(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("HostAndPort.json"))), JsonObject.class);
        int portNumber = gson.fromJson(job.get("portNumber"), Integer.class);
        String hostName = gson.fromJson(job.get("hostName"), String.class);

        return new ClientSocket(hostName,portNumber,view);
    }
}
