package it.polimi.ingsw.server;

public class ClientCrashedHandler  implements Runnable {

    private final ParserTCP tcpHandler;
    public ClientCrashedHandler(ParserTCP tcpHandler) {
        this.tcpHandler = tcpHandler;
    }

    public void run(){
        tcpHandler.handleCrash();
    }
}
