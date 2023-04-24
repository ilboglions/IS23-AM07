package it.polimi.ingsw.server;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.remoteInterfaces.RemoteGameController;
import it.polimi.ingsw.server.controller.LobbyController;
import it.polimi.ingsw.server.model.exceptions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ServerClientHandlerTCP  implements Runnable{
    private final Socket socket;
    private final LobbyController lobbyController;
    private String username;
    private RemoteGameController gameController;
    public ServerClientHandlerTCP(Socket socket, LobbyController lobbyController) {
        this.socket = socket;
        this.lobbyController = lobbyController;
    }
    public void run() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            NetMessage inputMessage;
            NetMessage outputMessage;
            boolean result;
            String errorType;
            loop: while (true) {
                inputMessage = (NetMessage)inputStream.readObject();
                switch(inputMessage.getMessageType()){
                    case JOIN_LOBBY:
                        JoinLobbyMessage joinLobbyMessage = (JoinLobbyMessage)inputMessage;
                        this.username = joinLobbyMessage.getUsername();
                        result = lobbyController.enterInLobby(joinLobbyMessage.getUsername());
                        outputMessage = new LoginReturnMessage(result, "");
                        break;
                    case CREATE_GAME:
                        CreateGameMessage createGameMessage = (CreateGameMessage)inputMessage;
                        try {
                            gameController = lobbyController.createGame(username, createGameMessage.getPlayerNumber());
                            result = true;
                            errorType = "";
                        }
                        catch (InvalidPlayerException e){
                            result = false;
                            errorType = "InvalidPlayer";
                        }
                        catch (BrokenInternalGameConfigurations e) {
                            result = false;
                            errorType = "BrokenInternalGameConfiguration";
                        }
                        catch (NotEnoughCardsException e) {
                            result = false;
                            errorType = "NotEnoughCardsException";
                        }
                        catch (PlayersNumberOutOfRange e) {
                            result = false;
                            errorType = "PlayersNumberOutOfRange";
                        }
                        outputMessage = new ConfirmGameMessage(result, errorType, "");
                        break;
                    case JOIN_GAME:
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
                        break;
                    case TILES_SELECTION:
                        TileSelectionMessage tileSelectionMessage = (TileSelectionMessage)inputMessage;
                        result = gameController.checkValidRetrieve(username, tileSelectionMessage.getTiles());
                        outputMessage = new ConfirmSelectionMessage(result);
                        break;
                    case MOVE_TILES:
                        MoveTilesMessage moveTilesMessage = (MoveTilesMessage) inputMessage;
                        result = gameController.moveTiles(username, moveTilesMessage.getTiles(), moveTilesMessage.getColumn());
                        outputMessage = new ConfirmMoveMessage(result);
                        break;
                    case POST_MESSAGE:
                        PostMessage postMessage = (PostMessage) inputMessage;
                        if(postMessage.getRecipient().isPresent()){
                            result = gameController.postDirectMessage(username, postMessage.getRecipient().get(),postMessage.getContent());
                        }
                        else{
                            result = gameController.postBroadCastMessage(username, postMessage.getContent());
                        }
                        outputMessage = new ConfirmChatMessage(result);
                        break;
                    default:
                        break loop;
                }
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
}



