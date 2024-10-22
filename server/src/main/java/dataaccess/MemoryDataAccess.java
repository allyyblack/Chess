package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.UUID;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess{

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void createUser() throws DataAccessException {

    }

    @Override
    public UserData getUser(UserData username) throws DataAccessException {
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
    public String createAuth() throws DataAccessException {
        return UUID.randomUUID().toString();
    }

    @Override
    public AuthData getAuth(AuthData authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authToken) throws DataAccessException {

    }
}
