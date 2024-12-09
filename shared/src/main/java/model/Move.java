package model;

import chess.ChessMove;

public class Move {
    ChessMove m;
    int gameId;
    public Move(ChessMove m, int gameId) {
        this.m = m;
        this.gameId = gameId;
    }

    public ChessMove getMove() {
        return m;
    }

    public int getGameId() {
        return gameId;
    }
}
