package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Option to select a move which prevents a corner to be taken by the enemy
 */
public class PreventOpponentCornerOption implements Option {

  private static final Set<Integer> C_FIELDS = Set.of(1,8,6,15,48,57,55,62);
  private static final Set<Integer> X_FIELDS = Set.of(9,14,49,54);
  private static final long DANGEROUS_FIELDS = 4810688826961871682L;
  private static final long CORNERS = 0x8100000000000081L;

  private static final String NAME = "PreventOpponentCornerOption";

  private static final Random rand  = new Random();

  @Override public boolean isBoardInInitiationSet(Board board, Constants.PLAYER_COLOR color) {
    //if corners are playable (only if C and/or X squares are occupied by me)
    boolean isWhite = color == Constants.PLAYER_COLOR.WHITE;
    if(isWhite){
      return (board.getWhite() & DANGEROUS_FIELDS) != 0;
    } else {
      return (board.getBlack() & DANGEROUS_FIELDS) != 0;
    }
  }
  @Override public List<Board> initiationSet() {
    return Collections.emptyList();
  }
  @Override public boolean shouldTerminate(Board board, Constants.PLAYER_COLOR color) {
    //if corners arent playable anymore
    boolean isWhite = color == PLAYER_COLOR.WHITE;
    long enemyMoves = board.generateAllPossibleMoves(!isWhite);

    return (enemyMoves & CORNERS) == 0;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    boolean isWhite = possibleMoves.getFirst().getColor() == Constants.PLAYER_COLOR.WHITE;

    List<Move> goodMoves = new ArrayList<>();

    List<Move> filteredMoves = possibleMoves.stream().filter(
        move -> !C_FIELDS.contains(move.getPosition()) && !X_FIELDS.contains(move.getPosition())
    ).toList();

    for(Move move : possibleMoves){
      Board clone = board.clone();
      clone.updateBoard(move.getPosition(), isWhite);
      long enemyMoves = clone.generateAllPossibleMoves(!isWhite);
      if((enemyMoves & CORNERS) == 0){
        goodMoves.add(move);
      }
    }

    if(!goodMoves.isEmpty()){
      return goodMoves.get(rand.nextInt(goodMoves.size()));
    } else if(!filteredMoves.isEmpty()) {
      return filteredMoves.get(rand.nextInt(filteredMoves.size()));
    } else {
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
  }

  @Override public String getName() { return NAME; }

  @Override public String toString() { return NAME; }
}
