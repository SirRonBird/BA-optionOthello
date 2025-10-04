package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Option to play stable disk of from the top right corner. Option selects
 * move which is stable along one of the edges. If no stable move is possible
 * or both edges are full the option is finished
 */

public class TopRightStableCornerOption implements Option {

  private static final String NAME = "TopRightStableCorner";

  private static final long TOPRIGHT_CORNER = 128L;
  private static final long TOP_EDGE = 255L;
  private static final long RIGHT_EDGE = 0x8080808080808080L;

  private static final Random rand = new Random();

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    Board cornerMask = new Board("TopRightCorner", true);
    cornerMask.setMask(TOPRIGHT_CORNER);

    long possibleMovesBitBoard = board.generateAllPossibleMoves(playerColor == PLAYER_COLOR.WHITE);

    return board.boardIsHittingMask(cornerMask, playerColor) &&
        ((possibleMovesBitBoard & RIGHT_EDGE) != 0 || (possibleMovesBitBoard & TOP_EDGE) != 0);
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    //Edges full or game over
    long edges = TOP_EDGE | RIGHT_EDGE;
    long allPieces = board.getWhite() | board.getBlack();

    return (allPieces & edges) ==  edges || board.isGameOver();
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    LOGGER.info(NAME);
    //Filter Moves to only take those on the edges into account
    List<Move> movesOnTopEdge = new ArrayList<>();
    List<Move> movesOnRightEdge = new ArrayList<>();

    List<Move> stableMoves = new ArrayList<>();

    for (Move move : possibleMoves) {
      if(((1L << move.getPosition()) & TOP_EDGE) != 0 ){
        movesOnTopEdge.add(move);
      } else if(((1L << move.getPosition()) & RIGHT_EDGE) != 0 ){
        movesOnRightEdge.add(move);
      }
    }

    //check for top edge stability
    for(Move move : movesOnTopEdge){
      if(isMoveTopEdgeStable(board, move)){
        stableMoves.add(move);
      }
    }
    //check for right edge stability
    for(Move move : movesOnRightEdge){
      if(isMoveRightEdgeStable(board, move)){
        stableMoves.add(move);
      }
    }
    if(!stableMoves.isEmpty()){
      return stableMoves.get(rand.nextInt(stableMoves.size()));
    } else {
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
  }

  private boolean isMoveTopEdgeStable(Board board, Move move){
    boolean isWhite = move.getColor() == PLAYER_COLOR.WHITE;
    Board clone = board.clone();
    clone.updateBoard(move.getPosition(), isWhite);
    long playerPieces;
    if(isWhite){
      playerPieces = board.getWhite();
    } else {
      playerPieces = board.getBlack();
    }

    long mask = 0L;
    for (int i = 7; i >= move.getPosition(); i--) {
      mask |= 1L << i;
    }
    return (playerPieces & mask) == mask;
  }

  private boolean isMoveRightEdgeStable(Board board, Move move){
    boolean isWhite = move.getColor() == PLAYER_COLOR.WHITE;
    Board clone = board.clone();
    clone.updateBoard(move.getPosition(), isWhite);
    long playerPieces;
    if(isWhite){
      playerPieces = board.getWhite();
    } else {
      playerPieces = board.getBlack();
    }

    long mask = 0L;
    for (int i = 7; i <= move.getPosition(); i += 8) {
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
