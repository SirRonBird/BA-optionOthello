package com.mcgreedy.optionothello.ai;

import static com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR.BLACK;
import static com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR.WHITE;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.engine.MoveStatistics;
import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.gamemanagement.Player;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
  static double explorationConstant;


  Random rand = new Random();

  int nodeCounter=0;

  public OMCTSPlayer(PLAYER_COLOR color,
      PLAYER_TYPE type,
      Gamemanager gamemanager,
      OMCTSSettings settings
  ) {
    super(color, type, gamemanager);
    this.settings = settings;
    this.options = settings.optionList();
    explorationConstant = settings.explorationConstant();
    this.currentNode = null;
    LOGGER.info("OMCTS player created: {}", this);
  }

  @Override
  public Move getMove(Board board) {
    Node root = new Node(null, null, board, color, -1);
    Node newNode = new Node(null,null,new Board(), color, -2);
    root.availableOptions.clear();
    nodeCounter = 0;

    //set searchtime
    long duration = 1000 + 1000 * searchTimeLimit;
    long startTime = System.currentTimeMillis();

    while (System.currentTimeMillis() - startTime < duration) {
      currentNode = root;
      while (!stop(currentNode.board)) {
        //LOGGER.info("Current Node: {}", currentNode.depth);
        expandedOptions.clear();
        currentNode.availableOptions.clear();
        //If option stops in state currentNode
        if (currentNode.hasFinishedOption()) {
          //availableOptions is set to all options with currentNode in I
          //LOGGER.info("No active Option");
          options.forEach(option -> {
            /*LOGGER.info("Check board {} if it fits into the Options initSet", currentNode.board);*/
            if (option.isBoardInInitiationSet(currentNode.board, color)) {
              /*LOGGER.info("Current Board fits into the Options initSet");*/
              currentNode.availableOptions.add(option);
             /* LOGGER.info("availableOptions: {}", currentNode.availableOptions);*/
            }
          });
        } else {
          //LOGGER.info("Current Node {} has not finished Options!", currentNode.depth);
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

        LOGGER.info("Expanded Options: {}; Available Option: {}; Options {};", expandedOptions.size(), currentNode.availableOptions.size(), options.size());

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
          //LOGGER.info("Move {} was selected", a);
          // create child from currentNode with action a and add it to the childrenList -> s'
          Node childNode = expand(currentNode, a);
          // set the chosen option from child s' to w
          childNode.optionFollowed = w;

          //LOGGER.info("New child: {}, Board: {}",childNode.depth,childNode.board);
          currentNode.children.add(childNode);
          //LOGGER.info("Option followed in new Node: {}", newNode.optionFollowed);
          newNode=childNode;
          break;
        }
      }
      //rollout -> delta
      nodeCounter++;
      int delta = rollOut(newNode);
      //backup delta to parent nodes
      backUp(newNode, delta);
    }
    //LOGGER.info("Max depth: {}", findMaxDepth(root));
    Move bestMove =  getBestAction(root);
    if(bestMove.getStatistics() == null){
      bestMove.setStatistics(new MoveStatistics());
    }
    bestMove.getStatistics().setSearchTime(System.currentTimeMillis()-startTime);

    bestMove.getStatistics().setSearchDepth(findMaxDepth(root));
    bestMove.getStatistics().setSearchedNodes(countNodes(root));
    bestMove.setSearchDepth(findMaxDepth(root));
    bestMove.getStatistics().setOption(bestMove.getOption());

    return bestMove;
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
    Node selectedChild = state.children.stream().max(Comparator.comparingDouble(
        c -> c.value
    )).orElse(state.children.get(rand.nextInt(state.children.size())));
    Move bestMove = selectedChild.move;
    bestMove.setOption(selectedChild.optionFollowed);
    return bestMove;
  }

  private Node selectChild(List<Node> children) {
    //LOGGER.info("Child selection");
    return children.stream().max(Comparator.comparingDouble(OMCTSPlayer::uctValue)).orElse(children.get(rand.nextInt(children.size())));
  }

  private static double uctValue(Node child) {
    double exploration = explorationConstant * Math.sqrt((2 * Math.log(child.parent.visits)) / child.visits);
    return child.value + exploration;
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

  private int rollOut(Node start) {
    //simulate until stop (end of game or max search depth is reached)
    //LOGGER.info("Rollout from {} with board", start.depth);
    int simulationDepth = start.depth + 1;
    PLAYER_COLOR simColor = start.color;

    Node currentSimNode =start;

    while (!currentSimNode.board.isGameOver()){
      List<Move> moves = currentSimNode.board.generateMovesAsList(simColor == WHITE, simulationDepth,PLAYER_TYPE.O_MCTS);

      if (moves.isEmpty()) {
        //No moves possible need to pass
        Move passMove = new Move(simColor, -1, simulationDepth, PLAYER_TYPE.O_MCTS);
        currentSimNode = expand(currentSimNode, passMove);
        simColor = toggleColor(simColor);
        continue;
      }
      if(currentSimNode.hasFinishedOption()){
        Move newMove = moves.get(rand.nextInt(moves.size()));
        currentSimNode = expand(currentSimNode, newMove);
      } else {
        //get moves from option
        Move newMove = getAction(currentSimNode.optionFollowed, currentSimNode);
        currentSimNode = expand(currentSimNode, newMove);
        simColor = toggleColor(simColor);
      }
      simulationDepth++;
    }
    return currentSimNode.board.getValue(start.color == WHITE);
  }

  private PLAYER_COLOR toggleColor(PLAYER_COLOR color) {
    return (color == WHITE) ? BLACK : WHITE;
  }

  private void backUp(Node start, int delta) {

    for(Node n = start; n != null;n=n.parent ){
      n.visits++;

      if(n.color == color){
        n.value += delta + Math.pow(settings.discountFactor(),n.depth);
      } else {
        n.value -= delta + Math.pow(settings.discountFactor(),n.depth);
      }

    }
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
      LOGGER.info("option Followed: {}", this.optionFollowed);
      if (this.optionFollowed == null) {
        return true;
      } else {
        return this.optionFollowed.shouldTerminate(board,color);
      }
    }


    @Override
    public String toString() {
      /*return "Node{\n" +
          *//*", optionFollowed=" + optionFollowed +*//*
          ", availableOptions=" + availableOptions.size() +
          "\n, move=" + move +
          "\n, board=" + board +
          "\n, color=" + color +
          "\n, depth=" + depth +
          "\n, value=" + value +
          "\nchildren=" + children +
          "\n }";*/
      return "[ Node (" + number + ") [" + depth + "]\n" + children;
    }
  }

}
