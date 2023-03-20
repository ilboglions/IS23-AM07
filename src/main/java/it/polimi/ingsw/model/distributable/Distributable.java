package it.polimi.ingsw.model.distributable;

import it.polimi.ingsw.model.distributable.exception.NumElementsNegativeException;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Distributable is an interface used for collections of elements that must be distributed, such as cards or tiles.
 * @param <T> the object that the interface has to deal with
 */
public interface Distributable<T> {
    /**
     * Draw method consents to draw a number of T elements
     * @param nElements the number of elements to draw
     * @return an ArrayList that contains the drawn elements
     * @throws FileNotFoundException - some implementation of Distributable interface had to deal with configuration files,
     *  so exception is thrown if the configuration file can not be found.
     */
    ArrayList<T> draw(int nElements) throws FileNotFoundException, NumElementsNegativeException;

}
