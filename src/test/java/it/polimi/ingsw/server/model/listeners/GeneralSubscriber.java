package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.GameState;
import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.game.GameModelInterface;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

class GeneralSubscriber implements BoardSubscriber, ChatSubscriber, BookshelfSubscriber, GameSubscriber, GameStateSubscriber, PlayerSubscriber {
    private final String name;

    public GeneralSubscriber(String name) {
        this.name  = name;
    }

    @Override
    public void updateBoardStatus(Map<Coordinates, ItemTile> tilesInBoard) throws RemoteException {

    }

    @Override
    public void updateBookshelfStatus(String player, ArrayList<ItemTile> tilesInserted, int colChosen, Map<Coordinates, ItemTile> currentTilesMap) throws RemoteException {

    }

    @Override
    public void receiveMessage(String from, String recipient, String msg) throws RemoteException {

    }

    @Override
    public void receiveMessage(String from, String msg) throws RemoteException {

    }

    @Override
    public void notifyChangedGameStatus(GameState newState, GameModelInterface gameModelInterface) throws RemoteException {

    }

    @Override
    public void notifyPlayerJoined(String username) throws RemoteException {

    }

    @Override
    public void notifyWinningPlayer(String username, int points, Map<String, Integer> scoreboard) throws RemoteException {

    }

    @Override
    public void notifyCommonGoalCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) throws RemoteException {

    }

    @Override
    public void notifyPlayerInTurn(String username) throws RemoteException {

    }

    @Override
    public void notifyPlayerCrashed(String userCrashed) throws RemoteException {

    }

    @Override
    public void notifyTurnOrder(ArrayList<String> playerOrder) throws RemoteException {

    }

    @Override
    public void notifyAlreadyJoinedPlayers(Set<String> alreadyJoinedPlayers) throws RemoteException {

    }

    @Override
    public void notifyChangedGameState(GameState newState) throws RemoteException {

    }

    @Override
    public String getSubscriberUsername() throws RemoteException {
        return name;
    }

    @Override
    public void updatePoints(String player, int overallPoints, int addedPoints) throws RemoteException {

    }

    @Override
    public void updateTokens(String player, ArrayList<ScoringToken> tokenPoints) throws RemoteException {

    }

    @Override
    public void updatePersonalGoalCard(String player, RemotePersonalGoalCard remotePersonal) throws RemoteException {

    }
}
