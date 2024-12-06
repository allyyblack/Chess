package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
import dataaccess.UnauthorizedAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.PlayerGame;
import dataaccess.DataAccessException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;
import java.util.Objects;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // Chess is not very simple

    public AuthData login(String username, String password) throws DataAccessException{
        UserData userData = new UserData(username, password, null);
        UserData foundUser = dataAccess.getUser(userData.username());
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        if (foundUser == null || !BCrypt.checkpw(password, foundUser.password())) {
            throw new DataAccessException("");
        }
        return dataAccess.createAuth(username);
    }

    public UserData register(String username, String password, String email) throws DataAccessException {
        UserData userInfo = new UserData(username, password, email);
        UserData foundUser = dataAccess.getUser(userInfo.username());
        if (foundUser != null) {
            throw new DataAccessException("");
        }
        return dataAccess.createUser(userInfo);
    }

    public GameData createGame(String gameName, String authToken) throws DataAccessException, UnauthorizedAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedAccessException("Invalid auth token");
        }
        return dataAccess.createGame(gameName);
    }

    public GameData makeMove(ChessMove move, int gameID, String authToken)
            throws DataAccessException, UnauthorizedAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedAccessException("Invalid auth token");
        }
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found for gameID: " + gameID);
        }

        ChessGame chessGame = gameData.game();
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new DataAccessException("Invalid move: " + e.getMessage());
        }
        dataAccess.updateGameState(gameID, chessGame);
        return dataAccess.getGame(gameID);
    }


    public void joinGame(PlayerGame playerGame, String authToken) throws DataAccessException, UnauthorizedAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedAccessException("Invalid auth token");
        }
        if (Objects.equals(playerGame.playerColor(), "WHITE")) {
            if (dataAccess.getGame(playerGame.gameID()).whiteUsername() != null) {
                throw new DataAccessException("White player slot is already filled");
            } else {
                dataAccess.updateGame(playerGame.gameID(), authToken, playerGame.playerColor());
            }
        }
        if (Objects.equals(playerGame.playerColor(), "BLACK")) {
            if (dataAccess.getGame(playerGame.gameID()).blackUsername() != null) {
                throw new DataAccessException("Black player slot is already filled");
            } else {
                dataAccess.updateGame(playerGame.gameID(), authToken, playerGame.playerColor());
            }
        }
    }

    public void clearApplication() throws DataAccessException {
        dataAccess.clear();
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException, UnauthorizedAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedAccessException("Invalid auth token");
        }
        return dataAccess.listGames();

    }

    public void logout(String authToken) throws DataAccessException, UnauthorizedAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedAccessException("Invalid auth token");
        }
        dataAccess.deleteAuth(authData);
    }
}
