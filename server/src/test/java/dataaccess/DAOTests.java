package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import service.ChessService;




public class DAOTests {


//    UserData getUser(String username) throws DataAccessException;
//
//    GameData getGame(int gameID) throws DataAccessException;
//
//    void updateGame(int gameID, String authToken, String color) throws DataAccessException;
//
//    AuthData createAuth(String username) throws DataAccessException;
//
//    AuthData getAuth(String authToken) throws DataAccessException;
//
//    void deleteAuth(AuthData authToken) throws DataAccessException;









@Test
    @DisplayName("Clear Application")
    public void testClearApplication() throws DataAccessException, UnauthorizedAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        ChessService service = new ChessService(mySqlDataAccess);
        String gameName = "Test Game";
        AuthData validAuthToken = mySqlDataAccess.createAuth("username");
        service.createGame(gameName, validAuthToken.authToken());

        service.clearApplication();

        Assertions.assertTrue(mySqlDataAccess.listGames().isEmpty(), "Games should be cleared");
        Assertions.assertNull(mySqlDataAccess.getUser("username"), "User should be cleared");
        Assertions.assertNull(mySqlDataAccess.getAuth(validAuthToken.authToken()), "Auth token should be cleared");
    }

    @Test
    @DisplayName("List Games")
    public void testListGamesSuccess() throws DataAccessException, UnauthorizedAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        ChessService service = new ChessService(mySqlDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.register(username, password, email);
        AuthData authData = service.login(username, password);
        service.createGame("gameName", authData.authToken());
        service.createGame("gameName2", authData.authToken());
        Collection<GameData> games = service.listGames(authData.authToken());
        Assertions.assertEquals(2, games.size(), "There should be 2 games in the list");
    }

    @Test
    @DisplayName("List Games with invalid authToken")
    public void testListGamesFailure() throws DataAccessException, UnauthorizedAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        ChessService service = new ChessService(mySqlDataAccess);
        String username = "username555555";
        String password = "password";
        String email = "email";
        service.register(username, password, email);
        AuthData authData = service.login(username, password);
        service.createGame("gameName", authData.authToken());
        service.createGame("gameName2", authData.authToken());
        Assertions.assertThrows(UnauthorizedAccessException.class, () -> {
            service.listGames("invalid authToken");
        });
    }


    @Test
    @DisplayName("Create Game")
    public void testCreateGameSuccess() throws DataAccessException, UnauthorizedAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        ChessService service = new ChessService(mySqlDataAccess);
        String gameName = "Test Game";
        AuthData validAuthToken = mySqlDataAccess.createAuth("username");
        GameData gameData = service.createGame(gameName, validAuthToken.authToken());

        Assertions.assertEquals(gameName, gameData.gameName(), "Game name should match the provided name");
    }

    @Test
    @DisplayName("Create Game with Invalid Auth Token")
    public void testCreateGameWithInvalidAuthToken() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        ChessService service = new ChessService(mySqlDataAccess);
        String gameName = "Test Game";
        String invalidAuthToken = "invalid token";

        assertThrows(UnauthorizedAccessException.class, () -> {
            service.createGame(gameName, invalidAuthToken);
        }, "Expected UnauthorizedAccessException for invalid auth token");
    }

    @Test
    @DisplayName("Create User Successfully")
    public void testCreateUserSuccess() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        UserData userData = new UserData("username1234", "password", "email");
        UserData success = mySqlDataAccess.createUser(userData);
        Assertions.assertEquals("username1234", success.username(), "Username should match the provided username");
    }

    @Test
    @DisplayName("Create User with Duplicate Username")
    public void testCreateUserWithDuplicateUsername() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        ChessService service = new ChessService(mySqlDataAccess);
        UserData userData = new UserData("username123", "password", "email");
        mySqlDataAccess.createUser(userData);
        UserData duplicateUserData = new UserData("username123", "newpassword", "newemail");
        assertThrows(DataAccessException.class, () -> {
            mySqlDataAccess.createUser(duplicateUserData);
        }, "Expected DataAccessException for duplicate username");
    }

    @Test
    @DisplayName("Get User")
    public void testGetUserSuccess() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        UserData userData = new UserData("username111", "password", "email");
        mySqlDataAccess.createUser(userData);
        UserData retrievedUser = mySqlDataAccess.getUser("username");
        Assertions.assertNotNull(retrievedUser, "Retrieved user should not be null");
    }
}