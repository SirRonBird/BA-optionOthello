package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Option to get the bottom right corner, if the X field is captured by the enemy.
 * Option selects move which either directly captures the corner or creates a disk
 * on the diagonal to capture the corner in the next move.
 * Option is finished if the bottom right corner is captured
 */

public class BottomRightCornerXOption implements Option {

  private static final String NAME = "BottomRightCornerX";

  private static final long BOTTOMRIGHT_X_FIELD = 18014398509481984L;
  private static final long BOTTOMRIGHT_CORNER = 0x8000000000000000L;
  private static final long MAINDIAG = 35253226045440L;

  private static final int CORNER_POS = 63;

  private Random rand = new Random();

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    if(playerColor == PLAYER_COLOR.WHITE) {
      return (board.getBlack() & BOTTOMRIGHT_X_FIELD) != 0 && (board.getWhite() & board.getBlack() & BOTTOMRIGHT_CORNER) == 0;
    } else {
      return (board.getWhite() & BOTTOMRIGHT_X_FIELD) != 0 && (board.getBlack() & board.getWhite() & BOTTOMRIGHT_CORNER) == 0;
    }
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    if(playerColor == PLAYER_COLOR.WHITE) {
      return (board.getWhite() & BOTTOMRIGHT_CORNER) != 0;
    } else {
      return (board.getBlack() & BOTTOMRIGHT_CORNER) != 0;
    }
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    boolean isWhite = possibleMoves.getFirst().getColor() == PLAYER_COLOR.WHITE;
    List<Move> bestMoves = new ArrayList<>();
    for(Move move : possibleMoves) {
      if (move.getPosition() == CORNER_POS) {
        return move;
      } else {
        Board clone = board.clone();
        //make move
        clone.updateBoard(move.getPosition(), isWhite);
        // check if we got an own piece in the diag to eventually take the corner later
        if (isWhite) {
          if ((clone.getWhite() & MAINDIAG) != 0) {
            bestMoves.add(move);
          } else {
            if ((clone.getBlack() & MAINDIAG) != 0) {
              bestMoves.add(move);
            }
          }
        }
      }
    }
    if(!bestMoves.isEmpty()) {
      return bestMoves.get(rand.nextInt(bestMoves.size()));
    } else {
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
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
