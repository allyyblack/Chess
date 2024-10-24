package service;

import dataaccess.DataAccess;
import dataaccess.UnauthorizedAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.PlayerGame;
import dataaccess.DataAccessException;

import java.util.Collection;
import java.util.Objects;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // Chess is not very simple

    public AuthData Login(String username, String password) throws DataAccessException {
        UserData userData = new UserData(username, password, null);
        UserData foundUser = dataAccess.getUser(userData.username());
        if (foundUser == null || !Objects.equals(foundUser.password(), password)) {
            throw new DataAccessException("");
        }
        return dataAccess.createAuth(username);
    }

    public UserData Register(String username, String password, String email) throws DataAccessException {
        UserData userInfo = new UserData(username, password, email);
        UserData foundUser = dataAccess.getUser(userInfo.username());
        if (foundUser != null) {
            throw new DataAccessException("");
        }
        return dataAccess.createUser(userInfo);
    }

    public GameData CreateGame(String gameName, String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("");
        }
        return dataAccess.createGame(gameName);
    }

    public void JoinGame(PlayerGame playerGame, String authToken) throws DataAccessException, UnauthorizedAccessException {
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

    public void ClearApplication() {
        dataAccess.clear();
    }

    public Collection<GameData> ListGames(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("");
        }
        return dataAccess.listGames();

    }

    public void Logout(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("");
        }
        dataAccess.deleteAuth(authData);
    }
}
