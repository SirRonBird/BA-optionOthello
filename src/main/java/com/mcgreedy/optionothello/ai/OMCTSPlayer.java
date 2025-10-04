package com.mcgreedy.optionothello.ai;

import static com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR.BLACK;
import static com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR.WHITE;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.engine.MoveStatistics;
import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OMCTSPlayer extends Player {

  //Logger
  private static final Logger LOGGER = LogManager.getLogger(OMCTSPlayer.class);

  List<Option> options;
  List<Option> expandedOptions = new ArrayList<>(); //m
  Node currentNode; //s
  OMCTSSettings settings;

  Random rand = new Random();
  int nodeCounter=0;

  //MAST
  private Map<Move, Integer> mastVisits = new HashMap<>();
  private Map<Move, Double> mastValues = new HashMap<>();

  public OMCTSPlayer(PLAYER_COLOR color,
      PLAYER_TYPE type,
      Gamemanager gamemanager,
      OMCTSSettings settings
  ) {
    super(color, type, gamemanager);
    this.settings = settings;
    this.options = settings.optionList();
    this.currentNode = null;
    LOGGER.info("Created OMCTSPlayer with settings: {}", settings);
  }



  @Override
  public Move getMove(Board board) {
    Node root = new Node(null, null, board, color, -1);
    Node newNode = root;
    root.availableOptions.clear();
    nodeCounter = 0;

    //set searchtime
    long duration = 1000 + 1000 * searchTimeLimit;
    long startTime = System.currentTimeMillis();

    int simulations = 0;

    while (
        (searchTimeLimit >= 0 && System.currentTimeMillis() - startTime < duration) ||
            (simulationLimit > 0 && simulations <= simulationLimit)
    ) {
      currentNode = root;
      while (!stop(currentNode.board)) {
        expandedOptions.clear();
        currentNode.availableOptions.clear();
        //If option stops in state currentNode
        if (currentNode.hasFinishedOption()) {
          //availableOptions is set to all options with currentNode in I
          options.forEach(option -> {
            if (option.isBoardInInitiationSet(currentNode.board, color)) {
              currentNode.availableOptions.add(option);
            }
          });
        } else {
          //else no new option can be selected (availableOptions only contains the current option
          currentNode.availableOptions.clear();
          currentNode.availableOptions.add(currentNode.optionFollowed);
        }
        //m is set to the options chosen in the children of currentNode
        currentNode.children.forEach(child -> {
          if (child.optionFollowed != null) {
            expandedOptions.add(child.optionFollowed);
          }
        });

        //if options == availableOptions(m)
        if (new HashSet<>(expandedOptions).containsAll(currentNode.availableOptions)) {
          // new node gehts selected with uct -> currentNode
          currentNode = selectChild(currentNode.children);
          if (currentNode == null) {
            LOGGER.error("Something went wrong in child selection");
          }
        } else {
          // get random element from availableOptions - expanded Options -> w
          List<Option> notYetExploredOptions = new ArrayList<>(currentNode.availableOptions);
          notYetExploredOptions.removeAll(expandedOptions);
          Option w = randomElement(notYetExploredOptions);
          // get the action from getAction(w, board) -> a
          Move a = getAction(w, currentNode);
          // create child from currentNode with action a and add it to the childrenList -> s'
          Node childNode = expand(currentNode, a);
          // set the chosen option from child s' to w
          childNode.optionFollowed = w;
          currentNode.children.add(childNode);
          newNode=childNode;
          break;
        }
      }

      nodeCounter++;
      //rollout -> delta
      RolloutResult result = rollOut(newNode);
      backUp(newNode,result.value,result.movesInRollout, result.winner);

      simulations++;
    }

    Move bestMove =  getBestAction(root);
    if(bestMove.getStatistics() == null){
      bestMove.setStatistics(new MoveStatistics());
    }
    bestMove.getStatistics().setSearchTime(System.currentTimeMillis()-startTime);

    bestMove.getStatistics().setSearchDepth(findMaxDepth(root));
    bestMove.getStatistics().setSearchedNodes(countNodes(root));
    bestMove.setSearchDepth(findMaxDepth(root));
    bestMove.getStatistics().setOption(bestMove.getOption());
    /*try {
      exportTreeToDot(root, getBestNode(root));}
    catch (IOException e) {
      LOGGER.error("Failed to save tree as Dot: {}", e.getMessage());
    }*/

    return bestMove;
  }

  @Override
  public void resetMAST() {
    if(settings.useMast()){
      mastValues.clear();
      mastVisits.clear();
    }
  }

  public static int findMaxDepth(Node root) {
    if (root == null) return Integer.MIN_VALUE;

    int max = root.depth;

    for (Node child : root.children) {
      int childMax = findMaxDepth(child);
      if (childMax > max) {
        max = childMax;
      }
    }

    return max;
  }

  public static int countNodes(Node root) {
    if (root == null) return 0;

    int count = 0;
    Queue<Node> queue = new LinkedList<>();
    queue.add(root);

    while (!queue.isEmpty()){
      Node current = queue.poll();
      count++;
      queue.addAll(current.children);
    }

    return count;
  }

  private Move getBestAction(Node state) {
    //get the child with the best value

    if(state.children == null || state.children.isEmpty()){
      //return passmove
      return new Move(
          state.color,
         -1,
          state.depth,
          PLAYER_TYPE.O_MCTS
      );
    }
    LOGGER.info("Childs: {}", state.children);
    /*Node selectedChild = state.children.stream().max(Comparator.comparingDouble(
        c -> c.value / (c.visits + 1e-6)
    )).orElse(state.children.get(rand.nextInt(state.children.size())));*/
    /*Node selectedChild = state.children.stream().max(Comparator.comparingDouble(
        c -> c.visits
    )).orElse(state.children.get(rand.nextInt(state.children.size())));*/
    Node selectedChild = state.children.stream().max(Comparator.comparingDouble(
        c -> (double) c.wins/c.visits
    )).orElse(state.children.get(rand.nextInt(state.children.size())));
    Move bestMove = selectedChild.move;
    bestMove.setOption(selectedChild.optionFollowed);
    LOGGER.info("Selected Move: {}", bestMove.getPosition());
    return bestMove;
  }

  private Node selectChild(List<Node> children) {
    //LOGGER.info("Child selection");

    //return children.stream().max(Comparator.comparingDouble(OMCTSPlayer::uctValue)).orElse(children.get(rand.nextInt(children.size())));
    return children.stream().max((c1,c2) -> Double.compare(uct(c1), uct(c2)))
        .orElse(children.get(rand.nextInt(children.size())));
  }

  private double uct(Node child){
    // Q-Wert = kumulativer Reward / Besuche
    double qValue = child.wins / (child.visits + 1e-6);

    // Standard UCT-Exploration
    double exploration = settings.explorationConstant() *
        Math.sqrt(2 * Math.log((double) child.parent.visits + 1) / (child.visits + 1e-6));

    if (settings.useRave() && child.move != null) {
      // RAVE-Wert = kumulativer AMA-Faktor / Anzahl der AMA-Besuche
      int raveN = child.raveVisits.getOrDefault(child.move, 0);
      double raveW = child.raveValues.getOrDefault(child.move, 0.0);
      double amaf = (raveN > 0) ? raveW / raveN : 0.0;

      // Beta-Mischung zwischen qValue und AMA-Faktor
      double k = settings.k();
      double beta = Math.sqrt(k / (3.0 * child.visits + k));

      double mixedValue = (1 - beta) * qValue + beta * amaf;
      return mixedValue + exploration;
    }

    return qValue + exploration;
  }

  private boolean stop(Board board) {
    return board.isGameOver();
  }

  private Option randomElement(List<Option> options) {

    //LOGGER.info("{} Options are available", options.size());
    return options.get(rand.nextInt(options.size()));
  }

  private Move getAction(Option option, Node state) {
    //gets the best action from the state by option
    List<Move> possibleMoves = state.board.generateMovesAsList(state.color == WHITE,state.depth,PLAYER_TYPE.O_MCTS);
    if(possibleMoves.isEmpty()){
      return new Move(color, -1, state.depth, PLAYER_TYPE.O_MCTS);
    }
    return option.getBestMove(state.board,possibleMoves);
  }

  private Node expand(Node parent, Move move) {
    Board newBoard = parent.board.clone();
    newBoard.updateBoard(move.getPosition(), parent.color.equals(WHITE));
    //LOGGER.info("New Board: {} from {} ({}) with move {}", newBoard,parent.color, parent.board,move.getPosition());
    return new Node(parent, move,
        newBoard,
        parent.color == WHITE ? BLACK : WHITE,
        parent.depth+1);
  }

  private RolloutResult rollOut(Node start){
    int simulationDepth = start.depth +1;
    PLAYER_COLOR simColor = start.color;
    Node currentSimNode = start;
    List<Move> movesInRollout = new ArrayList<>();

    while(!currentSimNode.board.isGameOver()){
      List<Move> moves = currentSimNode.board.generateMovesAsList(simColor == WHITE, simulationDepth, PLAYER_TYPE.O_MCTS);
      Move chosenMove;

      if (moves.isEmpty()) {
        chosenMove = new Move(simColor, -1, simulationDepth, PLAYER_TYPE.O_MCTS);
      } else if (currentSimNode.hasFinishedOption()) {
        if(settings.useMast()){
          //select Move with MAST
          chosenMove = selectMoveWithMAST(moves);
        } else {
          chosenMove = moves.get(rand.nextInt(moves.size()));
        }
      } else {
        chosenMove = getAction(currentSimNode.optionFollowed, currentSimNode);
      }

      movesInRollout.add(chosenMove);
      currentSimNode = expand(currentSimNode, chosenMove);
      simColor = toggleColor(simColor);
      simulationDepth++;
    }
    PLAYER_COLOR winner = currentSimNode.board.getWinner();

    //MAST-Update
    if(settings.useMast()) {
      for (Move m : movesInRollout) {
        mastVisits.put(m, mastVisits.getOrDefault(m, 0) + 1);
        double reward = winner == this.color ? 1.0 : -1.0;
        mastValues.put(m, mastValues.getOrDefault(m, 0.0) + reward);
      }
    }
    return new RolloutResult(currentSimNode.board.getValue(start.color == WHITE),
        movesInRollout, winner);
  }

  private Move selectMoveWithMAST(List<Move> moves) {
    double tau = settings.tau(); // Temperatur
    double sum = 0.0;
    double[] scores = new double[moves.size()];

    for (int i = 0; i < moves.size(); i++) {
      Move m = moves.get(i);
      double q = mastValues.getOrDefault(m, 0.0) / (mastVisits.getOrDefault(m, 1));
      scores[i] = Math.exp(q / tau);
      sum += scores[i];
    }

    double r = Math.random() * sum;
    for (int i = 0; i < moves.size(); i++) {
      r -= scores[i];
      if (r <= 0) return moves.get(i);
    }

    return moves.get(moves.size() - 1);
  }

  private void backUp(Node start, int delta, List<Move> movesInRollout, PLAYER_COLOR winner) {
    int dSprime = start.depth;

    for (Node n = start; n != null; n = n.parent) {
      n.visits++;

      // ±1 Reward wie im MCTS
      if (winner == this.color) {
        n.wins += 1;
      } else {
        n.wins -= 1;
      }

      // Diskontierter Value (spezifisch für OMCTS)
      int depthDiff = dSprime - n.depth;
      double discountedDelta = delta * Math.pow(settings.discountFactor(), depthDiff);
      n.value += discountedDelta;

      // RAVE-Update auch mit ±1 Reward
      if (settings.useRave()) {
        for (Move m : movesInRollout) {
          n.raveVisits.put(m, n.raveVisits.getOrDefault(m, 0) + 1);

          if (winner == this.color) {
            n.raveValues.put(m, n.raveValues.getOrDefault(m, 0.0) + 1.0);
          } else {
            n.raveValues.put(m, n.raveValues.getOrDefault(m, 0.0) - 1.0);
          }
        }
      }
    }
  }

  private PLAYER_COLOR toggleColor(PLAYER_COLOR color) {
    return (color == WHITE) ? BLACK : WHITE;
  }

  @Override
  public String toString() {
    return "OMCTSPlayer{" +
        "options=" + options +
        ", color=" + color +
        ", type=" + type +
        '}';
  }

  private static class Node {

    Node parent;
    int number;

    List<Node> children; //c_s
    Option optionFollowed; //o_s

    List<Option> availableOptions; //p_s

    Move move;
    Board board;
    PLAYER_COLOR color;

    int depth =0;
    int visits;
    double value;
    int wins = 0;

    // RAVE Statistiken
    Map<Move, Integer> raveVisits = new HashMap<>();
    Map<Move, Double> raveValues = new HashMap<>();

    Node(Node parent, Move move, Board board, PLAYER_COLOR color, int depth) {
      this.parent = parent;
      this.move = move;
      this.board = board;
      this.color = color;
      this.depth = depth;
      this.optionFollowed = null;
      this.children = new ArrayList<>();
      this.availableOptions = new ArrayList<>();
    }

    public void setNumber(int number) {
      this.number = number;
    }

    public boolean hasFinishedOption() {
      //LOGGER.debug("option Followed: {}", this.optionFollowed);
      if (this.optionFollowed == null) {
        return true;
      } else {
        return this.optionFollowed.shouldTerminate(board,color);
      }
    }


    @Override
    public String toString() {
      return "{ Node (" + move.getPosition() + ") [" + optionFollowed.getName() + "] " + value + ":" + wins + ":" + visits + "}\n";
    }
  }

  private static class RolloutResult {
    int value;
    List<Move> movesInRollout;
    PLAYER_COLOR winner;


    RolloutResult(int value, List<Move> movesInRollout, PLAYER_COLOR winner) {
      this.value = value;
      this.movesInRollout = movesInRollout;
      this.winner = winner;
    }
  }

}
