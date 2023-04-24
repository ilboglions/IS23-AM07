package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.remoteInterfaces.RemoteGameController;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.model.exceptions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;


public class ServerClientHandlerTCP  implements Runnable{
    private final Socket socket;
    private boolean closeConnectionFlag;
    private final LobbyController lobbyController;
    private String username;
    private RemoteGameController gameController;
    public ServerClientHandlerTCP(Socket socket, LobbyController lobbyController) {
        this.socket = socket;
        this.lobbyController = lobbyController;
        closeConnectionFlag = false;
    }
    public void run() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            NetMessage inputMessage;
            NetMessage outputMessage;
            while (true) {
                inputMessage = (NetMessage)inputStream.readObject();
                outputMessage = messageParser(inputMessage);
                if(closeConnectionFlag)
                    break;
                outputStream.writeObject(outputMessage);
            }
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private NetMessage messageParser(NetMessage inputMessage) throws RemoteException {
        NetMessage outputMessage;
        boolean result;
        String errorType;
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
                    errorType = "";
                } catch (InvalidPlayerException e) {
                    result = false;
                    errorType = "InvalidPlayer";
                } catch (BrokenInternalGameConfigurations e) {
                    result = false;
                    errorType = "BrokenInternalGameConfiguration";
                } catch (NotEnoughCardsException e) {
                    result = false;
                    errorType = "NotEnoughCardsException";
                } catch (PlayersNumberOutOfRange e) {
                    result = false;
                    errorType = "PlayersNumberOutOfRange";
                }
                outputMessage = new ConfirmGameMessage(result, errorType, "");
            }
            case JOIN_GAME -> {
                try {
                    gameController = lobbyController.addPlayerToGame(username);
                    result = true;
                    errorType = "";
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
                result = gameController.moveTiles(username, moveTilesMessage.getTiles(), moveTilesMessage.getColumn());
                outputMessage = new ConfirmMoveMessage(result);
            }
            case POST_MESSAGE -> {
                PostMessage postMessage = (PostMessage) inputMessage;
                if (postMessage.getRecipient().isPresent()) {
                    result = gameController.postDirectMessage(username, postMessage.getRecipient().get(), postMessage.getContent());
                } else {
                    result = gameController.postBroadCastMessage(username, postMessage.getContent());
                }
                outputMessage = new ConfirmChatMessage(result);
            }
            default -> {
                closeConnectionFlag = true;
                outputMessage = new CloseConnectionMessage();
            }
        }

        return outputMessage;
    }
}



