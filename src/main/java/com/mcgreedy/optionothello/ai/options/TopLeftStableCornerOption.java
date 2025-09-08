package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TopLeftStableCornerOption implements Option {

  private static final String NAME = "TopLeftStableCorner";

  private static final long TOPLEFT_CORNER = 1L;
  private static final long TOP_EDGE = 255L;
  private static final long LEFT_EDGE = 72340172838076673L;

  private static final Random rand = new Random();

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    Board cornerMask = new Board("TopLeftCorner", true);
    cornerMask.mask = TOPLEFT_CORNER;

    long possibleMovesBitBoard = board.generateAllPossibleMoves(playerColor == PLAYER_COLOR.WHITE);

    return  board.boardIsHittingMask(cornerMask, playerColor) &&
        ((possibleMovesBitBoard & LEFT_EDGE) != 0 || (possibleMovesBitBoard & TOP_EDGE) != 0);
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    //Edges full or game over
    long edges = TOP_EDGE | LEFT_EDGE;
    long allPieces = board.getBlack() | board.getWhite();

    return (allPieces & edges) == edges || board.isGameOver();
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    LOGGER.info(NAME);
    //Filter Moves to only take those on the edges into account
    List<Move> movesOnTopEdge = new ArrayList<>();
    List<Move> movesOnLeftEdge = new ArrayList<>();

    List<Move> stableMoves = new ArrayList<>();

    for (Move move : possibleMoves) {
      if(((1L << move.getPosition()) & TOP_EDGE) != 0 ){
        movesOnTopEdge.add(move);
      } else if (((1L << move.getPosition()) & LEFT_EDGE) != 0 ) {
        movesOnLeftEdge.add(move);
      }
    }

    //check for top edge stability
    for (Move move : movesOnTopEdge) {
      if(isMoveTopEdgeStable(board,move)){
        stableMoves.add(move);
      }
    }
    //check for left edge stability
    for (Move move : movesOnLeftEdge) {
      if(isMoveLeftEdgeStable(board,move)){
        stableMoves.add(move);
      }
    }

    if(!stableMoves.isEmpty()){
      return stableMoves.get(rand.nextInt(stableMoves.size()));
    } else {
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
  }

  private boolean isMoveTopEdgeStable(Board board, Move move) {
    boolean isWhite = move.getColor() == PLAYER_COLOR.WHITE;
    Board clone = board.clone();
    clone.updateBoard(move.getPosition(), isWhite);
    long playerPieces;
    if(isWhite){
      playerPieces = clone.getWhite();
    } else {
      playerPieces = clone.getBlack();
    }

    long mask = ((1L << (move.getPosition() +1)) -1);

    return (playerPieces & mask) == mask;
  }

  private boolean isMoveLeftEdgeStable(Board board, Move move) {
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
    for (int i = 0; i <= move.getPosition(); i += 8) {
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
