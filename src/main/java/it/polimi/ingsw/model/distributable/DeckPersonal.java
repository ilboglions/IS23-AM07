package it.polimi.ingsw.model.distributable;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.cards.personal.PersonalGoalCard;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.NotEnoughCardsException;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.io.FileNotFoundException;
import java.io.FileReader;
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

    /**
     * a set containing all the indexes of the card that have been already distributed
     */
    private final String pointsReferenceFile;

    private final Set<Integer> generatedCardsIndex;

    /**
     * The deckPersonal constructor assign the configurations parameters in order to create the cards.
     * @param configurationFile used for the card pattern
     * @param pointsReferenceFile used to create a reference for the points
     */
    public DeckPersonal(String configurationFile, String pointsReferenceFile) {
        this.configurationFile = Objects.requireNonNull(configurationFile, "You passed a null instead of a String for the configuration file");
        this.pointsReferenceFile = Objects.requireNonNull(pointsReferenceFile, "You passed a null instead of a String for the points reference file");
        this.generatedCardsIndex = new HashSet<>();
    }

    /**
     * Draw method consents to draw a number of PersonalGoalCard elements
     * @param nElements the number of elements to draw
     * @return an ArrayList that contains the drawn elements
     * @throws FileNotFoundException if the configuration cannot be found, this exception is thrown
     */
    @Override
    public ArrayList<PersonalGoalCard> draw(int nElements) throws FileNotFoundException, NotEnoughCardsException {
        ArrayList<PersonalGoalCard> selected = new ArrayList<>();
        TypeToken<Map<Coordinates, ItemTile>> mapType = new TypeToken<>(){};
        TypeToken<Map<Integer, Integer>> pointsReferenceMapType = new TypeToken<>(){};
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Random randGenerator = new Random();

        int extractedCardIndex;


        JsonArray jsonCards = gson.fromJson(new FileReader(configurationFile), JsonArray.class);
        Map<Integer, Integer> pointsReference = gson.fromJson(new FileReader(pointsReferenceFile), pointsReferenceMapType);

        if(jsonCards.size() < nElements) throw new NotEnoughCardsException("error! only "+jsonCards.size()+" cards available");

        for( int i = 0; i < nElements; i++){
            do {
                extractedCardIndex = randGenerator.nextInt(jsonCards.size());
            } while(this.generatedCardsIndex.contains(extractedCardIndex));

            this.generatedCardsIndex.add(extractedCardIndex);

            JsonElement extractedCard = jsonCards.get(i);
            Map<Coordinates, ItemTile> pattern = gson.fromJson(extractedCard, mapType);

            selected.add(new PersonalGoalCard(pattern, pointsReference));
        }

        return selected;
    }

}
