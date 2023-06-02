package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.cards.common.CommonCardType;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Stack;

public interface RemoteCommonGoalCard extends Remote, Serializable {
    String getDescription() throws RemoteException;
    CommonCardType getName() throws RemoteException;

    Stack<ScoringToken> getTokenStack() throws RemoteException;
}
