package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class SmartCornerGrabberOption implements Option {

  private static final long CORNERS = 0x8100000000000081L;
  private static final long X_SQUARES = 0x4200000000000042L;
  private static final long C_SQUARES = 0x2400000000000024L;

  private static final int[] POSITION_WEIGHTS = {
      100, -20, 10, 5, 5, 10, -20, 100,
      -20, -50, -2, -2, -2, -2, -50, -20,
      10, -2, -1, -1, -1, -1, -2, 10,
      5, -2, -1, -1, -1, -1, -2, 5,
      5, -2, -1, -1, -1, -1, -2, 5,
      10, -2, -1, -1, -1, -1, -2, 10,
      -20, -50, -2, -2, -2, -2, -50, -20,
      100, -20, 10, 5, 5, 10, -20, 100
  };

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
    return possibleMoves.stream()
        .max(Comparator.comparingInt(m -> evaluateMove(m)))
        .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
  }

  private int evaluateMove(Move move) {
    return POSITION_WEIGHTS[move.getPosition()];
  }

  @Override
  public String getName() { return "SmartCornerGrabber"; }

  @Override
  public String toString() { return "SmartCornerGrabber"; }
}
