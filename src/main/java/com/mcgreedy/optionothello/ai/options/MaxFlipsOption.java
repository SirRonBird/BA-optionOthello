package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Option to select the move which flips the most enemy disks
 */
public class MaxFlipsOption implements Option {

  private static final String NAME = "MaxFlipsOption";

  private static final Random rand  = new Random();

  @Override public boolean isBoardInInitiationSet(Board b, Constants.PLAYER_COLOR c) { return true; }
  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board b, Constants.PLAYER_COLOR c) { return true; }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    Constants.PLAYER_COLOR me = possibleMoves.getFirst().getColor();
    Collections.shuffle(possibleMoves);
    Move bestMove = null;
    int mostFlips = Integer.MIN_VALUE;
    for(Move move : possibleMoves) {
      int moveFlips = flipDelta(board,move,me);
      if(moveFlips > mostFlips) {
        mostFlips = moveFlips;
        bestMove = move;
      }
    }
    if(bestMove != null) {
      return bestMove;
    } else {
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
  }

  private int flipDelta(Board b, Move m, Constants.PLAYER_COLOR me) {
    int before = (me == Constants.PLAYER_COLOR.WHITE) ? b.getWhiteCount() : b.getBlackCount();
    Board c = b.clone();
    c.updateBoard(m.getPosition(), me == Constants.PLAYER_COLOR.WHITE);
    int after  = (me == Constants.PLAYER_COLOR.WHITE) ? c.getWhiteCount() : c.getBlackCount();
    return after - before;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String toString() {
    return NAME;
  }
}
