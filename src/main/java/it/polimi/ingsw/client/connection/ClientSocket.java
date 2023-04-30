package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.server.ReschedulableTimer;
import it.polimi.ingsw.server.model.coordinate.Coordinates;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientSocket implements ConnectionHandler{

    private final String ip;
    private final int port;
    private final Socket connection;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private NetMessage requestMessage;
    private NetMessage responseMessage;
    private final Queue<NetMessage> lastReceivedMessages;
    private final ExecutorService threadManager;
    private final ReschedulableTimer timer;
    private final ScheduledExecutorService heartBeatManager;

    public ClientSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.lastReceivedMessages = new ArrayDeque<>();
        this.timer = new ReschedulableTimer();
        this.threadManager = Executors.newCachedThreadPool();
        this.heartBeatManager = Executors.newSingleThreadScheduledExecutor();
        /* game port */
        try {
            connection = new Socket(ip, port);
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            inputStream = new ObjectInputStream(connection.getInputStream());
            // here we should create some task that listens the server!
        } catch ( UnknownHostException e) {
            System.out.println("Server this address is not reachable");
            throw new RuntimeException(e);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        timer.schedule(this::handleCrash, 15000);
        this.messagesHopper();
        this.sendHeartBeat();

    }

    private void handleCrash() {
        System.out.println("Connection Down!\n");
        this.close();
    }


    @Override
    public void close(){
        synchronized (outputStream) {
            try {
                outputStream.writeObject(new CloseConnectionMessage());
                heartBeatManager.shutdownNow();
                threadManager.shutdownNow();
                outputStream.close();
                inputStream.close();
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void JoinLobby(String username) {
        requestMessage = new JoinLobbyMessage(username);
        try {
            synchronized (outputStream) {
                outputStream.writeObject(requestMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        responseMessage = getMessageFromBuffer(MessageType.LOGIN_RETURN);
        System.out.println(responseMessage.getMessageType());
    }

    @Override
    public void CreateGame(int nPlayers) {
        requestMessage = new CreateGameMessage(nPlayers);
        try {
            synchronized (outputStream) {
                outputStream.writeObject(requestMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        responseMessage = getMessageFromBuffer(MessageType.CONFIRM_GAME);
    }

    @Override
    public void JoinGame() {
        requestMessage = new JoinGameMessage();
        try {
            synchronized (outputStream) {
                outputStream.writeObject(requestMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        responseMessage = getMessageFromBuffer(MessageType.CONFIRM_GAME);
    }

    @Override
    public void checkValidRetrieve(ArrayList<Coordinates> tiles) {
        requestMessage = new TileSelectionMessage(tiles);
        try {
            synchronized (outputStream) {
                outputStream.writeObject(requestMessage);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        responseMessage = getMessageFromBuffer(MessageType.CONFIRM_SELECTION);
    }

    @Override
    public void moveTiles(ArrayList<Coordinates> tiles, int column) {
        requestMessage = new MoveTilesMessage(tiles,column);
        try {
            synchronized (outputStream) {
                outputStream.writeObject(requestMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        responseMessage = getMessageFromBuffer(MessageType.CONFIRM_MOVE);
    }

    public void sendMessage(String content){
        PostMessage message = new PostMessage(content);
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        responseMessage = getMessageFromBuffer(MessageType.CONFIRM_CHAT);

    }

    public void sendMessage(String content, String recipient){
        PostMessage message = new PostMessage(content, recipient);
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        responseMessage = getMessageFromBuffer(MessageType.CONFIRM_CHAT);

    }

    @Override
    public void sendHeartBeat()  {
        heartBeatManager.scheduleAtFixedRate(
            () -> {
                requestMessage = new StillActiveMessage();
                synchronized (outputStream) {
                    try {
                        outputStream.writeObject(requestMessage);
                        responseMessage = getMessageFromBuffer(MessageType.STILL_ACTIVE);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            },
            0, 5, TimeUnit.SECONDS);
    }

    private void messagesHopper()  {
        threadManager.submit( () -> {
            while(true) {
                synchronized (lastReceivedMessages) {
                    try {
                        NetMessage incomingMessage = (NetMessage) inputStream.readObject();
                        lastReceivedMessages.add(incomingMessage);
                        timer.reschedule(15000);
                        lastReceivedMessages.notifyAll();
                        lastReceivedMessages.wait(1);
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
    }
    private NetMessage getMessageFromBuffer(MessageType type){
        NetMessage result;
        synchronized (lastReceivedMessages) {
            while (lastReceivedMessages.stream().noneMatch(message -> message.getMessageType().equals(type))) {
                try {
                    lastReceivedMessages.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            result =  lastReceivedMessages.stream().filter(message -> message.getMessageType().equals(type)).findFirst().get();
            lastReceivedMessages.remove(result);
        }
        return result;
    }
}

