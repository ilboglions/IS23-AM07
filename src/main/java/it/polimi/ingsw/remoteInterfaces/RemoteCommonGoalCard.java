package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.cards.common.CommonCardType;

import java.io.Serializable;
import java.rmi.Remote;

public interface RemoteCommonGoalCard extends Remote, Serializable {
    public String getDescription();
    public CommonCardType getName();
}
