// Player.java

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final PlayerType playerType;
    protected Hand hand;
    protected int score;
    protected List<Card> playedCards;
    public Player(String playerTypeStr, int playerOrder) {
        this.playerType = PlayerType.valueOf(playerTypeStr.toUpperCase());
        this.hand = new Hand(CountingUpGame.Instance().deck);
        this.score = 0;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public Hand getHand() {
        return hand;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public void playCard(CountingUpGame game) {
    }

    public enum PlayerType {
        HUMAN, RANDOM, BASIC, CLEVER

    }
}
