package com.mcgreedy.optionothello.ai;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.engine.MoveStatistics;
import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RandomPlayer extends Player {

    private static final Logger LOGGER = LogManager.getLogger(RandomPlayer.class);

    public RandomPlayer(Constants.PLAYER_COLOR color, Constants.PLAYER_TYPE type, Gamemanager gamemanager) {
        super(color, type, gamemanager);
    }

    @Override
    public Move getMove(Board board) {
        long allPossibleMoves = gamemanager.getCurrentGame().board.generateAllPossibleMoves(color == Constants.PLAYER_COLOR.WHITE);
        MoveStatistics moveStatistics = new MoveStatistics(
            -1,
            null,
            0,
            0
        );
        if (allPossibleMoves != 0L) {

            List<Integer> moveIndices = getMoveIndices(allPossibleMoves);


            Move move = new Move(
                    this.color,
                    moveIndices.get((int) (Math.random() * moveIndices.size())),
                    1,
                    this.type
            );
            move.setStatistics(moveStatistics);
            return move;
        } else {
            LOGGER.info("RandomPlayer {} is passing because all possible moves are {}", this.color, allPossibleMoves);
            Move move = new Move(this.color, -1, -1, this.type);
            move.setStatistics(moveStatistics);
            return move;
        }


    }

    @Override
    public void resetMAST() {
        //nothing to do here
    }

    private List<Integer> getMoveIndices(long bitboard) {
        List<Integer> moves = new ArrayList<>();

        for (int i = 0; i < Constants.CELL_COUNT; i++) {
            if (((bitboard >>> i) & 1L) != 0) {
                moves.add(i);
            }
        }

        return moves;
    }

}
