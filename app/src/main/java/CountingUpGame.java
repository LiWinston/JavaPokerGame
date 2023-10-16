// CountingUpGame.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class CountingUpGame extends CardGame  {



    final String trumpImage[] = {"bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif"};

    static public final int seed = 30008;
    static final Random random = new Random(seed);
    private Properties properties;
    private StringBuilder logResult = new StringBuilder();
    private List<List<String>> playerAutoMovements = new ArrayList<>();

    public boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }
// new in -----------------------------------------------------------------------------------------------------------------------
    public CardDealer dealer = new CardDealer(properties);
    public Logger logger = new Logger();

<<<<<<< Updated upstream
    public CardDealer dealer = new CardDealer(properties);

=======
    public Score score = new  Score(this);

    public PlayerController controller = new PlayerController(this,properties);
//new in-----------------------------------------------------------------------------------------------------------------------
>>>>>>> Stashed changes
    private final String version = "1.0";
    public final int nbPlayers = 4;
    public final int nbStartCards = 13;
    public final int nbRounds = 3;
    private final int handWidth = 400;
    private final int trickWidth = 40;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            // new Location(650, 575)
            new Location(575, 575)
    };
    private Actor[] scoreActors = {null, null, null, null};
    private final Location trickLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    private int thinkingTime = 2000;
    private int delayTime = 600;
    private Hand[] hands;
    private Location hideLocation = new Location(-500, -500);

    public void setStatus(String string) {
        setStatusText(string);
    }

    private int[] scores = new int[nbPlayers];

    private boolean isWaitingForPass = false;
    private boolean passSelected = false;
    private int[] autoIndexHands = new int [nbPlayers];
    private boolean isAuto = false;

    Font bigFont = new Font("Arial", Font.BOLD, 36);
    private Card selected;







    private void initGame() {
        hands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(deck);
        }
        dealer.dealingOut(hands, nbPlayers, nbStartCards);
        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, false);
        }
        // Set up human player for interaction
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) {
                selected = card;
                hands[0].setTouchEnabled(false);
            }
        };
        hands[0].addCardListener(cardListener);
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            // layouts[i].setStepDelay(10);
            hands[i].setView(this, layouts[i]);
            hands[i].setTargetArea(new TargetArea(trickLocation));
            hands[i].draw();
        }
    }


<<<<<<< Updated upstream
    // return random Enum value
//    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
//        int x = random.nextInt(clazz.getEnumConstants().length);
//        return clazz.getEnumConstants()[x];
//    }
//
//    // return random Card from ArrayList
//    public static Card randomCard(ArrayList<Card> list) {
//        int x = random.nextInt(list.size());
//        return list.get(x);
//    }
//
//    public static Card getRandomCardOrSkip(ArrayList<Card> list) {
//        int isSkip = random.nextInt(2);
//        if (isSkip == 1) {
//            return null;
//        }
//
//        int x = random.nextInt(list.size());
//        return list.get(x);
//    }

//    private Rank getRankFromString(String cardName) {
//        String rankString = cardName.substring(0, cardName.length() - 1);
//        Integer rankValue = Integer.parseInt(rankString);
//
//        for (Rank rank : Rank.values()) {
//            if (rank.getRankCardValue() == rankValue) {
//                return rank;
//            }
//        }
//
//        return Rank.ACE;
//    }
//
//    private Suit getSuitFromString(String cardName) {
//        String suitString = cardName.substring(cardName.length() - 1);
//
//        for (Suit suit : Suit.values()) {
//            if (suit.getSuitShortHand().equals(suitString)) {
//                return suit;
//            }
//        }
//        return Suit.CLUBS;
//    }
//
//    private Card getCardFromList(List<Card> cards, String cardName) {
//        Rank cardRank = getRankFromString(cardName);
//        Suit cardSuit = getSuitFromString(cardName);
//        for (Card card: cards) {
//            if (card.getSuit() == cardSuit
//                    && card.getRank() == cardRank) {
//                return card;
//            }
//        }
//
//        return null;
//    }

//    private void dealingOut(Hand[] hands, int nbPlayers, int nbCardsPerPlayer) {
//        Hand pack = deck.toHand(false);
//        int[] cardsDealtPerPlayer = new int[nbPlayers];
//
//        for (int i = 0; i < nbPlayers; i++) {
//            String initialCardsKey = "players." + i + ".initialcards";
//            String initialCardsValue = properties.getProperty(initialCardsKey);
//            if (initialCardsValue == null) {
//                continue;
//            }
//            String[] initialCards = initialCardsValue.split(",");
//            for (String initialCard: initialCards) {
//                if (initialCard.length() <= 1) {
//                    continue;
//                }
//                Card card = getCardFromList(pack.getCardList(), initialCard);
//                if (card != null) {
//                    card.removeFromHand(false);
//                    hands[i].insert(card, false);
//                }
//            }
//        }
//
//        for (int i = 0; i < nbPlayers; i++) {
//            int cardsToDealt = nbCardsPerPlayer - hands[i].getNumberOfCards();
//            for (int j = 0; j < cardsToDealt; j++) {
//                if (pack.isEmpty()) return;
//                Card dealt = randomCard(pack.getCardList());
//                dealt.removeFromHand(false);
//                hands[i].insert(dealt, false);
//            }
//        }
//    }

=======
>>>>>>> Stashed changes
    private int playerIndexWithAceClub() {
        for (int i = 0; i < nbPlayers; i++) {
            Hand hand = hands[i];
            List<Card> cards = hand.getCardsWithRank(Rank.ACE);
            if (cards.size() == 0) {
                continue;
            }
            for (Card card: cards) {
                if (card.getSuit() == Suit.CLUBS) {
                    return i;
                }
            }
        }

        return 0;
    }

    private void playGame() {
        // End trump suit
        Hand playingArea = null;
        int winner = 0;
        int roundNumber = 1;
        for (int i = 0; i < nbPlayers; i++) score.updateScore(i);
        boolean isContinue = true;
        int skipCount = 0;
        List<Card>cardsPlayed = new ArrayList<>();
        playingArea = new Hand(deck);
        logger.addRoundInfoToLog(roundNumber);

        int nextPlayer = playerIndexWithAceClub();
        while(isContinue) {
            selected = null;
            boolean finishedAuto = false;
            if (isAuto) {
                int nextPlayerAutoIndex = autoIndexHands[nextPlayer];
                List<String> nextPlayerMovement = playerAutoMovements.get(nextPlayer);
                String nextMovement = "";

                if (nextPlayerMovement.size() > nextPlayerAutoIndex) {
                    nextMovement = nextPlayerMovement.get(nextPlayerAutoIndex);
                    nextPlayerAutoIndex++;

                    autoIndexHands[nextPlayer] = nextPlayerAutoIndex;
                    Hand nextHand = hands[nextPlayer];

                    if (nextMovement.equals("SKIP")) {
                        setStatusText("Player " + nextPlayer + " skipping...");
                        delay(thinkingTime);
                        selected = null;
                    } else {
                        setStatusText("Player " + nextPlayer + " thinking...");
                        delay(thinkingTime);
                        selected = dealer.getCardFromList(nextHand.getCardList(), nextMovement);
                    }
                } else {
                    finishedAuto = true;
                }
            }

            if (!isAuto || finishedAuto){
                if (0 == nextPlayer) {
                    hands[0].setTouchEnabled(true);
                    isWaitingForPass = true;
                    passSelected = false;
                    setStatus("Player 0 double-click on card to follow or press Enter to pass");
                    while (null == selected && !passSelected) delay(delayTime);
                    isWaitingForPass = false;
                } else {
                    setStatusText("Player " + nextPlayer + " thinking...");
                    delay(thinkingTime);
                    selected = dealer.getRandomCardOrSkip(hands[nextPlayer].getCardList());
                    if (selected == null) {
                        setStatusText("Player " + nextPlayer + " skipping...");
                        delay(thinkingTime);
                    }
                }
            }

            // Follow with selected card

            playingArea.setView(this, new RowLayout(trickLocation, (playingArea.getNumberOfCards() + 2) * trickWidth));
            playingArea.draw();
            logger.addCardPlayedToLog(nextPlayer, selected);
            if (selected != null) {
                skipCount = 0;
                cardsPlayed.add(selected);
                selected.setVerso(false);  // In case it is upside down
                // Check: Following card must follow suit if possible

                // End Check
                selected.transfer(playingArea, true); // transfer to trick (includes graphic effect)
                delay(delayTime);
                // End Follow
            } else {
                skipCount++;
            }

            if (skipCount == nbPlayers - 1) {
                playingArea.setView(this, new RowLayout(hideLocation, 0));
                playingArea.draw();
                winner = (nextPlayer + 1) % nbPlayers;
                skipCount = 0;
                score.calculateScoreEndOfRound(winner, cardsPlayed);
                score.updateScore(winner);
                logger.addEndOfRoundToLog();
                roundNumber++;
                logger.addRoundInfoToLog(roundNumber);
                cardsPlayed = new ArrayList<>();
                delay(delayTime);
                playingArea = new Hand(deck);
            }

            isContinue = hands[0].getNumberOfCards() > 0 && hands[1].getNumberOfCards() > 0 &&
                    hands[2].getNumberOfCards() > 0 && hands[3].getNumberOfCards() > 0;
            if (!isContinue) {
                winner = nextPlayer;
                score.calculateScoreEndOfRound(winner, cardsPlayed);
                logger.addEndOfRoundToLog();
            } else {
                nextPlayer = (nextPlayer + 1) % nbPlayers;
            }
            delay(delayTime);
        }

        for (int i = 0; i < nbPlayers; i++) {
            score.calculateNegativeScoreEndOfGame(i, hands[i].getCardList());
            score.updateScore(i);
        }
    }



    public String runApp() {
        setTitle("CountingUpGame (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        score.initScores();
        score.initScore();
        addKeyListener(controller);
        controller.setupPlayerAutoMovements();
        initGame();
        playGame();

        for (int i = 0; i < nbPlayers; i++) score.updateScore(i);
        int maxScore = 0;
        for (int i = 0; i < nbPlayers; i++) if (scores[i] > maxScore) maxScore = scores[i];
        List<Integer> winners = new ArrayList<Integer>();
        for (int i = 0; i < nbPlayers; i++) if (scores[i] == maxScore) winners.add(i);
        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " +
                    winners.iterator().next();
        } else {
            winText = "Game Over. Drawn winners are players: " +
                    String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText(winText);
        refresh();
        logger.addEndOfGameToLog(winners);

        return logResult.toString();
    }

    public CountingUpGame(Properties properties) {
        super(700, 700, 30);
        this.properties = properties;
        this.dealer = new CardDealer(properties);
<<<<<<< Updated upstream
=======
        this.logger = new Logger();
        this.score  = new Score(this);
        this.controller=new PlayerController(this,properties);
>>>>>>> Stashed changes
        isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));
        thinkingTime = Integer.parseInt(properties.getProperty("thinkingTime", "200"));
        delayTime = Integer.parseInt(properties.getProperty("delayTime", "100"));
    }
}

