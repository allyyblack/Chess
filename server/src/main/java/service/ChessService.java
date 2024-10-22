package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import dataaccess.DataAccessException;

import java.util.Collection;
import java.util.Objects;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // Chess is not very simple

    public String Login(String username, String password) throws DataAccessException {
        UserData userData = new UserData(username, password, null);
        UserData foundUser = dataAccess.getUser(userData.username());
        if (foundUser == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (!Objects.equals(foundUser.password(), password)) {
            throw new DataAccessException("Error: unauthorized");
        }
        String authToken = dataAccess.createAuth();
        return authToken;
    }

    public void Register(String username, String password, String email) throws DataAccessException {
        UserData UserInfo = new UserData(username, password, email);
        dataAccess.createUser(UserInfo);
    }
}
