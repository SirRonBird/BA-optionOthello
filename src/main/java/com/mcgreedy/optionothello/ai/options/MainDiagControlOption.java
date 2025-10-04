package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Option to control the main diagonal. Selectable if the player don't have any
 * disk on the diagonal. Then selects a move which creates a disk on the diagonal.
 * Option does not play on the X-fields.
 * Option is finished if at least one disk is on the diag.
 */

public class MainDiagControlOption implements Option {

  private static final String NAME = "MainDiagControl";
  private static final Random rand  = new Random();

  private static final long MAINDIAG = 35253226045440L;

  private static final long bestPositions = 35184372350976L;
  private static final List<Integer> dangerPositions = List.of(
      9,54
  );

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    // if the player dont have any piece on the diag
    boolean isWhite = playerColor == PLAYER_COLOR.WHITE;
    if(isWhite){
      return (board.getWhite() & MAINDIAG) == 0;
    } else {
      return (board.getBlack() & MAINDIAG) == 0;
    }
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    boolean isWhite = playerColor == PLAYER_COLOR.WHITE;
    if(isWhite){
      return (board.getWhite() & MAINDIAG) != 0;
    } else {
      return (board.getBlack() & MAINDIAG) != 0;
    }
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    boolean isWhite = possibleMoves.getFirst().getColor() == PLAYER_COLOR.WHITE;
    List<Move> movesToTakeDiag = new ArrayList<>();
    List<Move> betterMoves = new ArrayList<>();
    for (Move move : possibleMoves) {
      Board clone = board.clone();
      clone.updateBoard(move.getPosition(), isWhite);

      if((isWhite && (clone.getWhite() & MAINDIAG) != 0) || (!isWhite && (clone.getBlack() & MAINDIAG) != 0)){
          movesToTakeDiag.add(move);
          if((isWhite && (clone.getWhite() & bestPositions) != 0) || (!isWhite && (clone.getBlack() & bestPositions) != 0)){
            betterMoves.add(move);
          }
      }
    }

    List<Move> bestMoves = new ArrayList<>();
    if(!betterMoves.isEmpty()){
      bestMoves.addAll(betterMoves.stream().filter(
          move -> !dangerPositions.contains(move.getPosition())
      ).toList());
    } else if(!movesToTakeDiag.isEmpty()){
      bestMoves.addAll(movesToTakeDiag.stream().filter(
          move -> !dangerPositions.contains(move.getPosition())
      ).toList());
    }

    if(!bestMoves.isEmpty()){
      return bestMoves.get(rand.nextInt(bestMoves.size()));
    }


    return possibleMoves.get(rand.nextInt(possibleMoves.size()));
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
