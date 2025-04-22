package com.mcgreedy.optionothello;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class Constants {
    private Constants() {
        // restrict instantiation
    }

    // Game constants
    public static final int BOARD_SIZE = 8;
    public static final int CELL_SIZE = 60;
    public static final int PIECE_RADIUS = 25;

    public enum PLAYER_COLOR {
        BLACK, WHITE
    };

    public enum PLAYER_TYPE {
        HUMAN,
        RANDOM_AI,
        MCTS,
        O_MCTS
    }

    public static final ObservableList<String> PLAYER_TYPES = FXCollections.observableArrayList(
            Arrays.stream(PLAYER_TYPE.values()).map(Enum::name).collect(Collectors.toList())
    );



}
