package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class StableEdgeOption implements Option {

  private static final long CORNERS = 0x8100000000000081L;
  private static final long BAD_EDGES = 0x2400000000000024L; // C-Squares
  private static final long EDGES = 0xFF818181818181FFL & ~CORNERS; // alle Ränder außer Ecken

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    long empty = ~(board.getWhite() | board.getBlack());
    return (EDGES & empty) != 0;
  }

  @Override
  public List<Board> initiationSet() { return new ArrayList<>(); }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    long empty = ~(board.getWhite() | board.getBlack());
    return (EDGES & empty) == 0;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    return possibleMoves.stream()
        .max(Comparator.comparingInt(this::evaluateMove))
        .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
  }

  private int evaluateMove(Move move) {
    long moveBit = 1L << move.getPosition();
    if ((moveBit & BAD_EDGES) != 0) return -10;
    if ((moveBit & EDGES) != 0) return 5;
    return 0;
  }

  @Override
  public String getName() { return "StableEdgeController"; }

  @Override
  public String toString() { return "StableEdgeController"; }
}
