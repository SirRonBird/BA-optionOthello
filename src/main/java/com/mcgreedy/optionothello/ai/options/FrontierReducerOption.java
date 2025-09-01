package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class FrontierReducerOption implements Option {

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    return true; // fast immer sinnvoll
  }

  @Override
  public List<Board> initiationSet() { return new ArrayList<>(); }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    return false;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    return possibleMoves.stream()
        .max(Comparator.comparingInt(m -> evaluateMove(board, m)))
        .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
  }

  private int evaluateMove(Board board, Move move) {
    boolean forWhite = move.getColor() == PLAYER_COLOR.WHITE;
    Board clone = board.clone();
    clone.updateBoard(move.getPosition(), forWhite);
    return -countFrontier(clone, move.getColor());
  }

  private int countFrontier(Board board, PLAYER_COLOR color) {
    long discs = (color == PLAYER_COLOR.WHITE) ? board.getWhite() : board.getBlack();
    long empty = ~(board.getWhite() | board.getBlack());
    int frontier = 0;
    for (int i = 0; i < 64; i++) {
      if (((discs >>> i) & 1L) != 0) {
        long neighbors = getNeighborsMask(i);
        if ((neighbors & empty) != 0) frontier++;
      }
    }
    return frontier;
  }

  private long getNeighborsMask(int pos) {
    int row = pos / 8, col = pos % 8;
    long mask = 0;
    for (int dr = -1; dr <= 1; dr++) {
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) continue;
        int r = row + dr, c = col + dc;
        if (r >= 0 && r < 8 && c >= 0 && c < 8) {
          mask |= 1L << (r * 8 + c);
        }
      }
    }
    return mask;
  }

  @Override
  public String getName() { return "FrontierReducer"; }

  @Override
  public String toString() {return "FrontierReducer"; }
}
