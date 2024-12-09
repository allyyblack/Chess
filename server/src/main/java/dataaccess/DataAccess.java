package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.Collection;

public interface DataAccess {
    void clear() throws DataAccessException;

    UserData createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    String getAuthToken(String username) throws DataAccessException;

    boolean isGameValid(int gameID) throws DataAccessException;

    boolean isAuthTokenValid(String authToken) throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(int gameID, String authToken, String color) throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    GameData makeMove(ChessMove move, int gameID) throws DataAccessException;

    boolean isInCheck(ChessGame game, ChessGame.TeamColor color);

    void updateGameState(int gameID, ChessGame chessGame) throws DataAccessException;

    void deleteAuth(AuthData authToken) throws DataAccessException;

    void removeUser(int id, String color) throws DataAccessException;
}
