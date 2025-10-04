package com.mcgreedy.optionothello.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcgreedy.optionothello.dtos.OptionDTO;
import com.mcgreedy.optionothello.dtos.OptionDTO.BoardMaskDTO;
import com.mcgreedy.optionothello.dtos.SaveGameDTO;
import com.mcgreedy.optionothello.dtos.SaveGameDTO.MoveStatistics;
import com.mcgreedy.optionothello.dtos.SaveTournamentDTO;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.gamemanagement.Game;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.ai.Player;
import com.mcgreedy.optionothello.gamemanagement.Tournament;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SaveGameUtils {

    private SaveGameUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger LOGGER = LogManager.getLogger(SaveGameUtils.class);

    private static final List<SaveGameDTO> saveGames = new ArrayList<>();
    private static final List<SaveTournamentDTO> saveTournaments = new ArrayList<>();
    private static final List<OptionDTO> saveOptions = new ArrayList<>();

    public static void saveGame(String name, int winner, Player blackPlayer, Player whitePlayer, Game currentGame) {
        try {
            String projectDir = System.getProperty("user.dir");
            File directory = new File(projectDir, "savegames/games");

            if (!directory.exists()) {
                directory.mkdirs();
            }
            String filename = name + ".json";
            File gameFile = new File(directory, filename);

            ObjectMapper objectMapper = new ObjectMapper();
            SaveGameDTO saveGameDTO = createSaveGameDTO(name, winner, blackPlayer, whitePlayer, currentGame);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(gameFile, saveGameDTO);
            LOGGER.info("Saved game to file {}", filename);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private static SaveGameDTO createSaveGameDTO(String gameName, int winner, Player blackPlayer, Player whitePlayer, Game currentGame) {
        SaveGameDTO saveGameDTO = new SaveGameDTO();
        SaveGameDTO.GameDetails gameDetails = new SaveGameDTO.GameDetails();

        gameDetails.setId(UUID.randomUUID());
        gameDetails.setGameName(gameName);
        gameDetails.setWinner(winner);

        SaveGameDTO.PlayerDetails blackPlayerDetails = new SaveGameDTO.PlayerDetails();
        blackPlayerDetails.setColor(blackPlayer.getColor());
        blackPlayerDetails.setType(blackPlayer.getType());

        SaveGameDTO.PlayerDetails whitePlayerDetails = new SaveGameDTO.PlayerDetails();
        whitePlayerDetails.setColor(whitePlayer.getColor());
        whitePlayerDetails.setType(whitePlayer.getType());

        gameDetails.setBlackPlayer(blackPlayerDetails);
        gameDetails.setWhitePlayer(whitePlayerDetails);
        gameDetails.setStartBoardBlack(Board.BLACK_START_BOARD);
        gameDetails.setStartBoardWhite(Board.WHITE_START_BOARD);

        List<SaveGameDTO.MoveDetails> moveDetailsList = new ArrayList<>();
        if (currentGame.getMoveHistory().size() != currentGame.getBoardHistory().size()) {
            LOGGER.error("MoveHistory and BoardHistory have different sizes: {},{}",
                currentGame.getMoveHistory().size(), currentGame.getBoardHistory().size());
        } else {
            for (int i = 0; i < currentGame.getMoveHistory().size(); i++) {
                Move move = currentGame.getMoveHistory().get(i);
                Board board = currentGame.getBoardHistory().get(i);
                SaveGameDTO.MoveDetails moveDetails = new SaveGameDTO.MoveDetails();

                moveDetails.setColor(move.getColor());
                moveDetails.setPosition(move.getPosition());
                moveDetails.setSearchDepth(move.getSearchDepth());

                moveDetails.setPlayerType(move.getPlayerType());
                moveDetails.setBlackBoardAfterMove(board.getBlack());
                moveDetails.setWhiteBoardAfterMove(board.getWhite());

                MoveStatistics statistics = new SaveGameDTO.MoveStatistics();
                com.mcgreedy.optionothello.engine.MoveStatistics moveStatistics  = move.getStatistics();
                if(moveStatistics != null) {
                    statistics.setSearchDepth(moveStatistics.getSearchDepth());
                    statistics.setSearchTime(moveStatistics.getSearchTime());
                    statistics.setSearchedNodes(moveStatistics.getSearchedNodes());
                    statistics.setOption(OptionDTO.fromOption(moveStatistics.getOption()));
                } else {
                    statistics.setSearchDepth(0);
                    statistics.setSearchTime(0);
                    statistics.setSearchedNodes(0);
                    statistics.setOption(OptionDTO.fromOption(null));
                }

                moveDetails.setMoveStatistics(statistics);

                moveDetailsList.add(moveDetails);
            }
            gameDetails.setMoves(moveDetailsList);
        }


        saveGameDTO.setGame(gameDetails);
        return saveGameDTO;
    }

    public static void loadSaveGames() {
        saveGames.clear();
        ObjectMapper objectMapper = new ObjectMapper();


        String projectDir = System.getProperty("user.dir");
        File directory = new File(projectDir, "savegames/games");

        if (!directory.exists() || !directory.isDirectory()) {
            LOGGER.error("There is no savegames directory or it is not a directory");
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            LOGGER.error("There are no savegames in the savegames directory");
            return;
        }

        for (File file : files) {
            try (Reader reader = Files.newBufferedReader(file.toPath())) {
                SaveGameDTO saveGame = objectMapper.readValue(reader, SaveGameDTO.class);
                if (saveGame != null) {
                    //check for duplicates -> clearing the list beforehand
                    saveGames.add(saveGame);
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static void loadSaveTournaments() {
        saveTournaments.clear();
        ObjectMapper objectMapper = new ObjectMapper();

        String projectDir = System.getProperty("user.dir");
        File directory = new File(projectDir, "savegames/tournaments");

        if (!directory.exists() || !directory.isDirectory()) {
            LOGGER.error("There is no save tournaments directory or it is not a directory");
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            LOGGER.error("There are no save tournaments in the savegames directory");
            return;
        }

        for (File file : files) {
            try (Reader reader = Files.newBufferedReader(file.toPath())) {
                SaveTournamentDTO saveTournament = objectMapper.readValue(reader, SaveTournamentDTO.class);
                if (saveTournament != null) {
                    //check for duplicates -> clearing the list beforehand
                    saveTournaments.add(saveTournament);
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static void loadSaveOptions() {
        saveOptions.clear();
        ObjectMapper objectMapper = new ObjectMapper();

        String projectDir = System.getProperty("user.dir");
        File directory = new File(projectDir, "options/");
        if (!directory.exists() || !directory.isDirectory()) {
            LOGGER.error("There is no options directory or it is not a directory");
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            LOGGER.error("There are no save options in the savegames directory");
            return;
        }
        for (File file : files) {
            try (Reader reader = Files.newBufferedReader(file.toPath())){
                OptionDTO optionDTO = objectMapper.readValue(reader, OptionDTO.class);
                if (optionDTO != null) {
                    saveOptions.add(optionDTO);
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static List<SaveGameDTO> getSaveGames() {
        return saveGames;
    }

    public static List<SaveTournamentDTO> getSaveTournaments() {
        return saveTournaments;
    }

    public static List<OptionDTO> getSaveOptions(){
        return saveOptions;
    }

    public static void saveTournament(String name, int winner, Player blackPlayer, Player whitePlayer, Tournament tournament) {
        try {
            String projectDir = System.getProperty("user.dir");
            File directory = new File(projectDir, "savegames/tournaments");

            if (!directory.exists()) {
                directory.mkdirs();
            }
            String filename = name + ".json";
            File gameFile = new File(directory, filename);

            ObjectMapper objectMapper = new ObjectMapper();

            SaveTournamentDTO saveTournamentDTO = createTournamentDTO(name, winner, blackPlayer, whitePlayer, tournament);

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(gameFile, saveTournamentDTO);
            LOGGER.info("Saved game to file {}", filename);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private static SaveTournamentDTO createTournamentDTO(
            String name, int winner, Player blackPlayer, Player whitePlayer, Tournament tournament) {

        SaveTournamentDTO saveTournamentDTO = new SaveTournamentDTO();
        SaveTournamentDTO.TournamentDetails tournamentDetails = new SaveTournamentDTO.TournamentDetails();

        tournamentDetails.setId(UUID.randomUUID());
        tournamentDetails.setTournamentName(name);
        tournamentDetails.setWinner(winner);
        tournamentDetails.setNumberOfGames(tournament.getGamesPlayed());
        tournamentDetails.setBlackWins(tournament.getBlackWins());
        tournamentDetails.setWhiteWins(tournament.getWhiteWins());


        SaveTournamentDTO.PlayerDetails blackPlayerDetails = new SaveTournamentDTO.PlayerDetails();
        blackPlayerDetails.setColor(blackPlayer.getColor());
        blackPlayerDetails.setType(blackPlayer.getType());

        SaveTournamentDTO.PlayerDetails whitePlayerDetails = new SaveTournamentDTO.PlayerDetails();
        whitePlayerDetails.setColor(whitePlayer.getColor());
        whitePlayerDetails.setType(whitePlayer.getType());

        tournamentDetails.setBlackPlayer(blackPlayerDetails);
        tournamentDetails.setWhitePlayer(whitePlayerDetails);

        AtomicInteger gameIndex = new AtomicInteger();
        List<SaveTournamentDTO.GameDetails> gameDetailsList = new ArrayList<>();
        tournament.getGames().forEach(game -> {
            SaveTournamentDTO.GameDetails currentGameDetails = new SaveTournamentDTO.GameDetails();
            currentGameDetails.setWinner(game.getWinner());
            currentGameDetails.setGameNumber(gameIndex.getAndIncrement());

            currentGameDetails.setStartBoardBlack(Board.BLACK_START_BOARD);
            currentGameDetails.setStartBoardWhite(Board.WHITE_START_BOARD);

            List<SaveTournamentDTO.MoveDetails> moveDetailsList = new ArrayList<>();
            for (int i = 0; i < game.getMoveHistory().size(); i++) {
                Move move = game.getMoveHistory().get(i);
                Board board = game.getBoardHistory().get(i);
                SaveTournamentDTO.MoveDetails moveDetails = new SaveTournamentDTO.MoveDetails();

                moveDetails.setColor(move.getColor());
                moveDetails.setPosition(move.getPosition());
                moveDetails.setSearchDepth(move.getSearchDepth());
                moveDetails.setPlayerType(move.getPlayerType());
                moveDetails.setBlackBoardAfterMove(board.getBlack());
                moveDetails.setWhiteBoardAfterMove(board.getWhite());

                SaveTournamentDTO.MoveStatistics statistics = new SaveTournamentDTO.MoveStatistics();
                com.mcgreedy.optionothello.engine.MoveStatistics moveStatistics = move.getStatistics();

                statistics.setSearchDepth(moveStatistics.getSearchDepth());
                statistics.setSearchTime(moveStatistics.getSearchTime());
                statistics.setSearchedNodes(moveStatistics.getSearchedNodes());

                statistics.setOption(OptionDTO.fromOption(moveStatistics.getOption()));

                moveDetails.setMoveStatistics(statistics);

                moveDetailsList.add(moveDetails);
            }
            currentGameDetails.setMoves(moveDetailsList);
            gameDetailsList.add(currentGameDetails);
        });

        tournamentDetails.setGames(gameDetailsList);

        saveTournamentDTO.setTournament(tournamentDetails);

        return saveTournamentDTO;
    }

    public static void saveBoardAsPng(Node node, String filename){
        WritableImage image = node.snapshot(new SnapshotParameters(), null);
        File file = new File(filename);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image,null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<BoardMaskDTO> toMaskDTO(List<Board> boards) {
        List<BoardMaskDTO> boardMaskDTOs = new ArrayList<>();
        for (Board board : boards) {
            BoardMaskDTO boardMaskDTO = new BoardMaskDTO(
                board.getMask(),board.getName()
            );
            boardMaskDTOs.add(boardMaskDTO);
        }
        return boardMaskDTOs;
    }

    public static List<Board> fromMaskDTO(List<BoardMaskDTO> dto) {
        List<Board> boards = new ArrayList<>();
        for(BoardMaskDTO mask : dto){
            Board board = new Board(mask.name, true);
            board.setMask(mask.mask);
            boards.add(board);
        }
        return boards;
    }
}
