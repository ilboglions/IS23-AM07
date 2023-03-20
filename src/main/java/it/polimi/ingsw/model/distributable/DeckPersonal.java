package it.polimi.ingsw.model.distributable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class DeckPersonal implements Distributable<PersonalGoalCard> {
    private final String configurationFile;

    public DeckPersonal(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public ArrayList<PersonalGoalCard> draw(int nElements) {
        ArrayList<PersonalGoalCard> selected = new ArrayList<>();
        TypeToken<Map<Coordinates, ItemTile>> mapType = new TypeToken<Map<Coordinates, ItemTile>>(){};
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Random randGenerator = new Random();
        ArrayList<Integer> generatedCardsIndex = new ArrayList<>();
        int extractedCardIndex;

        try {
            JsonArray jsonCards = gson.fromJson(new FileReader(configurationFile), JsonArray.class);

            for( int i = 0; i < nElements; i++){
                do {
                    extractedCardIndex = randGenerator.nextInt(jsonCards.size());
                } while(generatedCardsIndex.contains(extractedCardIndex));

                generatedCardsIndex.add(extractedCardIndex);

                JsonObject extractedCard = jsonCards.get(i).getAsJsonObject();
                Map<Coordinates, ItemTile> pattern = gson.fromJson(extractedCard, mapType);

                selected.add(new PersonalGoalCard(pattern));
            }
        } catch (FileNotFoundException e){
            return selected;
        }
        return selected;
    }

}
