package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Option to get the top right corner, if the X field is captured by the enemy.
 * Option selects move which either directly captures the corner or creates a disk
 * on the diagonal to capture the corner in the next move.
 * Option is finished if the top right corner is captured
 */

public class TopRightCornerXOption implements Option {

  private static final String NAME = "TopRightCornerX";

  private static final long TOPRIGHT_X_FIELD = 16384L;
  private static final long TOPRIGHT_CORNER = 128L;
  private static final long ANTIDIAG = 4432676782080L;

  private static final int CORNER_POS = 7;

  private Random rand = new Random();

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    if(playerColor == PLAYER_COLOR.WHITE) {
      return (board.getBlack() & TOPRIGHT_X_FIELD) != 0 && (board.getWhite() & board.getBlack() & TOPRIGHT_CORNER) == 0;
    } else {
      return (board.getWhite() & TOPRIGHT_X_FIELD) != 0 && (board.getBlack() & board.getWhite() & TOPRIGHT_CORNER) == 0;
    }
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    if(playerColor == PLAYER_COLOR.WHITE) {
      return (board.getWhite() & TOPRIGHT_CORNER) != 0;
    } else {
      return (board.getBlack() & TOPRIGHT_CORNER) != 0;
    }
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    LOGGER.info(NAME);
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
          if ((clone.getWhite() & ANTIDIAG) != 0) {
            bestMoves.add(move);
          } else {
            if ((clone.getBlack() & ANTIDIAG) != 0) {
              bestMoves.add(move);
            }
          }
        }
      }
    }
    if(!bestMoves.isEmpty()) {
      return bestMoves.get(rand.nextInt(bestMoves.size()));
    } else {
      //TODO: better FallBack?
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
