package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Option to play stable disk of from the bottom right corner. Option selects
 * move which is stable along one of the edges. If no stable move is possible
 * or both edges are full the option is finished
 */

public class BottomRightStableCornerOption implements Option {

  private static final String NAME = "BottomRightStableCorner";

  private static final long BOTTOMRIGHT_CORNER = 0x8000000000000000L;
  private static final long BOTTOM_EDGE = 0xff00000000000000L;
  private static final long RIGHT_EDGE = 0x8080808080808080L;

  private static final Random rand  = new Random();

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    Board cornerMask = new Board("BottomLeftCorner", true);
    cornerMask.setMask(BOTTOMRIGHT_CORNER);

    long possibleMovesBitBoard = board.generateAllPossibleMoves(playerColor == PLAYER_COLOR.WHITE);

    return board.boardIsHittingMask(cornerMask, playerColor) &&
        ((possibleMovesBitBoard & BOTTOM_EDGE) != 0 || (possibleMovesBitBoard & RIGHT_EDGE) != 0);
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    //Edges full or game over
    long edges = BOTTOM_EDGE | RIGHT_EDGE;
    long allPieces = board.getWhite() | board.getBlack();

    return (allPieces & edges) == edges || board.isGameOver();
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    //Filter Moves to only take those on the edges into account
    List<Move> movesOnBottomEdge = new ArrayList<>();
    List<Move> movesOnRightEdge = new ArrayList<>();

    List<Move> stableMoves = new ArrayList<>();

    for(Move move : possibleMoves) {
      if(((1L << move.getPosition()) & BOTTOM_EDGE) != 0 ){
        movesOnBottomEdge.add(move);
      } else if(((1L << move.getPosition()) & RIGHT_EDGE) != 0 ){
        movesOnRightEdge.add(move);
      }
    }

    //check for bottom edge stability
    for(Move move : movesOnBottomEdge) {
      if(isMoveBottomEdgeStable(board, move)) {
        stableMoves.add(move);
      }
    }
    //check for right edge stability
    for(Move move : movesOnRightEdge) {
      if(isMoveRightEdgeStable(board, move)) {
        stableMoves.add(move);
      }
    }

    if(!stableMoves.isEmpty()) {
      return stableMoves.get(rand.nextInt(stableMoves.size()));
    } else {
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
  }

  private boolean isMoveBottomEdgeStable(Board board, Move move){
    boolean isWhite = move.getColor() == PLAYER_COLOR.WHITE;
    Board clone = board.clone();
    clone.updateBoard(move.getPosition(), isWhite);
    long playerPieces;
    if(isWhite){
      playerPieces = clone.getWhite();
    } else {
      playerPieces = clone.getBlack();
    }
    long mask = 0L;
    for (int i = 63; i >= move.getPosition(); i--) {
      mask |= (1L << i);
    }

    return (playerPieces & mask) == mask;
  }

  private boolean isMoveRightEdgeStable(Board board, Move move){
    boolean isWhite = move.getColor() == PLAYER_COLOR.WHITE;
    Board clone = board.clone();
    clone.updateBoard(move.getPosition(), isWhite);
    long playerPieces;
    if(isWhite){
      playerPieces = clone.getWhite();
    } else {
      playerPieces = clone.getBlack();
    }
    long mask = 0L;
    for (int i = 63; i >= move.getPosition(); i -= 8) {
      mask |= (1L << i);
    }

    return (playerPieces & mask) == mask;
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
