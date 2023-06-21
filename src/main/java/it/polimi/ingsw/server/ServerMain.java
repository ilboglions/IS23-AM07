package it.polimi.ingsw.server;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.model.lobby.Lobby;
import it.polimi.ingsw.server.model.utilities.UtilityFunctions;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


/**
 * This is the main class of the Server, it launches both RMI and TCP Services to accept clients' requests.
 */
public class ServerMain {
    public final static Logger logger = Logger.getLogger(ServerMain.class.getName());
    private final int rmiPortNumber;
    private final String rmiHostName;
    private final int port;
    private final String hostName;

    private final LobbyController lobbyController;
    public ServerMain(int port, String hostName, int rmiPortNumber, String rmiHostName) throws RemoteException {
        this.port = port;
        this.hostName = hostName;
        this.rmiHostName = rmiHostName;
        this.rmiPortNumber = rmiPortNumber;
        this.lobbyController = new LobbyController(new Lobby());
    }
    public void startServer() {
        ExecutorService executor = Executors.newCachedThreadPool();

        ServerSocket serverSocket;
        logger.info("Server started!");


        /* RMI INITIALIZATION */
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        try {
            registry.bind("lobby_controller", lobbyController);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }

        /* server socket initialization */
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // Porta non disponibile
            return;
        }

        logger.info("Server ready on port " + port);

        /* SOCKET TCP */
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                logger.info("Client connected");
                executor.execute(new ConnectionHandlerTCP(socket, lobbyController));
            } catch(IOException e) {
                break; // Entrerei qui se serverSocket venisse chiuso
            }
        }
        executor.shutdown();
    }
    public static void main(String[] args) {
        String hostName;
        int portNumber = 0;
        int rmiPortNumber = 0;
        String rmiHostName;
        try {
            if (args.length >= 5) {
                hostName = args[1];
                rmiHostName = args[3];
                if(UtilityFunctions.isNumeric(args[2]) && UtilityFunctions.isNumeric(args[4])) {
                    portNumber = Integer.parseInt(args[2]);
                    rmiPortNumber = Integer.parseInt(args[4]);
                }else
                    System.exit(-1);
            } else {
                Gson gson = new Gson();
                JsonObject job = gson.fromJson(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("HostAndPort.json"))), JsonObject.class);
                portNumber = gson.fromJson(job.get("portNumber"), Integer.class);
                hostName = gson.fromJson(job.get("hostName"), String.class);

                 rmiPortNumber = gson.fromJson(job.get("rmiPortNumber"), Integer.class);
                 rmiHostName = gson.fromJson(job.get("rmiHostName"), String.class);
            }

            ServerMain echoServer = new ServerMain(portNumber, hostName, rmiPortNumber, rmiHostName);
            echoServer.startServer();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }


}