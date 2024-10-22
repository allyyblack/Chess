package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import dataaccess.DataAccessException;

import java.util.Collection;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // Chess is not very simple

    public String Login(String username, String password) throws DataAccessException {
        String authToken = dataAccess.createAuth();
        return authToken;
    }
}
