package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    private int port;
    private String hostName;
    public ServerMain(int port, String hostName) {
        this.port = port;
        this.hostName = hostName;
    }
    public void startServer() {

        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        System.out.println("Server started!");

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // Porta non disponibile
            return;
        }
        System.out.println("Server ready");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executor.submit(new EchoServerClientHandler(socket));
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
                JsonObject job = gson.fromJson(new FileReader("confFiles/HostAndPort.json"), JsonObject.class);
                portNumber = gson.fromJson(job.get("portNumber"), Integer.class);
                hostName = gson.fromJson(job.get("hostName"), String.class);
            }

            ServerMain echoServer = new ServerMain(portNumber, hostName);
            echoServer.startServer();
        }catch (FileNotFoundException e){
            System.err.println(e.getMessage());
        }

    }


}