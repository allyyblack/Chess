package model;

import chess.ChessGame;

public record Game_Data(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public Game_Data withWhiteUsername(String newWhiteUsername) {
        return new Game_Data(this.gameID, newWhiteUsername, this.blackUsername, this.gameName, this.game);
    }

    public Game_Data withBlackUsername(String newBlackUsername) {
        return new Game_Data(this.gameID, this.whiteUsername, newBlackUsername, this.gameName, this.game);
    }
}
