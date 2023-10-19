// Player.java

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {
    private PlayerType playerType;
    protected Hand hand;
    protected int score;
    protected List<Card> playedCards;

        IPlayerStrategy playerStrategy;

    public Player(String playerTypeStr) {
        this.playerType = PlayerType.valueOf(playerTypeStr.toUpperCase());
        this.hand = new Hand(CountingUpGame.Instance().deck);
        this.score = 0;
        playerStrategy = PlayerStrategyFactory.getInstance().getMyStrategy(this);
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

    public Card PickCardToPlay() {
        return playerStrategy.PickCardToPlay(this);
    }
//
//    public Card getRandomCardOrSkip(ArrayList<Card> list) {
//        ArrayList<Card> tempList = new ArrayList<>(list);
//
//        while (!tempList.isEmpty()) {
//            Random random = new Random();
//            int x = random.nextInt(tempList.size());
//            Card selectedCard = tempList.get(x);
//
//            if (CountingUpGame.Instance().isValidCardToPlay(selectedCard)) {
//                return selectedCard;
//            } else {
//                tempList.remove(x);
//            }
//        }
//
//        return null;
//    }

    public enum PlayerType {
        HUMAN, RANDOM, BASIC, CLEVER

    }

}
