package com.mcgreedy.optionothello.ai;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.utils.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MCTSPlayerTest {

    @Test
    void getMove() {
        // Given: Ein spezifisches Board mit bekannten Zügen
        long black = 0x43ffcfcbffffL;
        long white = 0x30340000L;
        Board board = new Board(black, white);

        // MCTSPlayer vorbereiten
        Constants.PLAYER_COLOR color = Constants.PLAYER_COLOR.WHITE;
        Constants.PLAYER_TYPE type = Constants.PLAYER_TYPE.MCTS;
        Gamemanager dummyManager = null; // Falls nicht gebraucht

        MCTSSettings options = new MCTSSettings(
                1.41,
            false,
            false
        );

        MCTSPlayer player = new MCTSPlayer(color, type, dummyManager, options);

        // When: getMove aufgerufen wird
        Move move = null;
        try {
            move = player.getMove(board);
        } catch (Exception e) {
            fail("getMove() hat eine Exception geworfen: " + e.getMessage());
        }

        // Then: Ein gültiger Zug sollte zurückgegeben werden
        assertNotNull(move, "MCTS getMove() sollte einen gültigen Move zurückgeben.");
        assertTrue(move.getPosition() >= 0 && move.getPosition() < 64, "Position muss im gültigen Bereich liegen.");
    }
}