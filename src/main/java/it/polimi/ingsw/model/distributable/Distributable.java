package it.polimi.ingsw.model.distributable;

import it.polimi.ingsw.model.cards.CommonGoalCard;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface Distributable<T> {
    ArrayList<T> draw(int nElements,int nPlayers);
    void shuffle();
}
