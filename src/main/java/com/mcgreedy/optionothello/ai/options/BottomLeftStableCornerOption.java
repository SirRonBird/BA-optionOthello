package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BottomLeftStableCornerOption implements Option {

  private static final String NAME = "BottomLeftStableCorner";

  private static final long BOTTOMLEFT_CORNER = 72057594037927936L;
  private static final long BOTTOM_EDGE = 0xff00000000000000L;
  private static final long LEFT_EDGE = 72340172838076673L;

  private static final Random rand = new Random();

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    Board cornerMask = new Board("BottomLeftCorner", true);
    cornerMask.mask = BOTTOMLEFT_CORNER;

    long possibleMovesBitBoard = board.generateAllPossibleMoves(playerColor == PLAYER_COLOR.WHITE);

    return board.boardIsHittingMask(cornerMask, playerColor) &&
        ((possibleMovesBitBoard & BOTTOM_EDGE) != 0 || (possibleMovesBitBoard & LEFT_EDGE) != 0);
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    //Edges full or game over
    long edges = BOTTOM_EDGE | LEFT_EDGE;
    long allPieces = board.getWhite() | board.getBlack();

    return (allPieces & edges) == edges || board.isGameOver();
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    LOGGER.info(NAME);
    //Filter Moves to only take those on the edges into account
    List<Move> movesOnBottomEdge = new ArrayList<>();
    List<Move> movesOnLeftEdge = new ArrayList<>();

    List<Move> stableMoves = new ArrayList<>();

    for(Move move : possibleMoves) {
      if(((1L << move.getPosition()) & BOTTOM_EDGE) != 0 ){
        movesOnBottomEdge.add(move);
      } else if(((1L << move.getPosition()) & LEFT_EDGE) != 0 ){
        movesOnLeftEdge.add(move);
      }
    }

    //check for bottom edge stability
    for(Move move : movesOnBottomEdge) {
      if(isMoveBottomEdgeStable(board, move)) {
        stableMoves.add(move);
      }
    }
    //check for left edge stability
    for(Move move : movesOnLeftEdge) {
      if(isMoveLeftEdgeStable(board, move)) {
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
    for (int i = 56; i <= move.getPosition(); i++) {
      mask |= (1L << i);
    }

    return (playerPieces & mask) == mask;
  }

  private boolean isMoveLeftEdgeStable(Board board, Move move){
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
    for (int i = 56; i >= move.getPosition(); i -= 8) {
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
