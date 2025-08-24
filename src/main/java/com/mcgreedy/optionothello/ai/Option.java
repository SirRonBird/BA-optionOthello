package com.mcgreedy.optionothello.ai;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Option {

  Logger LOGGER = LogManager.getLogger(Option.class);

  boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor);

  List<Board> initiationSet();

  boolean shouldTerminate(Board board, PLAYER_COLOR playerColor);

  Move getBestMove(Board board, List<Move> possibleMoves);

  String getName();

}
