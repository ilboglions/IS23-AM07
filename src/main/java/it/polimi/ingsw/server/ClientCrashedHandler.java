package it.polimi.ingsw.server;

import java.io.IOException;

public class ClientCrashedHandler  implements Runnable {

    private final ConnectionHandlerTCP tcpHandler;
    public ClientCrashedHandler(ConnectionHandlerTCP tcpHandler) {
        this.tcpHandler = tcpHandler;
    }

    public void run(){
        tcpHandler.handleCrash();
    }
}
