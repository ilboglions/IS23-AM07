package it.polimi.ingsw.model.distributable;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface Distributable<T> {
    ArrayList<T> draw(int nElements) throws FileNotFoundException;

}
