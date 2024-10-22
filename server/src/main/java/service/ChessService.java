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



    public Pet addPet(Pet pet) throws ResponseException {
        if (pet.type() == PetType.DOG && pet.name().equals("fleas")) {
            throw new ResponseException(200, "no dogs with fleas");
        }
        return dataAccess.addPet(pet);
    }
}
