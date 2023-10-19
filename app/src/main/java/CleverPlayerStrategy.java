import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CleverPlayerStrategy implements IPlayerStrategy, IObserver{
    HashMap<Player, ArrayList<Card>> cardsMemory = new HashMap<>();
    CleverPlayerStrategy(){
        CountingUpGame.Instance().addObserver(this);
    }
    @Override
    public Card PickCardToPlay(Player p) {
        return null;
    }

    @Override
    public void response(IObserverable subject) {
        if(cardsMemory.containsKey(CountingUpGame.Instance().getNextPlayer())){
            cardsMemory.get(CountingUpGame.Instance().getNextPlayer()).add(CountingUpGame.Instance().getSelectedCard());
        }else{
            cardsMemory.put(CountingUpGame.Instance().getNextPlayer(), new ArrayList<Card>());
            cardsMemory.get(CountingUpGame.Instance().getNextPlayer()).add(CountingUpGame.Instance().getSelectedCard());
        }

        System.out.println("tongji" + Runtime.getRuntime());
        for (Player p : cardsMemory.keySet()) {
            System.out.println("Player: " + p.getPlayerType());
            System.out.println("Cards: " + cardsMemory.get(p));
            System.out.println();
        }
    }
}
