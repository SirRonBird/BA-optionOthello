package com.mcgreedy.optionothello.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class Constants {

  public static final int[] DIRECTIONS = {-8, 8, 1, -1, -7, 7, -9, 9};

  private Constants() {
        // restrict instantiation
    }

    // Game constants
    public static final int BOARD_SIZE = 8;
    public static final int CELL_SIZE = 60;
    public static final int CELL_COUNT = BOARD_SIZE * BOARD_SIZE;
    public static final int CELL_GAP = 2;

    public enum PLAYER_COLOR {
        BLACK, WHITE
    }

    public enum PLAYER_TYPE {
        HUMAN,
        RANDOM_AI,
        MCTS,
        O_MCTS
    }

    public static final ObservableList<String> PLAYER_TYPES = FXCollections.observableArrayList(
            Arrays.stream(PLAYER_TYPE.values()).map(Enum::name).collect(Collectors.toList())
    );

    public enum MCTS_IMPROVEMENT_TYPE {
        SIMULATION,
        EXPLORATION
    }

}
