package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomOption implements Option {

  String name = "RandomOption";
  List<Board> initiationSet = new ArrayList<>();

  Random rand = new Random();


  public RandomOption() {
    Board fullBoardMask = new Board("FullBoard", true);
    fullBoardMask.mask = 0xffffffffffffffffL;
    initiationSet.add(fullBoardMask);
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
    return true;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    return possibleMoves.get(rand.nextInt(possibleMoves.size()));
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
