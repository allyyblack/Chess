package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryDataAccess implements DataAccess{
    final private Collection<UserData> users = new HashSet<>();
    final private Collection<AuthData> tokens = new HashSet<>();


    @Override
    public void clear() throws DataAccessException {

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
    public void createGame() throws DataAccessException {

    }

    @Override
    public GameData getGame(GameData gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData gameID) throws DataAccessException {

    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        String token =  UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        tokens.add(authData);
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
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

