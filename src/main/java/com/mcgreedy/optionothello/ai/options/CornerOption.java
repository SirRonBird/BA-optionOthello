package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CornerOption implements Option {

  Board cornerMask = new Board("Corners", true);

  Board cFields = new Board("CFields", true);
  Board xFields = new Board("XFields", true);

  String name = "CornerOption";
  List<Board> initiationSet = new ArrayList<>();

  Random rand = new Random();

  List<Integer> cornerPositions = new ArrayList<>(
      Arrays.asList(
          0,7,56,63
      )
  );
  List<Integer> xPositions = new ArrayList<>(
      Arrays.asList(
          9,14,49,54
      )
  );
  List<Integer> cPositions = new ArrayList<>(
      Arrays.asList(
          1,8,6,15,48,57,55,62
      )
  );

  public CornerOption(){
    cornerMask.mask = 0x8100000000000081L;
    cFields.mask =0x4281000000008142L;
    xFields.mask = 0x42000000004200L;

    Board fullBoardMask = new Board("FullBoard", true);
    fullBoardMask.mask = 0xffffffffffffffffL;
    initiationSet.add(fullBoardMask);
  }

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    boolean result = false;
    for (Board m : initiationSet) {
      result |= board.boardIsHittingMask(m, playerColor);
    }
    return result;
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    return board.boardIsHittingMask(cornerMask, playerColor);
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    List<Move> bestMoves = new ArrayList<>();

    List<Move> playableMoves = new ArrayList<>(possibleMoves);
    for(Move move : possibleMoves){
      if(cornerPositions.contains(move.getPosition())){
        bestMoves.add(move);
      } else if(xPositions.contains(move.getPosition())){
        playableMoves.remove(move);
      } else if(cPositions.contains(move.getPosition())){
        playableMoves.remove(move);
      }
    }
    if(!bestMoves.isEmpty()){
      return bestMoves.get(rand.nextInt(bestMoves.size()));
    } else if(!playableMoves.isEmpty()){
      return playableMoves.get(rand.nextInt(playableMoves.size()));
    } else {
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString(){
    return name;
  }
}
