package com.mcgreedy.optionothello.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcgreedy.optionothello.engine.Game;
import com.mcgreedy.optionothello.gamemanagement.Player;
import com.mcgreedy.optionothello.utils.Constants;

import java.util.List;
import java.util.UUID;


public class SaveGameDTO {

    @JsonProperty("Game")
    public GameDetails game;

    public GameDetails getGame() {
        return game;
    }

    public void setGame(GameDetails game){
        this.game = game;
    }

    public static class GameDetails{
        private UUID id;
        private PlayerDetails blackPlayer;
        private PlayerDetails whitePlayer;
        private long startBoardBlack;
        private long startBoardWhite;
        private List<MoveDetails> moves;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
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

        public List<MoveDetails> getMoves() {
            return moves;
        }

        public void setMoves(List<MoveDetails> moves) {
            this.moves = moves;
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

    public static class PlayerDetails{
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

    public static class MoveDetails{
        private Constants.PLAYER_COLOR color;
        private int position;
        private int searchDepth;
        private Constants.PLAYER_TYPE playerType;
        private long blackBoardAfterMove;
        private long whiteBoardAfterMove;

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
    }


}
