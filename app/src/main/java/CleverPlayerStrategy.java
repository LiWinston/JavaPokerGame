import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CleverPlayerStrategy implements IPlayerStrategy, IObserver{
    private static final int DEPTH_LIMIT = 3;
    HashMap<Player, ArrayList<Card>> cardsMemory = new HashMap<>();
    CleverPlayerStrategy(){
        CountingUpGame.Instance().addObserver(this);
    }
    @Override
    public Card PickCardToPlay(Player p) {
        List<Card> hand = new ArrayList<>(p.hand.getCardList());
        double bestScore = Double.NEGATIVE_INFINITY;
        Card bestCard = null;

        ArrayList<Card> mergedList = new ArrayList<>();
        for(List<Card> l : cardsMemory.values()) mergedList.addAll(l);
        for (Card card : hand) {
            List<Card> simulatedHand = new ArrayList<>(hand);
            simulatedHand.remove(card);
            List<Card> simulatedPlayedCards = new ArrayList<>(mergedList);  // assuming getLastPlayedCards() gives us a history
            simulatedPlayedCards.add(card);
            double currentScore = minimax(DEPTH_LIMIT, false, simulatedHand, simulatedPlayedCards);

            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestCard = card;
            }
        }
        return bestCard;
    }
    private double minimax(int depth, boolean isMaximizingPlayer, List<Card> hand, List<Card> playedCards) {
        if (depth == 0) {
            return evaluateHandScore(hand);
        }

        if (isMaximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for (Card card : hand) {
                List<Card> newHand = new ArrayList<>(hand);
                newHand.remove(card);
                List<Card> newPlayedCards = new ArrayList<>(playedCards);
                newPlayedCards.add(card);
                double eval = minimax(depth - 1, false, newHand, newPlayedCards);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for (Card card : hand) {
                List<Card> newHand = new ArrayList<>(hand);
                newHand.remove(card);
                List<Card> newPlayedCards = new ArrayList<>(playedCards);
                newPlayedCards.add(card);
                double eval = minimax(depth - 1, true, newHand, newPlayedCards);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }
    private double evaluateHandScore(List<Card> hand) {
        double score = 0;
        for (Card card : hand) {
            score += evaluateCardScore(card, hand);
        }
        return score;
    }
    private double evaluateCardScore(Card card, List<Card> hand) {
//        double score = 0.0;
//
//        // 基于牌的点数调整分数
//        int cardValue = card.getRank().getRankCardValue();
//        if (cardValue <= 10) {
//            score -= cardValue;
//        } else {
//            score -= 10;  // J, Q, K, A 都计为10分
//        }
//
//        // 根据其他玩家的行为调整分数
//        Suit cardSuit = (Suit) card.getSuit();
//        int countSameSuitPlayed = (int) playedCards.stream().filter(c -> c.getSuit() == cardSuit).count();
//        score += countSameSuitPlayed * 0.5;  // 被多次打出的花色可能更有价值
//
//        // 考虑赢得本轮的可能性
//        int highValueCards = (int) hand.stream().filter(c -> c.getValue() > 8 || c.getValue() == 1).count();  // 1 for Ace, and values > 8 for 10, J, Q, K
//        if (highValueCards > 3) {  // 如果手中有多张高点数的牌，增加牌的分数，因为玩家可能会尝试赢得本轮
//            score += 2.0;
//        }
//
//        return score;
        double score = 0;

        if (((Rank)card.getRank()).getRankCardValue() >= 10 || ((Rank)card.getRank()).getRankCardValue() == 1) {
            score += 10;
        } else {
            score += ((Rank)card.getRank()).getRankCardValue();
        }

        for (Player player : cardsMemory.keySet()) {
            ArrayList<Card> playedByPlayer = cardsMemory.get(player);
            for (Card playedCard : playedByPlayer) {
                if (playedCard.getSuit() == card.getSuit()) {
                    score += 0.5;
                }
            }
        }

        return score;
    }
    @Override
    public void response(IObserverable subject) {
        if(cardsMemory.containsKey(CountingUpGame.Instance().getNextPlayer())){
            cardsMemory.get(CountingUpGame.Instance().getNextPlayer()).add(CountingUpGame.Instance().getSelectedCard());
        }else{
            cardsMemory.put(CountingUpGame.Instance().getNextPlayer(), new ArrayList<Card>());
            cardsMemory.get(CountingUpGame.Instance().getNextPlayer()).add(CountingUpGame.Instance().getSelectedCard());
        }

//        System.out.println("tongji" + Runtime.getRuntime());
//        for (Player p : cardsMemory.keySet()) {
//            System.out.println("Player: " + p.getPlayerType());
//            System.out.println("Cards: " + cardsMemory.get(p));
//            System.out.println();
//        }
    }
}
