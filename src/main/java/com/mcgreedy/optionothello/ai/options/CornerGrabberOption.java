package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CornerGrabberOption implements Option {

  private static final long CORNERS = 0x8100000000000081L;
  private static final long X_SQUARES = 0x4200000000000042L;
  private static final long C_SQUARES = 0x2400000000000024L;

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    long empty = ~(board.getWhite() | board.getBlack());
    long candidateCorners = CORNERS & empty & ~(X_SQUARES | C_SQUARES);
    return candidateCorners != 0;
  }

  @Override
  public List<Board> initiationSet() { return new ArrayList<>(); }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    return !isBoardInInitiationSet(board, playerColor);
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    Move bestMove = null;
    int bestValue = Integer.MIN_VALUE;

    for (Move move : possibleMoves) {
      long moveBit = 1L << move.getPosition();
      if ((moveBit & CORNERS) != 0 && (moveBit & (X_SQUARES | C_SQUARES)) == 0) {
        int value = evaluateMove(board, move, move.getColor());
        if (value > bestValue) {
          bestValue = value;
          bestMove = move;
        }
      }
    }

    if (bestMove == null) {
      bestMove = possibleMoves.stream()
          .max(Comparator.comparingInt(m -> evaluateMove(board, m, m.getColor())))
          .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
    }

    return bestMove;
  }

  @Override
  public String getName() { return "SmartCornerGrabber"; }

  private int evaluateMove(Board board, Move move, PLAYER_COLOR color) {
    long moveBit = 1L << move.getPosition();
    if ((moveBit & CORNERS) != 0) return 5;
    return 0;
  }
}



