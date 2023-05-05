package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.view.ViewInterface;
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
    private final Queue<NetMessage> lastReceivedMessages;
    private final ExecutorService threadManager;
    private final ReschedulableTimer timer;
    private final ScheduledExecutorService heartBeatManager;

    private String username;
    private final ViewInterface view;

    private final long timerDelay = 15000;

    public ClientSocket(String ip, int port, ViewInterface view) {
        this.ip = ip;
        this.port = port;
        this.lastReceivedMessages = new ArrayDeque<>();
        this.timer = new ReschedulableTimer();
        this.threadManager = Executors.newCachedThreadPool();
        this.heartBeatManager = Executors.newSingleThreadScheduledExecutor();
        this.view = view;

        boolean connected = false;
        Socket tempConnection = null;

        while(!connected){
            /* game port */
            try {
                tempConnection = new Socket(ip, port);
                connected = true;
                // here we should create some task that listens the server!
            } catch ( UnknownHostException e) {
                view.postNotification("Connection error", "Server this address is not reachable, trying again soon...");
                try {
                    this.wait(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        this.connection = tempConnection;
        try {
            this.outputStream = new ObjectOutputStream(connection.getOutputStream());
            this.inputStream = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        timer.schedule(this::handleCrash, this.timerDelay);
        this.messagesHopper();
        this.sendHeartBeat();

    }

    private void handleCrash() {
        view.postNotification("Connection error", "connection with server no longer available!");

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
        this.username = username;
        JoinLobbyMessage requestMessage = new JoinLobbyMessage(username);
        try {
            synchronized (outputStream) {
                outputStream.writeObject(requestMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        NetMessage responseMessage = getMessageFromBuffer(MessageType.LOGIN_RETURN);
        System.out.println(responseMessage.getMessageType());
    }

    @Override
    public void CreateGame(int nPlayers) {
        CreateGameMessage requestMessage = new CreateGameMessage(nPlayers);
        try {
            synchronized (outputStream) {
                outputStream.writeObject(requestMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        NetMessage responseMessage = getMessageFromBuffer(MessageType.CONFIRM_GAME);
    }

    @Override
    public void JoinGame() {
        JoinGameMessage requestMessage = new JoinGameMessage();
        try {
            synchronized (outputStream) {
                outputStream.writeObject(requestMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        NetMessage responseMessage = getMessageFromBuffer(MessageType.CONFIRM_GAME);
        this.parse(responseMessage);
        //Here the player will receive also the CommonGoalCards, PersonalGoalCard and all the updates of the other players
    }

    @Override
    public void checkValidRetrieve(ArrayList<Coordinates> tiles) {
        TileSelectionMessage requestMessage = new TileSelectionMessage(tiles);
        try {
            synchronized (outputStream) {
                outputStream.writeObject(requestMessage);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        NetMessage responseMessage = getMessageFromBuffer(MessageType.CONFIRM_SELECTION);
    }

    @Override
    public void moveTiles(ArrayList<Coordinates> tiles, int column) {
        MoveTilesMessage requestMessage = new MoveTilesMessage(tiles, column);
        try {
            synchronized (outputStream) {
                outputStream.writeObject(requestMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        NetMessage responseMessage = getMessageFromBuffer(MessageType.CONFIRM_MOVE);
    }

    public void sendMessage(String content){
        PostMessage message = new PostMessage(content);
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        NetMessage responseMessage = getMessageFromBuffer(MessageType.CONFIRM_CHAT);

    }

    public void sendMessage(String content, String recipient){
        PostMessage message = new PostMessage(content, recipient);
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        NetMessage responseMessage = getMessageFromBuffer(MessageType.CONFIRM_CHAT);

    }

    @Override
    public void sendHeartBeat()  {
        heartBeatManager.scheduleAtFixedRate(
            () -> {
                StillActiveMessage requestMessage = new StillActiveMessage();
                synchronized (outputStream) {
                    try {
                        outputStream.writeObject(requestMessage);
                        NetMessage responseMessage = getMessageFromBuffer(MessageType.STILL_ACTIVE);
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
                        timer.reschedule(this.timerDelay);
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

    private void parse(NetMessage responseMessage){
        switch (responseMessage.getMessageType()){
            case LOGIN_RETURN -> {
                LoginReturnMessage message = (LoginReturnMessage) responseMessage;
                    if(message.getConfirmLogin()){
                      view.postNotification("Logged in as "+this.username+"!","choose either to create or join a game!");
                    } else if (message.getConfirmRejoined()) {
                        view.postNotification("Welcome back "+this.username+"!", "reconnecting to your game...");
                    } else {
                        /* an error occurred */
                        view.postNotification(message.getErrorType(),message.getDetails());
                    }

            } /* end LOGIN_RETURN */
            case CONFIRM_GAME -> {
                ConfirmGameMessage message = (ConfirmGameMessage) responseMessage;
                if(message.getConfirmGameCreation()){
                    view.postNotification("Joining game...","sugo");
                } else{
                    view.postNotification(message.getErrorType(),message.getDetails());
                }
            } /* end CONFIRM_GAME */
            case CONFIRM_SELECTION -> {
                ConfirmSelectionMessage message = (ConfirmSelectionMessage) responseMessage;
                if(message.getConfirmSelection()){
                    view.postNotification("Your Selection has been accepted!","choose the column to fit the selection!");
                } else{
                    view.postNotification(message.getErrorType(),message.getDetails());
                }
            }
            case CONFIRM_MOVE -> {
                ConfirmMoveMessage message = (ConfirmMoveMessage) responseMessage;
                if(message.getConfirmSelection()){
                    view.postNotification("Move done!","grande bro");

                    /* qui immagino
                    * bookshelf.updateBookshelf( arrayList delle tiles, colonna scelta)
                    * view.drawBookShelf(bookshelf.getStructure); (questo potrebbe stare benissimo anche in questa sottospecie di minimodel)
                    * quindi un mini-model lato client che mantiene uno stato (ecco dove stanno i listener!)
                    **/
                }
            }

            case NOTIFY_NEW_CHAT -> {}

            case NOTIFY_WINNING_PLAYER -> {}

            case POINTS_UPDATE -> {}

            case STILL_ACTIVE -> {}

            case TOKEN_UPDATE -> {}

            case CONFIRM_CHAT -> {}

            case BOARD_UPDATE -> {}

            case BOOKSHELF_UPDATE -> {}

            case BOOKSHELF_FULL_UPDATE -> {}

            case PERSONAL_CARD_UPDATE -> {}

            case USER_GAME_CARDS -> {}
        }
    }
}



