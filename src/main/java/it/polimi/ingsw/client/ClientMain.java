package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.connection.ConnectionType;
import it.polimi.ingsw.client.view.CliView;
import it.polimi.ingsw.client.view.ViewInterface;

import java.io.InputStreamReader;
import java.util.Objects;

public class ClientMain {
    public static void main(String[] args) {
        ConnectionType c;
        ViewInterface view;
        if (args.length == 1) {

            c = args[0].equals("TCP") ? ConnectionType.TCP : ConnectionType.RMI;

            //ViewInterface cliView = args[1].equals("CLI") ?  new CliView(c);

            view = new CliView(c);

        } else {
             view = new CliView(ConnectionType.TCP);
        }



    }
}
