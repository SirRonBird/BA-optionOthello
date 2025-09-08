package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.List;
import java.util.Random;

public class MaximizeMobilityOption implements Option {

  private static final String NAME = "MaximizeMobilityOption";

  private static final Random rand  = new Random();

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    return true;
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    return true;
    /*return board.generateAllPossibleMoves(playerColor == PLAYER_COLOR.WHITE) == 0L
        || board.isGameOver();*/
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    LOGGER.info(NAME);
    PLAYER_COLOR me = possibleMoves.getFirst().getColor();
    PLAYER_COLOR opponent = me == PLAYER_COLOR.WHITE ? PLAYER_COLOR.BLACK : PLAYER_COLOR.WHITE;
    Move bestMove = null;
    int bestMinMobility = Integer.MIN_VALUE;

    for (Move myMove : possibleMoves) {
      Board afterMyMove = board.clone();
      afterMyMove.updateBoard(myMove.getPosition(), me == PLAYER_COLOR.WHITE);

      long oppMovesBB = afterMyMove.generateAllPossibleMoves(opponent == PLAYER_COLOR.WHITE);
      if (oppMovesBB == 0L) {
        int mobility = countMyMobility(afterMyMove, me);
        if (mobility > bestMinMobility) {
          bestMinMobility = mobility;
          bestMove = myMove;
        }
        continue;
      }

      List<Move> oppMoves = afterMyMove.generateMovesAsList(opponent == PLAYER_COLOR.WHITE, 0, myMove.getPlayerType());
      int worstMobility = Integer.MAX_VALUE;
      for (Move oppMove : oppMoves) {
        Board afterOppMove = afterMyMove.clone();
        afterOppMove.updateBoard(oppMove.getPosition(), opponent == PLAYER_COLOR.WHITE);
        int mobility = countMyMobility(afterOppMove, me);
        worstMobility = Math.min(worstMobility, mobility);
        if (worstMobility <= bestMinMobility) break;
      }
      if (worstMobility > bestMinMobility) {
        bestMinMobility = worstMobility;
        bestMove = myMove;
      }
    }

    if (bestMove == null) {
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
    return bestMove;
  }

  private int countMyMobility(Board board, PLAYER_COLOR me) {
    long bb = board.generateAllPossibleMoves(me == PLAYER_COLOR.WHITE);
    return Long.bitCount(bb);
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
