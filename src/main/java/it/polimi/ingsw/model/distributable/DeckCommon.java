package it.polimi.ingsw.model.distributable;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.cards.common.*;
import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.cards.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.coordinate.Coordinates;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * DeckCommon is an implementation of the Distributable interface, it deals with commonCards,
 * It can be used to draw common cards. As a factory method, it creates only the necessary cards based on a given configuration.
 */
public class DeckCommon implements Distributable<CommonGoalCard>{

    /**
     * this attribute contains the number of players of the game, it is used for dealing with the card generation
     */
    int nPlayers;
    /**
     * the configuration attribute contains a path to a configuration file, which contains all the common cards
     */
    String configuration;

    /**
     * The DeckCommon constructor initialize the deck with the given attributes
     * @param nPlayers the number of players of the actual game
     * @param configuration the path to the json configuration file
     */
    public DeckCommon(int nPlayers, String configuration){
        this.nPlayers = nPlayers;
        this.configuration = configuration;
    }


    /**
     * Draw method consents to draw a number of CommonGoalCard elements
     * @param nElements the number of elements to draw
     * @return an ArrayList that contains the drawn elements
     * @throws FileNotFoundException if the configuration cannot be found, this exception is thrown
     */
    public ArrayList<CommonGoalCard> draw(int nElements) throws FileNotFoundException, NegativeFieldException, PlayersNumberOutOfRange {
        ArrayList<CommonGoalCard> selected = new ArrayList<>();
        Gson gson = new Gson();
        Random randGenerator = new Random();
        ArrayList<Integer> generatedCardsIndex = new ArrayList<>();
        int extractedCardInex;

        JsonArray jsonCards = gson.fromJson(new FileReader(this.configuration), JsonArray.class);

        for( int i = 0; i < nElements; i++){
            do {
                extractedCardInex = randGenerator.nextInt(jsonCards.size());
            }while (generatedCardsIndex.contains(extractedCardInex));

            generatedCardsIndex.add(extractedCardInex);

            selected.add(createCard(jsonCards.get(i).getAsJsonObject(), nPlayers));

        }


        return selected;
    }

    /**
     * createCard make it possible to create a card based on the configuration element extracted by draw method
     * @param cardConfiguration the jsonObject that represent the configuration card
     * @param nPlayers the players that are playing the game, necessary for the card's creation
     * @return the CommonGoalCard, with the right configuration given
     * @throws PlayersNumberOutOfRange if the number of players exceed the limit, this exception will be thrown
     * @throws NegativeFieldException if the JsonObject contains a negative field, NegativeFieldException will be thrown
     * @throws IllegalArgumentException if the JsonObject doesn't match the right pattern, an IllegalArgumentException will be thrown
     */
    private CommonGoalCard createCard(JsonObject cardConfiguration, int nPlayers) throws PlayersNumberOutOfRange, NegativeFieldException, IllegalArgumentException {
        boolean sameTiles;
        int nCols;
        int maxTilesFrule;
        int nRows;
        int nGroups;
        int nElems;
        Gson gson = new Gson();
        String objectType = cardConfiguration.get("cardType").getAsString();
        String description= gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("description").getAsString(), String.class);
        ArrayList<ArrayList<Coordinates>> pattern = new ArrayList<>();
        TypeToken<ArrayList<Coordinates>> coordArrType = new TypeToken<>(){};

        switch (objectType) {
            case "CheckPattern" -> {
                cardConfiguration.get("Attributes").getAsJsonObject().get("pattern").getAsJsonArray()
                        .forEach(
                                el -> pattern.add(gson.fromJson(el.getAsString(), coordArrType))
                        );
                sameTiles = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("sameTiles").getAsString(), Boolean.class);

                return new CheckPattern(nPlayers, description, pattern, sameTiles);
            }
            case "FiveXTiles" -> {

                sameTiles = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("sameTiles").getAsString(), Boolean.class);

                return new FiveXTiles(nPlayers, description, sameTiles);

            }

            case "FullColumns" -> {

                sameTiles = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("sameTiles").getAsString(), Boolean.class);
                nCols = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nCols").getAsString(), Integer.class);
                maxTilesFrule = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("maxTilesFrule").getAsString(), Integer.class);

                return new FullColumns(nPlayers, description,nCols, sameTiles, maxTilesFrule);
            }
            case "FullRows" -> {

                sameTiles = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("sameTiles").getAsString(), Boolean.class);
                nRows = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nRows").getAsString(), Integer.class);
                maxTilesFrule = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("maxTilesFrule").getAsString(), Integer.class);

                return new FullRows(nPlayers,description, nRows, sameTiles, maxTilesFrule);
            }

            case "NadiacentElements" -> {

                nElems = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nElems").getAsString(), Integer.class);
                nGroups = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nGroups").getAsString(), Integer.class);

                return new NadiacentElements(nPlayers,description,nGroups, nElems);
            }

            case "NequalsSquare" -> {

                int nSquares = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nElems").getAsString(), Integer.class);
                int squareDim = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nGroups").getAsString(), Integer.class);

                return new NequalsSquares(nPlayers,description, nSquares, squareDim);
            }
            case "NsameTiles" -> {
                int nTiles = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nTiles").getAsString(), Integer.class);
                return new NsameTiles(nPlayers, description,nTiles);
            }
            default -> throw new IllegalArgumentException();
        }
    }




}
