package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.server.ReschedulableTimer;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
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
        this.startParserAgent();

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
    }

    public void sendMessage(String content){
        PostMessage message = new PostMessage(content);
        try {
            synchronized (outputStream){
                outputStream.writeObject(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String content, String recipient){
        PostMessage message = new PostMessage(content, recipient);
        try {
            synchronized (outputStream){
                outputStream.writeObject(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendHeartBeat()  {
        heartBeatManager.scheduleAtFixedRate(
            () -> {
                StillActiveMessage requestMessage = new StillActiveMessage();
                synchronized (outputStream) {
                    try {
                        outputStream.writeObject(requestMessage);
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

    private void startParserAgent(){
        threadManager.submit( () -> {
            while(this.connection.isConnected()){
                synchronized (lastReceivedMessages){
                    while(lastReceivedMessages.isEmpty()){
                        try {
                            lastReceivedMessages.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    this.parse(lastReceivedMessages.poll());
                }
            }
        });
    }
 /*   private NetMessage getMessageFromBuffer(MessageType type){
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
    } */

    private void parse(NetMessage responseMessage){
        if(responseMessage == null) return;
        switch (responseMessage.getMessageType()){
            case LOGIN_RETURN -> {
                this.parse((LoginReturnMessage) responseMessage);
            }
            case CONFIRM_GAME -> {
                this.parse((ConfirmGameMessage) responseMessage);
            }
            case CONFIRM_SELECTION -> {
                this.parse((ConfirmSelectionMessage) responseMessage);
            }
            case CONFIRM_MOVE -> {
                this.parse((ConfirmMoveMessage) responseMessage);
            }
            case NOTIFY_NEW_CHAT -> {
                this.parse((NotifyNewChatMessage) responseMessage);
            }
            case BOARD_UPDATE -> {
                this.parse((BoardUpdateMessage) responseMessage);
            }
            case BOOKSHELF_UPDATE -> {
                this.parse((BookshelfUpdateMessage) responseMessage);
            }
            case PERSONAL_CARD_UPDATE -> {
                this.parse((PersonalGoalCardUpdateMessage) responseMessage);
            }
            case COMMON_CARDS_UPDATE -> {
                this.parse((CommonGoalCardsUpdateMessage) responseMessage);
            }
            case NOTIFY_PLAYER_CRASHED -> {
                this.parse((NotifyPlayerCrashedMessage) responseMessage);
            }
            case NOTIFY_WINNING_PLAYER -> {
                this.parse((NotifyWinnerPlayerMessage) responseMessage);
            }
            case POINTS_UPDATE -> {
                this.parse((PointsUpdateMessage) responseMessage);
            }
            case CONFIRM_CHAT -> {
                this.parse((ConfirmChatMessage) responseMessage);
            }

            case STILL_ACTIVE -> {}

            case TOKEN_UPDATE -> {}

            case NEW_PLAYER -> {}

            case NOTIFY_PLAYER_IN_TURN -> {}
        }
    }

    private void parse(LoginReturnMessage message){
        if(message.getConfirmLogin()){
            if (message.getConfirmRejoined()) {
                view.postNotification("Welcome back " + this.username + "!", "reconnecting to your game...");
            }else {
                view.postNotification("Logged in as " + this.username + "!", "choose either to create or join a game!");
            }
        } else {
            /* an error occurred */
            view.postNotification(message.getErrorType(),message.getDetails());
        }
    }

    private void parse(ConfirmGameMessage message){
        if(message.getConfirmGameCreation()){
            view.postNotification("Game created successfully","");
        } else{
            view.postNotification(message.getErrorType(),message.getDetails());
        }
    }

    private void parse(ConfirmSelectionMessage message){
        if(message.getConfirmSelection()){
            view.postNotification("Your Selection has been accepted!","choose the column to fit the selection!");
        } else{
            view.postNotification(message.getErrorType(),message.getDetails());
        }
    }

    private void parse(ConfirmMoveMessage message){
        if(message.getConfirmSelection()){
            view.postNotification("Move done!","");

            /* qui immagino
             * bookshelf.updateBookshelf( arrayList delle tiles, colonna scelta)
             * view.drawBookShelf(bookshelf.getStructure); (questo potrebbe stare benissimo anche in questa sottospecie di minimodel)
             * quindi un mini-model lato client che mantiene uno stato (ecco dove stanno i listener!)
             **/
        }
    }

    private void parse(NotifyNewChatMessage message){
        //TODO: Add message to chat
    }

    private void parse(BoardUpdateMessage message) {
        try {
            view.drawLivingRoom(message.getTilesInBoard());
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }

    private void parse(BookshelfUpdateMessage message){
        try {
            //Decide how to handle orders, pass the arraylist of players ordered? pass in the message the player number?
            view.drawBookShelf(message.getCurrentMap(), message.getUsername(), 0);
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }

    private void parse(PersonalGoalCardUpdateMessage message){
        try {
            view.drawPersonalCard(message.getCard().getCardPattern(), message.getCard().getPointsReference());
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void parse(CommonGoalCardsUpdateMessage message){
        try {
            view.drawCommonCards(message.getCommonGoalCards());
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void parse(NotifyPlayerCrashedMessage message){
        //TODO: Add server message to chat
    }

    private void parse(NotifyWinnerPlayerMessage message){
        //TODO: Add server message to chat
    }

    private  void parse(PointsUpdateMessage message){
        //TODO: update map of points and players
    }

    private void parse(ConfirmChatMessage message){
        if(!message.getResult())
            //TODO: add error to the chat
            return;
    }
}



