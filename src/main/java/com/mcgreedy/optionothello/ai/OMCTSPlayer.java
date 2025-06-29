package com.mcgreedy.optionothello.ai;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.gamemanagement.Player;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OMCTSPlayer extends Player {

  //Logger
  private static final Logger LOGGER = LogManager.getLogger(OMCTSPlayer.class);

  List<Option> options;
  List<Option> availableOptions = new ArrayList<>(); //m
  Node currentNode; //s

  int maxDepth;

  Random rand = new Random();

  public OMCTSPlayer(PLAYER_COLOR color,
      PLAYER_TYPE type,
      Gamemanager gamemanager,
      List<Option> optionList
  ) {
    super(color, type, gamemanager);
    this.options = optionList;
    this.currentNode = null;
    this.maxDepth = 100;
    LOGGER.info("OMCTS player created: {}", this);
  }

  @Override
  public Move getMove(Board board) {
    Node root = new Node(null, null, board, color, 0);
    currentNode = root;
    while (!stop(currentNode.board)) {
      //If option stops in state currentNode
      if (currentNode.hasFinishedOption()) {
        //availableOptions is set to all options with currentNode in I
        options.forEach(option -> {
          LOGGER.info("Check board {} if it fits into the Options initSet", currentNode.board);
          if (option.isBoardInInitiationSet(currentNode.board, color)) {
            LOGGER.info("Current Board fits into the Options initSet");
            currentNode.availableOptions.add(option);
            LOGGER.info("availableOptions: {}", currentNode.availableOptions);
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
          availableOptions.add(child.optionFollowed);
        }
      });

      //if options == availableOptions(m)
      if (new HashSet<>(availableOptions).containsAll(currentNode.availableOptions)) {
        // new node gehts selected with uct -> currentNode
        currentNode = selectChild(currentNode.children);
      } else {
        // get random element from availableOptions - expanded Options -> w
        List<Option> notYetExploredOptions = new ArrayList<>(currentNode.availableOptions);
        notYetExploredOptions.removeAll(availableOptions);
        Option w = randomElement(notYetExploredOptions);
        // get the action from getAction(w, board) -> a
        Move a = getAction(w, currentNode);
        LOGGER.info("Move {} was selected", a);
        // create child from currentNode with action a and add it to the childrenList -> s'
        Node newNode = expand(currentNode, a);
        currentNode.children.add(newNode);
        // set the chosen option from child s' to w
        newNode.optionFollowed = w;
        break;
      }
    }
    //rollout -> delta
    int delta = rollOut(currentNode);
    //backup delta to parent nodes
    backUp(currentNode, delta);
    return getBestAction(root);
  }

  private Move getBestAction(Node state) {
    //get the child with the best value
    //return the corresponding move
    LOGGER.info("Children: {}", state.children);
    return state.children.getLast().move;
  }

  private Node selectChild(List<Node> children) {
    //TODO: UCT
    return children.getLast();
  }

  private boolean stop(Board board) {
    return board.isGameOver();
  }

  private Option randomElement(List<Option> options) {

    LOGGER.info("{} Options are available", options.size());
    return options.get(rand.nextInt(options.size()));
  }

  private Move getAction(Option option, Node state) {
    //gets the best action from the state by option
    List<Move> possibleMoves = state.board.generateMovesAsList(color.equals(PLAYER_COLOR.WHITE), -2,
        PLAYER_TYPE.O_MCTS);
    LOGGER.info("Get best Move with policy:\n {} \n for Moves {} and Board {} in color {}", option.policy, possibleMoves, state.board, state.color);
    if(possibleMoves.isEmpty()){
      return new Move(state.color,-1,-2,PLAYER_TYPE.O_MCTS);
    }
    List<Move> bestMoves = new ArrayList<>();
    double bestValue = Double.NEGATIVE_INFINITY;
    for (Move move : possibleMoves) {
      double value = option.executePolicy(state.board, move);
      if (value > bestValue) {
        bestValue = value;
        bestMoves.clear();
        bestMoves.add(move);
      } else if (value == bestValue) {
        bestMoves.add(move);
      }
    }
    return bestMoves.get(rand.nextInt(bestMoves.size()));
  }

  private Node expand(Node parent, Move move) {
    Board newBoard = parent.board.clone();
    newBoard.updateBoard(move.getPosition(), color.equals(PLAYER_COLOR.WHITE));
    return new Node(parent, move,
        newBoard,
        parent.color == PLAYER_COLOR.WHITE ? PLAYER_COLOR.BLACK : PLAYER_COLOR.WHITE,
        0);
  }

  private int rollOut(Node start) {
    //simulate until stop (end of game or max search depth is reached)

    return 0;
  }

  private void backUp(Node start, int delta) {

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

    List<Node> children; //c_s
    Option optionFollowed; //o_s

    List<Option> availableOptions; //p_s

    Move move;
    Board board;
    PLAYER_COLOR color;

    int depth;

    Node(Node parent, Move move, Board board, PLAYER_COLOR color, int depth) {
      this.parent = parent;
      this.move = move;
      this.board = board;
      this.color = color;
      this.depth = depth;
      this.optionFollowed = null;
      this.children = new ArrayList<Node>();
      this.availableOptions = new ArrayList<>();
    }


    public boolean hasFinishedOption() {
      if (this.optionFollowed == null) {
        return true;
      } else {
        return this.optionFollowed.checkTermination(board);
      }
    }
  }

}
