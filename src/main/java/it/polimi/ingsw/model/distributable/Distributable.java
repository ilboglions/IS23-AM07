package it.polimi.ingsw.model.distributable;

import java.util.ArrayList;

public interface Distributable<T> {
    ArrayList<T> draw(int nElements);
    void shuffle();
}
