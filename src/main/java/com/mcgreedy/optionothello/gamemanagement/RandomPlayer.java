package com.mcgreedy.optionothello.gamemanagement;

import com.mcgreedy.optionothello.engine.Move;
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
    public void makeMove() {
        long allPossibleMoves = gamemanager.currentGame.board.generateAllPossibleMoves(color == Constants.PLAYER_COLOR.WHITE);

        if(allPossibleMoves != 0L) {

            List<Integer> moveIndices = getMoveIndices(allPossibleMoves);


            Move move = new Move(
                    this.color,
                    moveIndices.get((int) (Math.random() * moveIndices.size())),
                    1,
                    this.type
            );
            try{
                Thread.sleep(10);
                this.gamemanager.makeMove(move);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }

        } else {
            LOGGER.info("RandomPlayer {} is passing because all possible moves are {}", this.color, allPossibleMoves);
            this.gamemanager.passMove(new Move(this.color, -1, -1, this.type));
        }
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
