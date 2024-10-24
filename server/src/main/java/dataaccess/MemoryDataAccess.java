package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import chess.ChessGame;
import java.util.*;

public class MemoryDataAccess implements DataAccess{
    final private Collection<UserData> users = new HashSet<>();
    final private Collection<AuthData> tokens = new HashSet<>();
    final private Collection<GameData> games = new HashSet<>();
    private int nextId = 1;




    @Override
    public void clear() {
        users.clear();
        tokens.clear();
        games.clear();
    }

    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        users.add(userData);
        return userData;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : users) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData data = new GameData(nextId++, null, null, gameName, game);
        games.add(data);
        return data;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData gameData : games) {
            if (Objects.equals(gameData.gameID(), gameID)) {
                return gameData;
            }
        }
        throw new DataAccessException("Game not found for ID: " + gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games;
    }

    @Override
    public void updateGame(int gameID, String authToken, String color) throws DataAccessException {
        GameData gameData = getGame(gameID);
        AuthData authData = getAuth(authToken);
        if (authToken == null) {
            throw new DataAccessException("authtoken null");
        }

        if (Objects.equals(color, "WHITE")) {
            GameData updatedGame = new GameData(gameID, authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
            games.remove(gameData);
            games.add(updatedGame);
        }
        if (Objects.equals(color, "BLACK")) {
            GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
            games.remove(gameData);
            games.add(updatedGame);
        }
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String token =  UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        tokens.add(authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("authtoken null");
        }
        for (AuthData authData : tokens) {
            if (Objects.equals(authData.authToken(), authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authToken) throws DataAccessException {
        tokens.remove(authToken);
    }
}

