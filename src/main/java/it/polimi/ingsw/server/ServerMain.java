package it.polimi.ingsw.server;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.model.lobby.Lobby;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerMain {
    private int port;
    private String hostName;

    private final LobbyController lobbyController;
    public ServerMain(int port, String hostName) throws RemoteException {
        this.port = port;
        this.hostName = hostName;
        this.lobbyController = new LobbyController(new Lobby());
    }
    public void startServer() {

        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        System.out.println("Server started!");


        /* RMI INITIALIZATION */
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(5678);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        try {
            registry.bind("lobby_controller", lobbyController);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // Porta non disponibile
            return;
        }
        System.out.println("Server ready");




        /* SOCKET TCP */
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executor.submit(new ServerClientHandlerTCP(socket));
            } catch(IOException e) {
                break; // Entrerei qui se serverSocket venisse chiuso
            }
        }
        executor.shutdown();
    }
    public static void main(String[] args) {
        String hostName;
        int portNumber;
        try {
            if (args.length == 2) {
                hostName = args[0];
                portNumber = Integer.parseInt(args[1]);
            } else {
                Gson gson = new Gson();
                JsonObject job = gson.fromJson(new FileReader("src/main/java/it/polimi/ingsw/server/confFiles/HostAndPort.json"), JsonObject.class);
                portNumber = gson.fromJson(job.get("portNumber"), Integer.class);
                hostName = gson.fromJson(job.get("hostName"), String.class);
            }

            ServerMain echoServer = new ServerMain(portNumber, hostName);
            echoServer.startServer();
        }catch (FileNotFoundException e){
            System.err.println(e.getMessage());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }


}