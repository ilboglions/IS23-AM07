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
import java.util.ArrayList;
import java.util.Map;
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

    private final ExecutorService parseExecutors = Executors.newCachedThreadPool();
    public ConnectionHandlerTCP(Socket socket, LobbyController lobbyController) {
        this.socket = socket;
        this.lobbyController = lobbyController;
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


        ReschedulableTimer timer = new ReschedulableTimer();
        NetMessage inputMessage;
        timer.schedule(this::handleCrash, 15000);
        while (!closeConnectionFlag) {
            try {
                inputMessage = (NetMessage)inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            logger.info("MESSAGE RECEIVED");
            timer.reschedule(15000); //15s
            NetMessage finalInputMessage = inputMessage;
            parseExecutors.submit(() -> {
                synchronized (outputStream) {
                    NetMessage outputMessage;
                    try {
                        outputMessage = messageParser(finalInputMessage);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    if (closeConnectionFlag)
                        return;
                    try {
                        outputStream.writeObject(outputMessage);
                        outputStream.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            });
        }
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);}

    }

    private NetMessage messageParser(NetMessage inputMessage) throws RemoteException {
        NetMessage outputMessage;
        boolean result;
        String errorType = "";
        String desc = "";
        boolean hasPlayerJoined;
        logger.info(inputMessage.getMessageType().toString());
        switch (inputMessage.getMessageType()) {
            case JOIN_LOBBY -> {

                JoinLobbyMessage joinLobbyMessage = (JoinLobbyMessage) inputMessage;
                if(username != null && !username.isEmpty()){
                    try {
                        lobbyController.handleCrashedPlayer(username);
                    } catch (PlayerNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                this.username = joinLobbyMessage.getUsername();
                try {
                    gameController = lobbyController.enterInLobby(joinLobbyMessage.getUsername());
                     if(gameController != null){
                         hasPlayerJoined = true;
                     } else {
                         hasPlayerJoined = false;
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

                outputMessage = new LoginReturnMessage(result,errorType, desc, hasPlayerJoined);
            }
            case CREATE_GAME -> {
                CreateGameMessage createGameMessage = (CreateGameMessage) inputMessage;
                try {
                    gameController = lobbyController.createGame(username, createGameMessage.getPlayerNumber());

                    result = true;
                    logger.info("Game CREATED Successfully");
                    errorType = "";
                    desc = "";
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
                logger.info(String.valueOf(result));
                outputMessage = new ConfirmGameMessage(result, errorType, desc);
            }
            case JOIN_GAME -> {
                try {
                    gameController = lobbyController.addPlayerToGame(username);
                    result = true;
                    errorType = "";
                    this.subscribeToAllListeners();
                    desc = "";
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
                outputMessage = new ConfirmGameMessage(result, errorType, desc);
            }
            case TILES_SELECTION -> {
                TileSelectionMessage tileSelectionMessage = (TileSelectionMessage) inputMessage;
                try {
                    result = gameController.checkValidRetrieve(username, tileSelectionMessage.getTiles());
                    errorType = "";
                    desc = "";
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
                    desc = "you cannot select those slots";
                }
                outputMessage = new ConfirmSelectionMessage(result, errorType, desc);
            }
            case MOVE_TILES -> {
                MoveTilesMessage moveTilesMessage = (MoveTilesMessage) inputMessage;
                try {
                    gameController.moveTiles(username, moveTilesMessage.getTiles(), moveTilesMessage.getColumn());
                    result = true;
                    errorType="";
                    desc = "";
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
                outputMessage = new ConfirmMoveMessage(result,errorType,desc);
            }
            case POST_MESSAGE -> {
                PostMessage postMessage = (PostMessage) inputMessage;
                if (!postMessage.getRecipient().equals("")) {
                    try {
                        result = true;
                        gameController.postDirectMessage(username, postMessage.getRecipient(), postMessage.getContent());
                        desc = "";
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
                outputMessage = new ConfirmChatMessage(result, errorType, desc);
            }
            case STILL_ACTIVE -> {
                logger.info("Heartbeat received");
                outputMessage = new StillActiveMessage();
            }
            default -> {
                closeConnectionFlag = true;
                outputMessage = new CloseConnectionMessage();
            }
        }

        return outputMessage;
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
        if (gameController == null) {
            try {
                lobbyController.handleCrashedPlayer(this.username);
            } catch (PlayerNotFoundException | RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                gameController.handleCrashedPlayer(this.username);
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
    public void updateBookshelfStatus(String player, ArrayList<ItemTile> tilesInserted, int colChosen) {
        BookshelfUpdateMessage update = new BookshelfUpdateMessage(tilesInserted, colChosen);
        this.sendUpdate(update);
    }

    @Override
    public void updateBookshelfComplete(Map<Coordinates, ItemTile> currentTilesMap) {
        BookshelfFullUpdateMessage update = new BookshelfFullUpdateMessage(currentTilesMap);
        this.sendUpdate(update);
    }


    @Override
    public void receiveMessage(String from, String msg) {
        logger.info("SENDING MESSAGE TO "+this.username);
        NotifyNewChatMessage update = new NotifyNewChatMessage(from, msg);
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
        NotifyWinnerPlayerMessage update = new NotifyWinnerPlayerMessage(username, points, scoreboard );
        this.sendUpdate(update);
    }

    @Override
    public void notifyCommonGoalCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) {
        CommonGoalCardsUpdateMessage update = new CommonGoalCardsUpdateMessage(commonGoalCards);
        this.sendUpdate(update);
    }

    /**
     *
     * @param update
     */
    private void sendUpdate(NetMessage update) {
        synchronized (outputStream) {
            try {
                outputStream.writeObject(update);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}



