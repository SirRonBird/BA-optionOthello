package com.mcgreedy.optionothello.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mcgreedy.optionothello.dtos.SaveGameDTO.MoveStatistics;
import com.mcgreedy.optionothello.utils.Constants;

import java.util.List;
import java.util.UUID;

public class SaveTournamentDTO {

    @JsonProperty("Tournament")
    public TournamentDetails tournament;

    public TournamentDetails getTournament() {
        return tournament;
    }

    public void setTournament(TournamentDetails tournament) {
        this.tournament = tournament;
    }

    public static class TournamentDetails {
        private UUID id;
        private String tournamentName;
        private int winner;
        private int numberOfGames;
        private int blackWins;
        private int whiteWins;
        private PlayerDetails blackPlayer;
        private PlayerDetails whitePlayer;

        private List<GameDetails> games;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getTournamentName() {
            return tournamentName;
        }

        public void setTournamentName(String tournamentName) {
            this.tournamentName = tournamentName;
        }

        public int getWinner() {
            return winner;
        }

        public void setWinner(int winner) {
            this.winner = winner;
        }

        public int getNumberOfGames() {
            return numberOfGames;
        }

        public void setNumberOfGames(int numberOfGames) {
            this.numberOfGames = numberOfGames;
        }

        public PlayerDetails getBlackPlayer() {
            return blackPlayer;
        }

        public void setBlackPlayer(PlayerDetails blackPlayer) {
            this.blackPlayer = blackPlayer;
        }

        public PlayerDetails getWhitePlayer() {
            return whitePlayer;
        }

        public void setWhitePlayer(PlayerDetails whitePlayer) {
            this.whitePlayer = whitePlayer;
        }

        public List<GameDetails> getGames() {
            return games;
        }

        public void setGames(List<GameDetails> games) {
            this.games = games;
        }

        public int getBlackWins() {
            return blackWins;
        }

        public void setBlackWins(int blackWins) {
            this.blackWins = blackWins;
        }

        public int getWhiteWins() {
            return whiteWins;
        }

        public void setWhiteWins(int whiteWins) {
            this.whiteWins = whiteWins;
        }
    }

    public static class PlayerDetails {
        private Constants.PLAYER_TYPE type;
        private Constants.PLAYER_COLOR color;

        public Constants.PLAYER_TYPE getType() {
            return type;
        }

        public void setType(Constants.PLAYER_TYPE type) {
            this.type = type;
        }

        public Constants.PLAYER_COLOR getColor() {
            return color;
        }

        public void setColor(Constants.PLAYER_COLOR color) {
            this.color = color;
        }
    }

    public static class GameDetails {
        private int gameNumber;
        private int winner;

        private long startBoardBlack;
        private long startBoardWhite;

        private List<MoveDetails> moves;

        public int getWinner() {
            return winner;
        }

        public void setWinner(int winner) {
            this.winner = winner;
        }

        public List<MoveDetails> getMoves() {
            return moves;
        }

        public void setMoves(List<MoveDetails> moves) {
            this.moves = moves;
        }

        public int getGameNumber() {
            return gameNumber;
        }

        public void setGameNumber(int gameNumber) {
            this.gameNumber = gameNumber;
        }

        public long getStartBoardBlack() {
            return startBoardBlack;
        }

        public void setStartBoardBlack(long startBoardBlack) {
            this.startBoardBlack = startBoardBlack;
        }

        public long getStartBoardWhite() {
            return startBoardWhite;
        }

        public void setStartBoardWhite(long startBoardWhite) {
            this.startBoardWhite = startBoardWhite;
        }
    }

    public static class MoveDetails {
        private Constants.PLAYER_COLOR color;
        private int position;
        private int searchDepth;
        private Constants.PLAYER_TYPE playerType;
        private long blackBoardAfterMove;
        private long whiteBoardAfterMove;

        private MoveStatistics moveStatistics;

        public Constants.PLAYER_COLOR getColor() {
            return color;
        }

        public void setColor(Constants.PLAYER_COLOR color) {
            this.color = color;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getSearchDepth() {
            return searchDepth;
        }

        public void setSearchDepth(int searchDepth) {
            this.searchDepth = searchDepth;
        }

        public Constants.PLAYER_TYPE getPlayerType() {
            return playerType;
        }

        public void setPlayerType(Constants.PLAYER_TYPE playerType) {
            this.playerType = playerType;
        }

        public long getBlackBoardAfterMove() {
            return blackBoardAfterMove;
        }

        public void setBlackBoardAfterMove(long blackBoardAfterMove) {
            this.blackBoardAfterMove = blackBoardAfterMove;
        }

        public long getWhiteBoardAfterMove() {
            return whiteBoardAfterMove;
        }

        public void setWhiteBoardAfterMove(long whiteBoardAfterMove) {
            this.whiteBoardAfterMove = whiteBoardAfterMove;
        }

        public MoveStatistics getMoveStatistics() {
            return moveStatistics;
        }

        public void setMoveStatistics(MoveStatistics moveStatistics) {
            this.moveStatistics = moveStatistics;
        }
    }

    public static class MoveStatistics {
        private int searchDepth;
        private int searchedNodes;
        private OptionDTO option;
        private long searchTime;


        public int getSearchDepth() {
            return searchDepth;
        }

        public void setSearchDepth(int searchDepth) {
            this.searchDepth = searchDepth;
        }

        public long getSearchTime() {
            return searchTime;
        }

        public void setSearchTime(long searchTime) {
            this.searchTime = searchTime;
        }

        public OptionDTO getOption() {
            return option;
        }

        public void setOption(OptionDTO option) {
            this.option = option;
        }

        public int getSearchedNodes() {
            return searchedNodes;
        }

        public void setSearchedNodes(int searchedNodes) {
            this.searchedNodes = searchedNodes;
        }
    }
}
