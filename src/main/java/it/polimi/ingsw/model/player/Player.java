package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.bookshelf.PlayerBookshelf;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tokens.ScoringToken;

import java.util.ArrayList;

public class Player {
    private final String nickname;
    private PersonalGoalCard personalCard;
    private final PlayerBookshelf bookshelf;
    private final ArrayList<ScoringToken> tokenAcquired;
    private int points;

    public Player(String nickname) {
        this.nickname = nickname;
        this.bookshelf = new PlayerBookshelf();
        this.tokenAcquired = new ArrayList<ScoringToken>();
        this.points = 0;
    }

    public String getNickname() {
        return nickname;
    }

    public PersonalGoalCard getPersonalCard() {
        return personalCard;
    }

    public PlayerBookshelf getBookshelf() {
        return bookshelf;
    }

    public int getPoints() {
        return points;
    }

    public int calculatePointsPersonalGoalCard() {
        int count = 0;
        Coordinates c;

        for(int i=0; i<bookshelf.getRows(); i++) {
            for(int j=0; j<bookshelf.getColumns(); j++) {
                c = new Coordinates(i,j);
                if(personalCard.getCardBookshelf().getItemTile(c).isPresent() && bookshelf.getItemTile(c).equals(personalCard.getCardBookshelf().getItemTile(c)))
                    count++;
            }
        }

        return personalCard.getPointsReference().get(count);
    }

    public int updatePoints() {
        int total = 0;

        for(ScoringToken token : tokenAcquired) {
            total += token.getScoreValue().getValue();
        }

        total += calculatePointsPersonalGoalCard();
        this.points = total;

        return this.points;
    }

    public void addToken(ScoringToken token) {
        this.tokenAcquired.add(token);
    }

    public void assignPersonalCard(PersonalGoalCard card) {
        this.personalCard = card;
    }
}
