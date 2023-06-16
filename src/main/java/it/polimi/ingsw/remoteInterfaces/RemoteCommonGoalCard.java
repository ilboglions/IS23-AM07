package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.cards.common.CommonCardType;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Stack;

/**
 * This interface specifies the methods exposed by a CommonGoalCard either over RMI or TCP
 */
public interface RemoteCommonGoalCard extends Remote, Serializable {
    /**
     * @return the card description
     * @throws RemoteException RMI Exception
     */
    String getDescription() throws RemoteException;
    /**
     * @return the type of the Common card
     * @throws RemoteException RMI Exception
     */
    CommonCardType getName() throws RemoteException;

    /**
     * @return a stack of all the token
     * @throws RemoteException RMI Exception
     */
    Stack<ScoringToken> getTokenStack() throws RemoteException;
}
