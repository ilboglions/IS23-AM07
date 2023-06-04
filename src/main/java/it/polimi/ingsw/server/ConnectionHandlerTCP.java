package it.polimi.ingsw.server;

import it.polimi.ingsw.GameState;
import it.polimi.ingsw.Notifications;
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
import java.util.*;
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
        this.startParserAgent();
        this.messagesHopper();
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
                    } catch ( ClassNotFoundException | InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch ( IOException ignored){

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
            case JOIN_LOBBY -> outputMessage =  this.parse((JoinLobbyMessage) inputMessage);
            case CREATE_GAME -> outputMessage = this.parse((CreateGameMessage) inputMessage);
            case JOIN_GAME -> outputMessage = this.parse((JoinGameMessage) inputMessage);
            case TILES_SELECTION -> outputMessage = this.parse((TileSelectionMessage) inputMessage);
            case MOVE_TILES -> outputMessage = this.parse((MoveTilesMessage) inputMessage);
            case POST_MESSAGE -> outputMessage = this.parse((PostMessage) inputMessage);
            case GAME_RECEIVED_MESSAGE -> outputMessage = this.parse((GameReceivedMessage) inputMessage);
            case STILL_ACTIVE -> outputMessage = new StillActiveMessage();
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
            errorType = Notifications.ERR_ALREADY_PLAYING_A_GAME.getTitle();
            result = false;
            hasPlayerJoined = false;
            desc = Notifications.ERR_ALREADY_PLAYING_A_GAME.getDescription();
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
                errorType = Notifications.ERR_USERNAME_ALREADY_TAKEN.getTitle();
                result = false;
                hasPlayerJoined = false;
                desc = Notifications.ERR_USERNAME_ALREADY_TAKEN.getDescription();
            } catch (InvalidPlayerException e) {
                errorType = Notifications.ERR_INVALID_USERNAME.getTitle();
                result = false;
                hasPlayerJoined = false;
                desc = Notifications.ERR_INVALID_USERNAME.getDescription();
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
            errorType = Notifications.ERR_PLAYER_NO_JOINED_IN_LOBBY.getTitle();
            desc = Notifications.ERR_PLAYER_NO_JOINED_IN_LOBBY.getDescription();
        } catch (PlayersNumberOutOfRange e) {
            result = false;
            errorType = Notifications.ERR_GAME_N_PLAYER_OUT_OF_RANGE.getTitle();
            desc = Notifications.ERR_GAME_N_PLAYER_OUT_OF_RANGE.getDescription();
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
            errorType = Notifications.ERR_USERNAME_ALREADY_TAKEN.getTitle();
            desc = Notifications.ERR_USERNAME_ALREADY_TAKEN.getDescription();
        } catch (NoAvailableGameException e) {
            result = false;
            errorType = Notifications.ERR_GAME_NO_AVAILABLE.getTitle();
            desc = Notifications.ERR_GAME_NO_AVAILABLE.getDescription();
        } catch (InvalidPlayerException e) {
            result = false;
            errorType = Notifications.ERR_PLAYER_NO_JOINED_IN_LOBBY.getTitle();
            desc = Notifications.ERR_PLAYER_NO_JOINED_IN_LOBBY.getDescription();
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
                errorType = Notifications.INVALID_TILES_SELECTION.getTitle();
                desc = Notifications.INVALID_TILES_SELECTION.getDescription();
                logger.info(desc);
            }

        } catch (PlayerNotInTurnException e) {
            result = false;
            errorType = Notifications.PLAYER_NOT_IN_TURN.getTitle();
            desc = Notifications.PLAYER_NOT_IN_TURN.getDescription();
        } catch (GameNotStartedException e) {
            result = false;
            errorType = Notifications.ERR_GAME_NOT_STARTED.getTitle();
            desc = Notifications.ERR_GAME_NOT_STARTED.getDescription();
        } catch (GameEndedException e) {
            result = false;
            errorType = Notifications.ERR_GAME_ENDED.getTitle();
            desc = Notifications.ERR_GAME_ENDED.getDescription();
        } catch (EmptySlotException e) {
            result = false;
            errorType = Notifications.ERR_EMPTY_SLOT_SELECTED.getTitle();
            desc = Notifications.ERR_EMPTY_SLOT_SELECTED.getDescription();
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
                errorType = Notifications.CHAT_SENDER_EQUALS_RECIPIENT.getTitle();
                desc = Notifications.CHAT_SENDER_EQUALS_RECIPIENT.getDescription();
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
        String desc = "";

        try {
            gameController.moveTiles(username, moveTilesMessage.getTiles(), moveTilesMessage.getColumn());
            result = true;
        } catch (GameNotStartedException e) {
            result = false;
            errorType = Notifications.ERR_GAME_NOT_STARTED.getTitle();
            desc = Notifications.ERR_GAME_NOT_STARTED.getDescription();
        } catch (GameEndedException e) {
            result = false;
            errorType = Notifications.ERR_GAME_ENDED.getTitle();
            desc = Notifications.ERR_GAME_ENDED.getDescription();
        } catch (EmptySlotException e) {
            result = false;
            errorType = Notifications.ERR_EMPTY_SLOT_SELECTED.getTitle();
            desc = Notifications.ERR_EMPTY_SLOT_SELECTED.getDescription();
        } catch (NotEnoughSpaceException e) {
            result = false;
            errorType = Notifications.NO_SPACE_IN_BOOKSHELF_COLUMN.getTitle();
            desc = Notifications.NO_SPACE_IN_BOOKSHELF_COLUMN.getDescription();
        } catch (InvalidCoordinatesException e) {
            result = false;
            errorType = Notifications.INVALID_TILES_SELECTION.getTitle();
            desc = Notifications.INVALID_TILES_SELECTION.getDescription();
        } catch (PlayerNotInTurnException e) {
            result = false;
            errorType = Notifications.PLAYER_NOT_IN_TURN.getTitle();
            desc = Notifications.PLAYER_NOT_IN_TURN.getDescription();
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
    public void receiveMessage(String from, String msg) {
        logger.info("SENDING MESSAGE TO " + this.username);
        NotifyNewChatMessage update = new NotifyNewChatMessage(from, msg);
        this.sendUpdate(update);
    }

    @Override
    public void receiveMessage(String from, String recipient, String msg) {
        logger.info("SENDING MESSAGE TO " + this.username);
        NotifyNewChatMessage update = new NotifyNewChatMessage(from, recipient, msg);
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
    public void notifyCommonGoalCards(ArrayList<RemoteCommonGoalCard> commonGoalCards){
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


    @Override
    public void notifyAlreadyJoinedPlayers(Set<String> alreadyJoinedPlayers) throws RemoteException {
        this.sendUpdate( new AlreadyJoinedPlayersMessage(alreadyJoinedPlayers));
    }

    /**
     * @param newState
     * @throws RemoteException
     */
    @Override
    public void notifyChangedGameState(GameState newState) throws RemoteException {
        GameStatusMessage update = new GameStatusMessage(newState);
        this.sendUpdate(update);
    }

    /**
     *
     * @param update
     */
    private void sendUpdate(NetMessage update){
        synchronized (outputStream) {
            try {
                logger.info("SENDING..." + update.getMessageType()+ " to "+this.username );
                outputStream.writeObject(update);
                outputStream.flush();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

}



