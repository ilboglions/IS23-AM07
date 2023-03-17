package it.polimi.ingsw.model.distributable;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.cards.exceptions.tooManyPlayersException;
import it.polimi.ingsw.model.coordinates.Coordinates;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class DeckCommon implements Distributable<CommonGoalCard>{



    @Override
    public ArrayList<CommonGoalCard> draw(int nElements, int nPlayers) {
        ArrayList<CommonGoalCard> selected = new ArrayList<CommonGoalCard>();
        Gson gson = new Gson();
        try {
            JsonArray jsonArray = gson.fromJson(new FileReader("cards/confFiles/commonCards.json"), JsonArray.class);
            // 2implement: just draw 2 numbers and then creates only that two cards!
            jsonArray.forEach( el -> {
                try {
                    selected.add(createCard(el.getAsJsonObject(), nPlayers));
                } catch (tooManyPlayersException | NegativeFieldException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (FileNotFoundException e){
            return null;
        }
        return selected;
    }

    private CommonGoalCard createCard(JsonObject asJsonObject, int nPlayers) throws tooManyPlayersException, NegativeFieldException {
        boolean sameTiles;
        int nCols;
        int maxTilesFrule;
        int nRows;
        int nGroups;
        int nElems;

        String objectType = asJsonObject.get("cardType").getAsString();
        ArrayList<Coordinates> pattern = new ArrayList<>();
        Gson gson = new Gson();

        switch (objectType) {
            case "CheckPattern" -> {
                asJsonObject.get("Attributes").getAsJsonObject().get("pattern").getAsJsonArray()
                        .forEach(
                                el -> {
                                    pattern.add(gson.fromJson(el.getAsString(), Coordinates.class));
                                }
                        );
                sameTiles = gson.fromJson(asJsonObject.get("Attributes").getAsJsonObject().get("sameTiles").getAsString(), Boolean.class);

                return new CheckPattern(nPlayers, pattern, sameTiles);
            }
            case "FullColumns" -> {

                sameTiles = gson.fromJson(asJsonObject.get("Attributes").getAsJsonObject().get("sameTiles").getAsString(), Boolean.class);
                nCols = gson.fromJson(asJsonObject.get("Attributes").getAsJsonObject().get("nCols").getAsString(), Integer.class);
                maxTilesFrule = gson.fromJson(asJsonObject.get("Attributes").getAsJsonObject().get("maxTilesFrule").getAsString(), Integer.class);

                return new FullColumns(nPlayers, nCols, sameTiles, maxTilesFrule);
            }
            case "FullRows" -> {

                sameTiles = gson.fromJson(asJsonObject.get("Attributes").getAsJsonObject().get("sameTiles").getAsString(), Boolean.class);
                nRows = gson.fromJson(asJsonObject.get("Attributes").getAsJsonObject().get("nRows").getAsString(), Integer.class);
                maxTilesFrule = gson.fromJson(asJsonObject.get("Attributes").getAsJsonObject().get("maxTilesFrule").getAsString(), Integer.class);

                return new FullRows(nPlayers, nRows, sameTiles, maxTilesFrule);
            }

            case "NadiacentElements" -> {

                nElems = gson.fromJson(asJsonObject.get("Attributes").getAsJsonObject().get("nElems").getAsString(), Integer.class);
                nGroups = gson.fromJson(asJsonObject.get("Attributes").getAsJsonObject().get("nGroups").getAsString(), Integer.class);

                return new NadiacentElements(nPlayers,nGroups, nElems);
            }
            default -> {
                return null;
            }
        }
    }


    @Override
    public void shuffle() {

        return;
    }
}
