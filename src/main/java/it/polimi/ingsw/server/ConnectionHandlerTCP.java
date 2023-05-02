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
import java.util.Optional;

import static it.polimi.ingsw.server.ServerMain.logger;


public class ConnectionHandlerTCP implements Runnable, BoardSubscriber, BookshelfSubscriber, ChatSubscriber, PlayerSubscriber, GameSubscriber {

    private final Socket socket;
    private boolean closeConnectionFlag;
    private final LobbyController lobbyController;
    private String username;
    private RemoteGameController gameController;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
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
        try {

            ReschedulableTimer timer = new ReschedulableTimer();
            NetMessage inputMessage;
            NetMessage outputMessage;
            timer.schedule(this::handleCrash, 15000);
            while (true) {
                inputMessage = (NetMessage)inputStream.readObject();
                logger.info("MESSAGE RECEIVED");
                timer.reschedule(15000); //15s
                outputMessage = messageParser(inputMessage);
                    if(closeConnectionFlag)
                        break;
                    synchronized (outputStream) {
                        outputStream.writeObject(outputMessage);
                    }
                }
                outputStream.flush();
            } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
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
        logger.info(inputMessage.getMessageType().toString());
        switch (inputMessage.getMessageType()) {
            case JOIN_LOBBY -> {
                JoinLobbyMessage joinLobbyMessage = (JoinLobbyMessage) inputMessage;
                this.username = joinLobbyMessage.getUsername();
                result = lobbyController.enterInLobby(joinLobbyMessage.getUsername());
                outputMessage = new LoginReturnMessage(result, "");
            }
            case CREATE_GAME -> {
                CreateGameMessage createGameMessage = (CreateGameMessage) inputMessage;
                try {
                    gameController = lobbyController.createGame(username, createGameMessage.getPlayerNumber());
                    result = true;
                    logger.info("Game CREATED Successfully");
                    errorType = "";
                    gameController.subscribeToListener((PlayerSubscriber)this);
                    gameController.subscribeToListener((ChatSubscriber) this);
                    gameController.subscribeToListener((BoardSubscriber) this);
                    gameController.subscribeToListener((BookshelfSubscriber) this);
                    gameController.subscribeToListener((GameSubscriber) this);
                } catch (InvalidPlayerException e) {
                    result = false;
                    errorType = "InvalidPlayer";
                } catch (BrokenInternalGameConfigurations e) {
                    result = false;
                    errorType = "BrokenInternalGameConfiguration";
                } catch (PlayersNumberOutOfRange e) {
                    result = false;
                    errorType = "PlayersNumberOutOfRange";
                }
                logger.info(String.valueOf(result));
                outputMessage = new ConfirmGameMessage(result, errorType, "");
            }
            case JOIN_GAME -> {
                try {
                    gameController = lobbyController.addPlayerToGame(username);
                    result = true;
                    errorType = "";
                    gameController.subscribeToListener((PlayerSubscriber)this);
                    gameController.subscribeToListener((ChatSubscriber) this);
                    gameController.subscribeToListener((BoardSubscriber) this);
                    gameController.subscribeToListener((BookshelfSubscriber) this);
                    gameController.subscribeToListener((GameSubscriber) this);
                } catch (NicknameAlreadyUsedException e) {
                    result = false;
                    errorType = "NicknameAlreadyUsed";
                } catch (NoAvailableGameException e) {
                    result = false;
                    errorType = "NoAvailableGameException";
                } catch (InvalidPlayerException e) {
                    result = false;
                    errorType = "InvalidPlayerException";
                } catch (PlayersNumberOutOfRange e) {
                    result = false;
                    errorType = "PlayersNumberOutOfRange";
                }
                outputMessage = new ConfirmGameMessage(result, errorType, "");
            }
            case TILES_SELECTION -> {
                TileSelectionMessage tileSelectionMessage = (TileSelectionMessage) inputMessage;
                result = gameController.checkValidRetrieve(username, tileSelectionMessage.getTiles());
                outputMessage = new ConfirmSelectionMessage(result);
            }
            case MOVE_TILES -> {
                MoveTilesMessage moveTilesMessage = (MoveTilesMessage) inputMessage;
                try {
                    gameController.moveTiles(username, moveTilesMessage.getTiles(), moveTilesMessage.getColumn());
                    result = true;
                    errorType="";
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
                } catch (InvalidPlayerException e) {
                    result = false;
                    errorType = "InvalidPlayerException";
                    desc = e.getMessage();
                } catch (TokenAlreadyGivenException e) {
                    result = false;
                    errorType = "TokenAlreadyGivenException";
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
    public void updateBoardStatus(Map<Coordinates, Optional<ItemTile>> tilesInBoard) {
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
    }

    @Override
    public void notifyWinningPlayer(String username) {
        NotifyWinningPlayerMessage update = new NotifyWinningPlayerMessage(username);
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



