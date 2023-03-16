package it.polimi.ingsw.model.distributable;
import it.polimi.ingsw.model.cards.CommonGoalCard;

import java.util.ArrayList;

public class DeckCommon implements Distributable<CommonGoalCard>{
    private final ArrayList<CommonGoalCard> cardList;

    public DeckCommon() {

        this.cardList = new ArrayList<CommonGoalCard>();

    }

    @Override
    public ArrayList<CommonGoalCard> draw(int nElements) {
        ArrayList<CommonGoalCard> selected = new ArrayList<CommonGoalCard>();
        for( int i = 0; i < nElements; i++){
            selected.add(cardList.get(i));
        }
        return selected;
    }

    @Override
    public void shuffle() {

        return;
    }
}
