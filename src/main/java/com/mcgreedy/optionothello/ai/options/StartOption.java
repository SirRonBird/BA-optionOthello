package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StartOption implements Option {

  Board startMask = new Board("Start", true);

  Board fullCenterMask = new Board("Full Center", true);

  List<Board> initiationSet = new ArrayList<>();

  String name = "StartOption";

  Random rand = new Random();

  List<Integer> innerRingPositions = new ArrayList<>(Arrays.asList(
      18, 19, 20, 21,
      26, 34,
      29, 37,
      42, 43, 44, 45
  ));

  public StartOption() {
    startMask.mask = 0x1818000000L;
    fullCenterMask.mask = 0x3c3c3c3c0000L;
    initiationSet.add(startMask);
  }

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    boolean result = false;
    for (Board m : initiationSet) {
      result |= board.boardIsHittingMask(m, playerColor);
    }
    return result;
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    return board.boardIsHittingMask(fullCenterMask, playerColor);
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    List<Move> bestMoves = new ArrayList<>();
    for(Move move : possibleMoves) {
      if(innerRingPositions.contains(move.getPosition())){
        bestMoves.add(move);
      }
    }
    if(!bestMoves.isEmpty()){
      return bestMoves.get(rand.nextInt(bestMoves.size()));
    } else {
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString(){
    return name;
  }
}
