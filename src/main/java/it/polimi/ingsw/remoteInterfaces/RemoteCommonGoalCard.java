package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.cards.common.CommonCardType;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteCommonGoalCard extends Remote, Serializable {
    public String getDescription() throws RemoteException;
    public CommonCardType getName() throws RemoteException;
}
