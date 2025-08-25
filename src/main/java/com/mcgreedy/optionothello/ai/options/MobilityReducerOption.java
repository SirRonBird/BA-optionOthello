package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobilityReducerOption implements Option {

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    boolean opponentWhite = playerColor != PLAYER_COLOR.WHITE;
    return Long.bitCount(board.generateAllPossibleMoves(opponentWhite)) > 1;
  }

  @Override
  public List<Board> initiationSet() { return new ArrayList<>(); }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    boolean opponentWhite = playerColor != PLAYER_COLOR.WHITE;
    return Long.bitCount(board.generateAllPossibleMoves(opponentWhite)) <= 1;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    Move bestMove = null;
    int bestValue = Integer.MIN_VALUE;
    boolean forWhite = possibleMoves.get(0).getColor() == PLAYER_COLOR.WHITE;

    for (Move move : possibleMoves) {
      int value = evaluateMove(board, move, forWhite);
      if (value > bestValue) {
        bestValue = value;
        bestMove = move;
      }
    }

    if (bestMove == null) {
      bestMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
    }
    return bestMove;
  }

  @Override
  public String getName() { return "MobilityReducerAdvanced"; }

  private int evaluateMove(Board board, Move move, boolean forWhite) {
    Board clone = board.clone();
    clone.updateBoard(move.getPosition(), forWhite);
    int oppMovesBefore = Long.bitCount(board.generateAllPossibleMoves(!forWhite));
    int oppMovesAfter = Long.bitCount(clone.generateAllPossibleMoves(!forWhite));
    return 2 * (oppMovesBefore - oppMovesAfter);
  }
}


