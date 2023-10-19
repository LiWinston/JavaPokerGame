import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.Random;

public class BasicPlayerStrategy implements IPlayerStrategy {

    @Override
    public Card PickCardToPlay(Player p) {
        Card sameSuitGreaterRank = null;
        Card difSuitEqualRank = null;
        Card basicCard = null;
        ArrayList<Card> tempList = new ArrayList<>(p.hand.getCardList());

        for (Card card : tempList) {
            if(card.getSuit() == CountingUpGame.Instance().getLastPlayedCard().getSuit()){
                
            }
//            if(){
//                basicCard = sameSuitGreaterRank;
//            }else{
//                basicCard = difSuitEqualRank;
//            }
        }

        return basicCard;
    }
}
