package dataaccess;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    void clear() throws DataAccessException;

    void createUser() throws DataAccessException;

    UserData getUser(UserData username) throws DataAccessException;

    void createGame() throws DataAccessException;

    GameData getGame(GameData gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData gameID) throws DataAccessException;

    String createAuth() throws DataAccessException;

    AuthData getAuth(AuthData authToken) throws DataAccessException;

    void deleteAuth(AuthData authToken) throws DataAccessException;
}
