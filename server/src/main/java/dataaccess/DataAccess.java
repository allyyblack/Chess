package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.Collection;

public interface DataAccess {
    void clear();

    UserData createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(int gameID, String AuthToken, String color) throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(AuthData authToken) throws DataAccessException;
}
