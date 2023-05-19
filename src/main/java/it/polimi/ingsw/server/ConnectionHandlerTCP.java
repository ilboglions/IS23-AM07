package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.*;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static it.polimi.ingsw.server.ServerMain.logger;


public class ConnectionHandlerTCP implements Runnable, BoardSubscriber, BookshelfSubscriber, ChatSubscriber, PlayerSubscriber, GameSubscriber {

    private final Socket socket;
    private boolean closeConnectionFlag;
    private final LobbyController lobbyController;
    private String username;
    private RemoteGameController gameController;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    private final int timerDelay = 15000;

    private final Queue<NetMessage> lastReceivedMessages;

    private final ExecutorService parseExecutors = Executors.newCachedThreadPool();
    public ConnectionHandlerTCP(Socket socket, LobbyController lobbyController) {
        this.socket = socket;
        this.lobbyController = lobbyController;
        this.username = "";
        this.gameController = null;
        this.lastReceivedMessages = new ArrayDeque<>();
        closeConnectionFlag = false;
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void run() {
        //ReschedulableTimer timer = new ReschedulableTimer();
        //NetMessage inputMessage;

        //timer.schedule(this::handleCrash, timerDelay);
        this.startParserAgent();
        this.messagesHopper();

        /*while (!closeConnectionFlag) {
            try {
                inputMessage = (NetMessage)inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            logger.info("MESSAGE RECEIVED");
            timer.reschedule(timerDelay); //15s
            NetMessage finalInputMessage = inputMessage;

            parseExecutors.execute(() -> {

                    NetMessage outputMessage;
                    try {
                        outputMessage = messageParser(finalInputMessage);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    if (closeConnectionFlag)
                        return;

                    this.sendUpdate(outputMessage);
            });
        }
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);}

         */

    }

    private void messagesHopper()  {
        parseExecutors.execute( () -> {
            ReschedulableTimer timer = new ReschedulableTimer();
            timer.schedule(this::handleCrash, timerDelay);
            while(true) {
                synchronized (lastReceivedMessages) {
                    try {
                        NetMessage incomingMessage = (NetMessage) inputStream.readObject();
                        lastReceivedMessages.add(incomingMessage);
                        timer.reschedule(timerDelay);
                        lastReceivedMessages.notifyAll();
                        lastReceivedMessages.wait(1);
                    } catch (IOException | ClassNotFoundException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }


    private void startParserAgent(){
        parseExecutors.execute( () -> {
            NetMessage response;
            while (true)
                synchronized (lastReceivedMessages) {
                    while (lastReceivedMessages.isEmpty()) {
                        try {
                            lastReceivedMessages.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        response = this.messageParser(lastReceivedMessages.poll());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    sendUpdate(response);
                }
        });
    }


    private NetMessage messageParser(NetMessage inputMessage) throws RemoteException {
        NetMessage outputMessage;
        logger.info(inputMessage.getMessageType().toString());
        switch (inputMessage.getMessageType()) {
            case JOIN_LOBBY -> {
                outputMessage =  this.parse((JoinLobbyMessage) inputMessage);
            }
            case CREATE_GAME -> {
                outputMessage = this.parse((CreateGameMessage) inputMessage);
            }
            case JOIN_GAME -> {
                outputMessage = this.parse((JoinGameMessage) inputMessage);
            }
            case TILES_SELECTION -> {
                outputMessage = this.parse((TileSelectionMessage) inputMessage);
            }
            case MOVE_TILES -> {
                outputMessage = this.parse((MoveTilesMessage) inputMessage);
            }
            case POST_MESSAGE -> {
                outputMessage = this.parse((PostMessage) inputMessage);
            }
            case GAME_RECEIVED_MESSAGE -> {
                outputMessage = this.parse((GameReceivedMessage) inputMessage);
            }
            case STILL_ACTIVE -> {
                outputMessage = new StillActiveMessage();
            }
            default -> {
                closeConnectionFlag = true;
                outputMessage = new CloseConnectionMessage();
            }
        }

        return outputMessage;
    }

    private NetMessage parse(JoinLobbyMessage joinLobbyMessage) throws RemoteException {
        boolean result;
        String errorType = "";
        String desc;
        boolean hasPlayerJoined;
        if( gameController != null){
            errorType = "Already playing";
            result = false;
            hasPlayerJoined = false;
            desc = "You are already playing a game!";
        } else {
            if (username != null && !username.isEmpty()) {
                try {
                    lobbyController.handleCrashedPlayer(username);
                } catch (PlayerNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                gameController = lobbyController.enterInLobby(joinLobbyMessage.getUsername());
                this.username = joinLobbyMessage.getUsername();
                if(gameController != null) {
                    this.subscribeToAllListeners();
                    hasPlayerJoined = true;
                    desc ="Reconnecting to game...";
                } else {
                    hasPlayerJoined = false;
                    desc="Welcome in lobby!";
                }
                result = true;
            } catch (NicknameAlreadyUsedException e) {
                errorType = "NicknameAlreadyUsedException";
                result = false;
                hasPlayerJoined = false;
                desc = e.getMessage();
            } catch (InvalidPlayerException e) {
                errorType = "InvalidPlayerException";
                result = false;
                hasPlayerJoined = false;
                desc = e.getMessage();
            }
        }
        return new  LoginReturnMessage(result,errorType, desc, hasPlayerJoined);

    }
    private NetMessage parse(CreateGameMessage createGameMessage) throws RemoteException {
        boolean result;
        String errorType = "";
        String desc = "";
        try {
            gameController = lobbyController.createGame(username, createGameMessage.getPlayerNumber());
            result = true;
            logger.info("Game CREATED Successfully");
            this.subscribeToAllListeners();
        } catch (InvalidPlayerException e) {
            result = false;
            errorType = "InvalidPlayer";
            desc = e.getMessage();
        } catch (PlayersNumberOutOfRange e) {
            result = false;
            errorType = "PlayersNumberOutOfRange";
            desc = e.getMessage();
        }

        return new ConfirmGameMessage(result, errorType, desc, false);
    }
    private NetMessage parse(JoinGameMessage inputMessage) throws RemoteException {
        boolean result;
        String errorType = "";
        String desc = "";
        try {
            gameController = lobbyController.addPlayerToGame(username);
            this.subscribeToAllListeners();
            result = true;
        } catch (NicknameAlreadyUsedException e) {
            result = false;
            errorType = "NicknameAlreadyUsed";
            desc = e.getMessage();
        } catch (NoAvailableGameException e) {
            result = false;
            errorType = "NoAvailableGameException";
            desc = e.getMessage();
        } catch (InvalidPlayerException e) {
            result = false;
            errorType = "InvalidPlayerException";
            desc = e.getMessage();
        }

        return new ConfirmGameMessage(false, errorType, desc, result);
    }
    private NetMessage parse(TileSelectionMessage tileSelectionMessage) throws RemoteException {
            boolean result;
            String errorType = "";
            String desc = "";
        try {
            result = gameController.checkValidRetrieve(username, tileSelectionMessage.getTiles());
            if(!result) {
                errorType = "invalid selection!";
                desc = "can't select that!";
                logger.info(desc);
            }

        } catch (PlayerNotInTurnException e) {
            result = false;
            errorType = "PlayerNotInTurnException";
            desc = e.getMessage();
        } catch (GameNotStartedException e) {
            result = false;
            errorType = "GameNotStartedException";
            desc = e.getMessage();
        } catch (GameEndedException e) {
            result = false;
            errorType = "GameEndedException";
            desc = e.getMessage();
        } catch (EmptySlotException e) {
            result = false;
            errorType = "EmptySlotException";
            desc = e.getMessage();
        }
        return new ConfirmSelectionMessage(result, errorType, desc);
    }
    private NetMessage parse(PostMessage postMessage) throws RemoteException {
        boolean result;
        String errorType = "";
        String desc = "";
        if (!postMessage.getRecipient().equals("")) {
            try {
                result = true;
                gameController.postDirectMessage(username, postMessage.getRecipient(), postMessage.getContent());
            } catch (InvalidPlayerException e) {
                result = false;
                errorType = "InvalidPlayerException";
                desc = e.getMessage();
            } catch (SenderEqualsRecipientException e) {
                result = false;
                errorType = "SenderEqualsRecipientException";
                desc = e.getMessage();
            }
        } else {
            try {
                gameController.postBroadCastMessage(username, postMessage.getContent());
                result = true;
            } catch (InvalidPlayerException e) {
                result = false;
                errorType = "InvalidPlayerException";
                desc = e.getMessage();
            }
        }
        logger.info(postMessage.getContent() + " " + postMessage.getRecipient());
        return new ConfirmChatMessage(result, errorType, desc);
    }

    private NetMessage parse(MoveTilesMessage moveTilesMessage) throws RemoteException {
        boolean result;
        String errorType = "";
        String desc;

        try {
            gameController.moveTiles(username, moveTilesMessage.getTiles(), moveTilesMessage.getColumn());
            result = true;
            desc = "Moved tiles successfully!";
        } catch (GameNotStartedException e) {
            result = false;
            errorType = "GameNotStartedException";
            desc = e.getMessage();
        } catch (GameEndedException e) {
            result = false;
            errorType = "GameEndedException";
            desc = e.getMessage();
        } catch (EmptySlotException e) {
            result = false;
            errorType = "EmptySlotException";
            desc = e.getMessage();
        } catch (NotEnoughSpaceException e) {
            result = false;
            errorType = "NotEnoughSpaceException";
            desc = e.getMessage();
        } catch (InvalidCoordinatesException e) {
            result = false;
            errorType = "InvalidCoordinatesException";
            desc = e.getMessage();
        } catch (PlayerNotInTurnException e) {
            result = false;
            errorType = "PlayerNotInTurnException";
            desc = e.getMessage();
        }
        return new ConfirmMoveMessage(result,errorType,desc);
    }

    private NetMessage parse(GameReceivedMessage inputMessage) {
        if(!inputMessage.getErrorOccurred()) {
            try {
                gameController.triggerAllListeners(this.username);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        return new StillActiveMessage();
    }

    private void subscribeToAllListeners(){

        if(this.gameController == null) throw new RuntimeException("no game controlled");
        try {
            gameController.subscribeToListener((PlayerSubscriber) this);
            gameController.subscribeToListener((ChatSubscriber) this);
            gameController.subscribeToListener((BoardSubscriber) this);
            gameController.subscribeToListener((BookshelfSubscriber) this);
            gameController.subscribeToListener((GameSubscriber) this);
        } catch (RemoteException e){
            throw new RuntimeException(e);
        }
    }

    public void handleCrash() {
        logger.info("Connection lost");
        if(username == null || username.isEmpty()) return;
        if (gameController == null) {
            try {
                lobbyController.handleCrashedPlayer(this.username);
                logger.info("Handling crash in the lobby... player hasn't join any game!");
            } catch (PlayerNotFoundException | RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                gameController.handleCrashedPlayer(this.username);
                logger.info("Handling crash in the game!");
            } catch (RemoteException | PlayerNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateBoardStatus(Map<Coordinates, ItemTile> tilesInBoard) {
        BoardUpdateMessage update = new BoardUpdateMessage(tilesInBoard);
        this.sendUpdate(update);
    }

    @Override
    public void updateBookshelfStatus(String player, ArrayList<ItemTile> tilesInserted, int colChosen, Map<Coordinates, ItemTile> currentTilesMap) {
        BookshelfUpdateMessage update = new BookshelfUpdateMessage(tilesInserted, colChosen, currentTilesMap, player);
        this.sendUpdate(update);
    }


    @Override
    public void receiveMessage(String from, String msg, Boolean privateMessage) {
        logger.info("SENDING MESSAGE TO " + this.username);
        NotifyNewChatMessage update = new NotifyNewChatMessage(from, msg, privateMessage);
        this.sendUpdate(update);
    }

    @Override
    public String getSubscriberUsername() {
        return username;
    }

    @Override
    public void updatePoints(String player, int overallPoints, int addedPoints) {
        PointsUpdateMessage update = new PointsUpdateMessage(player, overallPoints, addedPoints);
        this.sendUpdate(update);
    }
    @Override
    public void updateTokens(String player, ArrayList<ScoringToken> tokenPoints) {
        TokenUpdateMessage update = new TokenUpdateMessage(tokenPoints, player);
        this.sendUpdate(update);
    }

    @Override
    public void updatePersonalGoalCard(String player, RemotePersonalGoalCard remotePersonal) {
        PersonalGoalCardUpdateMessage update = new PersonalGoalCardUpdateMessage(player, remotePersonal);
        this.sendUpdate(update);
    }

    @Override
    public void notifyPlayerJoined(String username) {
        NewPlayerInGame update = new NewPlayerInGame(username);
        this.sendUpdate(update);

        try {
            gameController.subscribeToListener((PlayerSubscriber) this);
            gameController.subscribeToListener((BookshelfSubscriber) this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
}

    @Override
    public void notifyWinningPlayer(String username, int points, Map<String,Integer> scoreboard) {
        NotifyWinnerPlayerMessage update = new NotifyWinnerPlayerMessage(username, points, scoreboard);
        this.sendUpdate(update);
    }

    @Override
    public void notifyCommonGoalCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) {
        CommonGoalCardsUpdateMessage update = new CommonGoalCardsUpdateMessage(commonGoalCards);
        this.sendUpdate(update);
    }

    @Override
    public void notifyPlayerInTurn(String username) {
        NotifyPlayerInTurnMessage update = new NotifyPlayerInTurnMessage(username, this.username.equals(username));
        this.sendUpdate(update);
    }

    @Override
    public void notifyPlayerCrashed(String userCrashed){
        NotifyPlayerCrashedMessage update = new NotifyPlayerCrashedMessage(userCrashed);
        this.sendUpdate(update);
    }

    @Override
    public void notifyTurnOrder(ArrayList<String> playerOrder){
        NotifyTurnOrder update = new NotifyTurnOrder(playerOrder);
        this.sendUpdate(update);
    }

    /**
     *
     * @param update
     */
    private void sendUpdate(NetMessage update) {
        synchronized (outputStream) {
            try {
                logger.info("SENDING..." + update.getMessageType()+ " to "+this.username );
                outputStream.writeObject(update);
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}



