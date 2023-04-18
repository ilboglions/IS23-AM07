package it.polimi.ingsw.server.model.distributable;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.cards.common.*;
import it.polimi.ingsw.server.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.exceptions.NotEnoughCardsException;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.cards.common.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;
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
     * @throws PlayersNumberOutOfRange if the nPlayers is less than 2 and grater than 4
     */
    public DeckCommon(int nPlayers, String configuration) throws PlayersNumberOutOfRange {
        if(nPlayers < 2 || nPlayers > 4)
            throw new PlayersNumberOutOfRange("Expected min 2 and max 4 players, you gave " + nPlayers);

        this.nPlayers = nPlayers;
        this.configuration = Objects.requireNonNull(configuration, "You passed a null instead of a String for the configuration file");
    }


    /**
     * Draw method consents to draw a number of CommonGoalCard elements
     * @param nElements the number of elements to draw
     * @return an ArrayList that contains the drawn elements
     * @throws FileNotFoundException if the configuration cannot be found, this exception is thrown
     * @throws NegativeFieldException if the nElements is negative
     * @throws PlayersNumberOutOfRange if the number of players is less than 2 or grater than 4
     * @throws NotEnoughCardsException if the number of the cards read from the JSON file is less than the required cards with nElements
     */
    public ArrayList<CommonGoalCard> draw(int nElements) throws FileNotFoundException, NegativeFieldException, PlayersNumberOutOfRange, NotEnoughCardsException {
        ArrayList<CommonGoalCard> selected = new ArrayList<>();
        Gson gson = new Gson();
        Random randGenerator = new Random();
        ArrayList<Integer> generatedCardsIndex = new ArrayList<>();
        int extractedCardInex;

        if(nElements < 0)
            throw new NegativeFieldException("You can't draw a negative number of cards");

        JsonArray jsonCards = gson.fromJson(new FileReader(this.configuration), JsonArray.class);
        if(jsonCards.size() < nElements) throw new NotEnoughCardsException("error! only "+jsonCards.size()+" cards available");

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
        Objects.requireNonNull(cardConfiguration, "You passed a null instead of a JsonObject");

        boolean sameTiles;
        int nCols;
        int maxTilesFrule;
        int nRows;
        int nGroups;
        int nElems;
        Gson gson = new Gson();
        String objectType = cardConfiguration.get("cardType").getAsString();
        String description= cardConfiguration.get("description").getAsString();
        ArrayList<ArrayList<Coordinates>> pattern = new ArrayList<>();
        TypeToken<ArrayList<Coordinates>> coordArrType = new TypeToken<>(){};



        switch (objectType) {
            case "CheckPattern" -> {
                cardConfiguration.get("Attributes").getAsJsonObject().get("pattern").getAsJsonArray()
                        .forEach(
                                el -> pattern.add(gson.fromJson(el, coordArrType))
                        );
                sameTiles = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("sameTiles"), Boolean.class);

                return new CheckPattern(nPlayers, description, pattern, sameTiles);
            }
            case "FiveXTiles" -> {

                sameTiles = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("sameTiles"), Boolean.class);

                return new FiveXTiles(nPlayers, description, sameTiles);

            }

            case "MarioPyramid" -> {

                return new MarioPyramid(nPlayers, description);

            }

            case "FullColumns" -> {

                sameTiles = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("sameTiles"), Boolean.class);
                nCols = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nCols"), Integer.class);
                maxTilesFrule = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("maxTilesFrule"), Integer.class);

                return new FullColumns(nPlayers, description,nCols, sameTiles, maxTilesFrule);
            }
            case "FullRows" -> {

                sameTiles = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("sameTiles"), Boolean.class);
                nRows = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nRows"), Integer.class);
                maxTilesFrule = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("maxTilesFrule"), Integer.class);

                return new FullRows(nPlayers,description, nRows, sameTiles, maxTilesFrule);
            }

            case "NadjacentElements" -> {

                nElems = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nElems"), Integer.class);
                nGroups = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nGroups"), Integer.class);

                return new NadjacentElements(nPlayers,description,nGroups, nElems);
            }

            case "NequalsSquares" -> {

                int nSquares = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nSquares"), Integer.class);
                int squareDim = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("squareDim"), Integer.class);

                return new NequalsSquares(nPlayers,description, nSquares, squareDim);
            }
            case "NsameTiles" -> {
                int nTiles = gson.fromJson(cardConfiguration.get("Attributes").getAsJsonObject().get("nTiles"), Integer.class);
                return new NsameTiles(nPlayers, description,nTiles);
            }
            default -> throw new IllegalArgumentException();
        }
    }




}
