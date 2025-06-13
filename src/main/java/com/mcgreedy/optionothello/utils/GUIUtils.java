package com.mcgreedy.optionothello.utils;

import com.mcgreedy.optionothello.ui.GameAnalysisUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.function.BiConsumer;

import static com.mcgreedy.optionothello.utils.Constants.BOARD_SIZE;
import static com.mcgreedy.optionothello.utils.Constants.CELL_SIZE;

public final class GUIUtils {

    private static final String BOT_RIGHT_RADIUS = "-fx-background-radius: 0 0 5 0;";
    private static final String TOP_RIGHT_RADIUS = "-fx-background-radius: 0 0 0 5;";
    private static final String BOT_LEFT_RADIUS = "-fx-background-radius: 0 5 0 0;";
    private static final String TOP_LEFT_RADIUS = "-fx-background-radius: 5 0 0 0;";

    private GUIUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static GridPane createBoardGrid(BiConsumer<Integer, Integer> clickHandler, int useLabel) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(Constants.CELL_GAP);
        grid.setVgap(Constants.CELL_GAP);
        grid.setPadding(new Insets(10));
        grid.setStyle("-fx-background-color: #2E8B57;");


        // Create the cells
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                StackPane cell = createBoardCell(row, col, clickHandler, useLabel);
                grid.add(cell, col, row);
            }
        }
        return grid;
    }

    private static StackPane createBoardCell(int row, int col, BiConsumer<Integer, Integer> clickHandler, int useLabel) {
        StackPane cell = new StackPane();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);

        final int r = row;
        final int c = col;

        if (clickHandler != null) {
            cell.setOnMouseClicked(e -> clickHandler.accept(r, c));
        }

        cell.setStyle(getCellStyle(row, col));

        if(useLabel == 1) {
            char columnLabel = (char) ('a' + col);
            int rowLabel = row + 1;

            cell.setPadding(new Insets(0, 1, 1, 0));

            Label label = new Label(columnLabel + String.valueOf(rowLabel));
            label.setTextFill(Color.WHITE);
            label.setOpacity(0.8);
            label.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
            StackPane.setAlignment(label, Pos.BOTTOM_RIGHT);

            cell.getChildren().add(label);
        } else if (useLabel == 2) {
            // Special Cell Labels
            String labelText = "";
            if((row == 0 && col == 1) ||
                (row == 1 && col == 0) ||
                (row == 0 && col == 6) ||
                (row == 1 && col == 7) ||
                (row == 6 && col == 0) ||
                (row == 6 && col == 7) ||
                (row == 7 && col == 1) ||
                (row == 7 && col == 6 )
            ) {
                labelText = "C";
            } else if (
                (row == 1 && col == 1) ||
                    (row == 6 && col == 6) ||
                    (row == 1 && col == 6) ||
                    (row == 6 && col == 1)
            ) {
                labelText = "X";
            } else if (
                (row == 0 && col == 2) ||
                    (row == 0 && col == 5) ||
                    (row == 2 && col == 0) ||
                    (row == 2 && col == 7) ||
                    (row == 5 && col == 0) ||
                    (row == 5 && col == 7) ||
                    (row == 7 && col == 2) ||
                    (row == 7 && col == 5)
            ) {
                labelText = "A";
            } else if (
                (row == 0 && col == 3) ||
                    (row == 0 && col == 4) ||
                    (row == 3 && col == 0) ||
                    (row == 3 && col == 7) ||
                    (row == 4 && col == 0) ||
                    (row == 4 && col == 7) ||
                    (row == 7 && col == 3) ||
                    (row == 7 && col == 4)
            ) {
                labelText = "B";
            }

            if(
                (row == 2 && col == 2) ||
                    (row == 2 && col == 3) ||
                    (row == 2 && col == 4) ||
                    (row == 2 && col == 5) ||
                    (row == 3 && col == 2) ||
                    (row == 3 && col == 3) ||
                    (row == 3 && col == 4) ||
                    (row == 3 && col == 5) ||
                    (row == 4 && col == 2) ||
                    (row == 4 && col == 3) ||
                    (row == 4 && col == 4) ||
                    (row == 4 && col == 5) ||
                    (row == 5 && col == 2) ||
                    (row == 5 && col == 3) ||
                    (row == 5 && col == 4) ||
                    (row == 5 && col == 5)
            ){
                cell.setStyle("-fx-background-color: #c3ba59;");
            }

            Label label = new Label(labelText);
            label.setTextFill(Color.WHITE);
            label.setOpacity(0.8);
            label.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
            StackPane.setAlignment(label, Pos.CENTER);

            cell.getChildren().add(label);
        }

        return cell;
    }

    private static String getCellStyle(int row, int col){
        String base = "-fx-background-color: #3CB371;";
        String radius = "";

        if (row == 1 && col == 1) {
            radius = BOT_RIGHT_RADIUS;
        } else if (row == 1 && col == 2) {
            radius = TOP_RIGHT_RADIUS;
        } else if (row == 2 && col == 1) {
            radius = BOT_LEFT_RADIUS;
        } else if (row == 2 && col == 2) {
            radius = TOP_LEFT_RADIUS;
        } else if (row == 1 && col == 5) {
            radius = BOT_RIGHT_RADIUS;
        } else if (row == 1 && col == 6) {
            radius = TOP_RIGHT_RADIUS;
        } else if (row == 2 && col == 6) {
            radius = TOP_LEFT_RADIUS;
        } else if (row == 2 && col == 5) {
            radius = BOT_LEFT_RADIUS;
        } else if (row == 5 && col == 1) {
            radius = BOT_RIGHT_RADIUS;
        } else if (row == 5 && col == 2) {
            radius = TOP_RIGHT_RADIUS;
        } else if (row == 6 && col == 2) {
            radius = TOP_LEFT_RADIUS;
        } else if (row == 6 && col == 1) {
            radius = BOT_LEFT_RADIUS;
        } else if (row == 5 && col == 5) {
            radius = BOT_RIGHT_RADIUS;
        } else if (row == 5 && col == 6) {
            radius = TOP_RIGHT_RADIUS;
        } else if (row == 6 && col == 6) {
            radius = TOP_LEFT_RADIUS;
        } else if (row == 6 && col == 5) {
            radius = BOT_LEFT_RADIUS;
        }

        return base + radius;
    }

    public static void updateBoardGrid(long black, long white, GridPane boardGrid, int lastPlayedCell) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int index = row * BOARD_SIZE + col;
                StackPane cell = getCellFromGrid(boardGrid, row, col);

                cell.getChildren().removeIf(Circle.class::isInstance);

                long mask = 1L << index;


                if ((black & mask) != 0) {
                    if (index == lastPlayedCell) {
                        Circle circle = createDisc(Color.BLACK, true);
                        cell.getChildren().add(circle);
                    } else {
                        Circle circle = createDisc(Color.BLACK, false);
                        cell.getChildren().add(circle);
                    }
                } else if ((white & mask) != 0) {
                    if (index == lastPlayedCell) {
                        Circle circle = createDisc(Color.WHITE, true);
                        cell.getChildren().add(circle);
                    } else {
                        Circle circle = createDisc(Color.WHITE, false);
                        cell.getChildren().add(circle);
                    }
                }
            }
        }
    }

    public static Circle getPieceFromGrid(GridPane boardGrid, int row, int col) {
        StackPane cell = getCellFromGrid(boardGrid, row, col);

        for (Node node : cell.getChildren()) {
            if(node instanceof Circle){
                return (Circle) node;
            }
        }

        return null;
    }

    private static StackPane getCellFromGrid(GridPane grid, int row, int col) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (StackPane) node;
            }
        }
        throw new IllegalArgumentException("Cell not found at (" + row + ", " + col + ")");


    }

    private static Circle createDisc(Color color, boolean marked) {
        Circle disc = new Circle(CELL_SIZE * 0.4); // etwas kleiner als Zelle
        disc.setFill(color);

        if (marked) {
            disc.setStrokeWidth(2);
            disc.setStroke(Color.YELLOW);
        }

        return disc;
    }

    public static void markPiece(int row, int col){
        Circle piece = GUIUtils.getPieceFromGrid(GameAnalysisUI.gameBoard,row,col);

        assert piece != null;
        if(piece.getStroke() == null){
            piece.setStroke(Color.RED);
            piece.setStrokeWidth(4);
        } else if(piece.getStroke().equals(Color.RED)) {
            piece.setStroke(null);
            piece.setStrokeWidth(0);
        } else {
            piece.setStroke(Color.RED);
            piece.setStrokeWidth(4);
        }
    }


}