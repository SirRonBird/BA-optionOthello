package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.engine.OthelloEvaluator;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CornerSecuringOption implements Option {

  private static final long CORNERS = 0x8100000000000081L;

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    long empty = ~(board.getWhite() | board.getBlack());
    return (CORNERS & empty) != 0; // Ecke ist noch frei
  }

  @Override
  public List<Board> initiationSet() { return new ArrayList<>(); }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    long myDiscs = (playerColor == PLAYER_COLOR.WHITE) ? board.getWhite() : board.getBlack();
    long oppDiscs = (playerColor == PLAYER_COLOR.WHITE) ? board.getBlack() : board.getWhite();

    // Abbruch, wenn Ecke besetzt (egal von wem)
    if ((myDiscs & CORNERS) != 0 || (oppDiscs & CORNERS) != 0) {
      return true;
    }
    return false;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    PLAYER_COLOR color = possibleMoves.get(0).getColor();
    Move bestMove = null;
    int bestValue = Integer.MIN_VALUE;

    for (Move move : possibleMoves) {
      Board clone = board.clone();
      clone.updateBoard(move.getPosition(), color == PLAYER_COLOR.WHITE);
      int value = OthelloEvaluator.evaluate(clone, color);
      if (value > bestValue) {
        bestValue = value;
        bestMove = move;
      }
    }

    // Fallback (sollte selten vorkommen)
    if (bestMove == null) {
      bestMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
    }

    return bestMove;
  }

  @Override
  public String getName() { return "CornerSecuringOption"; }

  @Override
  public String toString() { return "CornerSecuringOption"; }
}
