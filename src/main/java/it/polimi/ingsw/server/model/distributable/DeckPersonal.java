package it.polimi.ingsw.server.model.distributable;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.server.model.cards.personal.PersonalGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.IllegalFilePathException;
import it.polimi.ingsw.server.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughCardsException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.*;

/**
 * DeckPersonal is an implementation of the Distributable interface, it deals with personalCards,
 * It can be used to draw personal cards. As a factory method, it creates only the necessary cards based on a given configuration.
 */
public class DeckPersonal implements Distributable<PersonalGoalCard> {
    /**
     * used to store the path to the card configuration file
     */
    private final String configurationFile;
    /**
     * used to store the point reference
     */
    private final String pointsReferenceFile;
    /**
     * a set containing all the indexes of the card that have been already distributed
     */
    private final Set<Integer> generatedCardsIndex;

    /**
     * The deckPersonal constructor assign the configurations parameters in order to create the cards.
     * @param configurationFile used for the card pattern
     * @param pointsReferenceFile used to create a reference for the points
     */
    public DeckPersonal(String configurationFile, String pointsReferenceFile) throws IllegalFilePathException {
        Objects.requireNonNull(configurationFile, "You passed a null instead of a String for the configuration file");
        Objects.requireNonNull(pointsReferenceFile, "You passed a null instead of a String for the points reference file");
        if(configurationFile.isBlank() || pointsReferenceFile.isBlank())
            throw new IllegalFilePathException("You passed an empty string");

        this.configurationFile = configurationFile;
        this.pointsReferenceFile = pointsReferenceFile;
        this.generatedCardsIndex = new HashSet<>();
    }

    /**
     * Draw method consents to draw a number of PersonalGoalCard elements
     * @param nElements the number of elements to draw
     * @return an ArrayList that contains the drawn elements
     * @throws NotEnoughCardsException if the number of cards read from the JSON is less than the nElements required
     */
    @Override
    public ArrayList<PersonalGoalCard> draw(int nElements) throws NotEnoughCardsException, NegativeFieldException {
        if(nElements < 0)
            throw new NegativeFieldException("You can't draw a negative number of cards");

        ArrayList<PersonalGoalCard> selected = new ArrayList<>();
        TypeToken<Map<Coordinates, ItemTile>> mapType = new TypeToken<>(){};
        TypeToken<Map<Integer, Integer>> pointsReferenceMapType = new TypeToken<>(){};
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Random randGenerator = new Random();

        int extractedCardIndex;


        JsonArray jsonCards = gson.fromJson(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(configurationFile))), JsonArray.class);
        Map<Integer, Integer> pointsReference = gson.fromJson(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(pointsReferenceFile))), pointsReferenceMapType);

        if(jsonCards.size() < nElements) throw new NotEnoughCardsException("error! only "+jsonCards.size()+" cards available");

        for( int i = 0; i < nElements; i++){
            do {
                extractedCardIndex = randGenerator.nextInt(jsonCards.size());
            } while(this.generatedCardsIndex.contains(extractedCardIndex));

            this.generatedCardsIndex.add(extractedCardIndex);

            JsonElement extractedCard = jsonCards.get(extractedCardIndex);
            Map<Coordinates, ItemTile> pattern = gson.fromJson(extractedCard, mapType);

            try {
                selected.add(new PersonalGoalCard(pattern, pointsReference));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        return selected;
    }

}
