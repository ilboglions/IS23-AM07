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

    private final int port;
    private final String hostName;

    private final LobbyController lobbyController;

    /**
     * Constructor to create an istance of the ServerMain
     * @param hostName the ip of the server
     * @param port the port used by the server for TCP
     * @param rmiPortNumber the port used by the server for RMI
     * @throws RemoteException RMI exception
     */
    public ServerMain(String hostName,int port, int rmiPortNumber) throws RemoteException {
        this.port = port;
        this.hostName = hostName;
        this.rmiPortNumber = rmiPortNumber;
        this.lobbyController = new LobbyController(new Lobby());
    }

    /**
     * Method that starts both the RMI and the TCP server. When it is called it starts to listen and accept all the new connection requested
     */
    public void startServer() {
        ExecutorService executor = Executors.newCachedThreadPool();

        ServerSocket serverSocket;
        logger.info("Server started!");


        /* RMI INITIALIZATION */
        Registry registry;
        try {
            System.setProperty("java.rmi.server.hostname",this.hostName);
            registry = LocateRegistry.createRegistry(rmiPortNumber);
            registry.bind("lobby_controller", lobbyController);
        } catch (RemoteException | AlreadyBoundException e) {
            executor.shutdownNow();
            logger.info("Server Failed to Launch RMI Server");
            System.exit(0);
            return;
        }
        /* server socket initialization */
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // Porta non disponibile
            System.exit(0);
            return;
        }

        logger.info("Server ready! host: "+hostName+ " port (TCP): " + port + " port(RMI): "+rmiPortNumber);

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
            if (args.length == 3) {
                hostName = args[0];
                if(UtilityFunctions.isNumeric(args[1]) && UtilityFunctions.isNumeric(args[2])) {
                    portNumber = Integer.parseInt(args[1]);
                    rmiPortNumber = Integer.parseInt(args[2]);
                }else
                    System.exit(-1);
            } else {
                Gson gson = new Gson();
                JsonObject job = gson.fromJson(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("HostAndPort.json"))), JsonObject.class);
                portNumber = gson.fromJson(job.get("portNumber"), Integer.class);
                hostName = gson.fromJson(job.get("hostName"), String.class);
                rmiPortNumber = gson.fromJson(job.get("rmiPortNumber"), Integer.class);
            }

            ServerMain echoServer = new ServerMain(hostName, portNumber, rmiPortNumber);
            echoServer.startServer();
        } catch (RemoteException e) {
            logger.info("Server couldn't start! ");
            System.exit(0);
        }

    }



}