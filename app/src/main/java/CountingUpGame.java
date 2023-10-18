// CountingUpGame.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class CountingUpGame extends CardGame {

    private static CountingUpGame instance; // 单例实例
    static public final int seed = 30008;
    public final int nbPlayers = 4;
    public final int nbStartCards = 13;
    public final int nbRounds = 3;
//    private List<List<String>> playerAutoMovements = new ArrayList<>();
    final String[] trumpImage = {"bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif"};
    //new in-----------------------------------------------------------------------------------------------------------------------
    private final String version = "1.0";
    private final int handWidth = 400;
    private final int trickWidth = 40;
    public final Deck deck;
    private final Location[] handLocations = {new Location(350, 625), new Location(75, 350), new Location(350, 75), new Location(625, 350)};
    private final Location trickLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    public Logger logger = new Logger();
    public Score score = new Score(this);
    public boolean isWaitingForPass = false;
    public boolean passSelected = false;
    private Properties properties;
    // new in -----------------------------------------------------------------------------------------------------------------------
    public CardDealer dealer = new CardDealer(properties);
    public PlayerController controller = new PlayerController(this, properties);
    private final StringBuilder logResult = new StringBuilder();
    private int thinkingTime = 2000;
    private int delayTime = 600;
    Hand[] hands;
    private final Location hideLocation = new Location(-500, -500);
    private final int[] scores = new int[nbPlayers];
    Player[] players;
    private final int[] autoIndexHands = new int[nbPlayers];
    private boolean isAuto = false;
    private Card selected;
    private Card lastPlayedCard = null;
    public CountingUpGame(Properties properties) {
        super(700, 700, 30);
        this.properties = properties;
        this.dealer = new CardDealer(properties);
        this.logger = new Logger();
        this.score = new Score(this);
        this.controller = new PlayerController(this, properties);
        isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));
        thinkingTime = Integer.parseInt(properties.getProperty("thinkingTime", "200"));
        delayTime = Integer.parseInt(properties.getProperty("delayTime", "100"));
        deck = new Deck(Suit.values(), Rank.values(), "cover");

        instance = this;
    }

    public static CountingUpGame Instance() {
        return instance;
    }

    public String runApp() {
        setTitle("CountingUpGame (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatus("Initializing...");
        initPlayers();
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
            winText = "Game over. Winner is player: " + winners.iterator().next();
        } else {
            winText = "Game Over. Drawn winners are players: " + String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatus(winText);
        refresh();
        logger.addEndOfGameToLog(winners);

        return logResult.toString();
    }

    private void initPlayers() {
        players = new Player[nbPlayers];
        for (int i = 0; i < 4; i++) {
            String playerKey = "players." + i;
            String playerTypeStr = properties.getProperty(playerKey);
            players[i] = new Player(playerTypeStr, i);
        }
        hands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = players[i].getHand();
        }
    }

    public void setStatus(String string) {
        setStatusText(string);
    }

    private void initGame() {
//        hands = new Hand[nbPlayers];
        dealer.dealingOut();
        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, false);
        }
        // Set up human player for interaction
        CardListener cardListener = new CardAdapter() {
            public void leftDoubleClicked(Card card) {
                if (isValidCardToPlay(card)) {
                    selected = card;
                    hands[0].setTouchEnabled(false);
                } else {
                    setStatus("Invalid card. Please select a valid card to play.");
                }
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

    private int playerIndexWithAceClub() {
        for (int i = 0; i < nbPlayers; i++) {
            Hand hand = hands[i];
            List<Card> cards = hand.getCardsWithRank(Rank.ACE);
            if (cards.size() == 0) {
                continue;
            }
            for (Card card : cards) {
                if (card.getSuit() == Suit.CLUBS) {
                    return i;
                }
            }
        }
        return 0;
    }



    public boolean isValidCardToPlay(Card card) {
        if (lastPlayedCard == null) return true;

        if (card.getSuit() == lastPlayedCard.getSuit()) {
            return Rank.isRankGreater(card, lastPlayedCard);
        } else return card.getRank() == lastPlayedCard.getRank();
    }

    private void playGame() {
        boolean isFirstTurn = true;

        // End trump suit
        Hand playingArea = null;
        int winner = 0;
        int roundNumber = 1;
        for (int i = 0; i < nbPlayers; i++) score.updateScore(i);
        boolean isContinue = true;
        int skipCount = 0;
        List<Card> cardsPlayed = new ArrayList<>();
        playingArea = new Hand(deck);
        logger.addRoundInfoToLog(roundNumber);

        int nextPlayer = playerIndexWithAceClub();
        while (isContinue) {
            selected = null;
            boolean finishedAuto = false;
            if (nextPlayer == playerIndexWithAceClub() && isFirstTurn) {
                selected = dealer.getCardFromList(hands[nextPlayer].getCardList(), "1C");
                if (selected != null) {
                    selected.transfer(playingArea, true);
                    cardsPlayed.add(selected);
                    isFirstTurn = false;
                    continue;
                }
            }

            if (isAuto) {
                if (0 == nextPlayer) {
                    hands[0].setTouchEnabled(true);
                    isWaitingForPass = true;
                    passSelected = false;
                    setStatus("Player 0 double-click on card to follow or press Enter to pass");
                    while (null == selected && !passSelected) delay(delayTime);
                    isWaitingForPass = false;
                } else {
                    setStatus("Player " + nextPlayer + " thinking...");
                    delay(thinkingTime);
                    do {
                        selected = players[nextPlayer].getRandomCardOrSkip(hands[nextPlayer].getCardList());
                    } while (selected != null && !isValidCardToPlay(selected)); // Ensure the selected card is valid

                    if (selected == null) {
                        setStatus("Player " + nextPlayer + " skipping...");
                        delay(thinkingTime);
                    }
                }
            }

            if (!isAuto || finishedAuto) {
                if (0 == nextPlayer) {
                    hands[0].setTouchEnabled(true);
                    isWaitingForPass = true;
                    passSelected = false;
                    setStatus("Player 0 double-click on card to follow or press Enter to pass");
                    while (null == selected && !passSelected) delay(delayTime);
                    isWaitingForPass = false;
                } else {
                    setStatus("Player " + nextPlayer + " thinking...");
                    delay(thinkingTime);
                    selected = players[nextPlayer].getRandomCardOrSkip(hands[nextPlayer].getCardList());
                    if (selected == null) {
                        setStatus("Player " + nextPlayer + " skipping...");
                        delay(thinkingTime);
                    }
                }
            }

            // Follow with selected card

            playingArea.setView(this, new RowLayout(trickLocation, (playingArea.getNumberOfCards() + 2) * trickWidth));
            playingArea.draw();
            logger.addCardPlayedToLog(nextPlayer, selected);
            if (selected != null) {
                lastPlayedCard = selected;
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

            isContinue = hands[0].getNumberOfCards() > 0 && hands[1].getNumberOfCards() > 0 && hands[2].getNumberOfCards() > 0 && hands[3].getNumberOfCards() > 0;
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
}

