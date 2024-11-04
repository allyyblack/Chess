package dataaccess;

import chess.ChessGame;
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

    @Test
    @DisplayName("Clear Application")
    public void testClearApplication() throws DataAccessException, UnauthorizedAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        ChessService service = new ChessService(mySqlDataAccess);
        UserData userData = new UserData("thisuser", "password", "email");
        mySqlDataAccess.createUser(userData);
        AuthData validAuthToken = mySqlDataAccess.createAuth("thisuser");
        mySqlDataAccess.createGame("test");
        mySqlDataAccess.clear();
        Assertions.assertTrue(mySqlDataAccess.listGames().isEmpty(), "Games should be cleared");
    }

    @Test
    @DisplayName("List Games")
    public void testListGamesSuccess() throws DataAccessException, UnauthorizedAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        UserData userData = new UserData("usernameee", "password", "email");
        mySqlDataAccess.createUser(userData);
        AuthData authData = mySqlDataAccess.createAuth("usernameee");
        mySqlDataAccess.createGame("name");
        mySqlDataAccess.createGame("name2");
        Collection<GameData> games = mySqlDataAccess.listGames();
        Assertions.assertTrue(games.stream().anyMatch(game -> game.gameName().equals("name")), "Game 'name' should be in the list");
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
    public void testCreateGameFailure() throws DataAccessException {
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
    public void testCreateUserFailure() throws DataAccessException {
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

    @Test
    @DisplayName("Get Non User")
    public void testGetUserFailure() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        UserData retrievedUser = mySqlDataAccess.getUser("nonexistentUsername");
        Assertions.assertNull(retrievedUser, "Retrieved user should be null for a nonexistent username");
    }

    @Test
    @DisplayName("Get Game")
    public void testGetGameSuccess() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        GameData createdGame = mySqlDataAccess.createGame("test"); // Assuming createGame returns the created GameData with an assigned ID
        GameData retrievedGame = mySqlDataAccess.getGame(createdGame.gameID());
        Assertions.assertNotNull(retrievedGame, "Retrieved game should not be null");
    }

    @Test
    @DisplayName("Get Game Not Found")
    public void testGetGameFailure() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        GameData retrievedGame = mySqlDataAccess.getGame(9999);
        Assertions.assertNull(retrievedGame, "Retrieved game should be null for a nonexistent game ID");
    }

    @Test
    @DisplayName("Update Game")
    public void testUpdateGameSuccess() throws DataAccessException, UnauthorizedAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        UserData userData = new UserData("username", "password", "email");
        mySqlDataAccess.createUser(userData);
        AuthData authData = mySqlDataAccess.createAuth("username");
        GameData createdGame = mySqlDataAccess.createGame("test");
        int gameID = createdGame.gameID();
        mySqlDataAccess.updateGame(gameID, authData.authToken(), "WHITE");
        GameData updatedGame = mySqlDataAccess.getGame(gameID);
        Assertions.assertEquals(userData.username(), updatedGame.whiteUsername(), "The white player's username should match");
    }

    @Test
    @DisplayName("Update Game with Invalid Auth Token")
    public void testUpdateGameFailure() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        UserData userData = new UserData("USERUSER", "password", "email");
        mySqlDataAccess.createUser(userData);
        GameData createdGame = mySqlDataAccess.createGame("test");
        int gameID = createdGame.gameID();
        String invalidAuthToken = null;
        assertThrows(DataAccessException.class, () -> {
            mySqlDataAccess.updateGame(gameID, invalidAuthToken, "WHITE");
        }, "Expected Exception for invalid auth token");
    }

    @Test
    @DisplayName("Create Auth Token")
    public void testCreateAuthSuccess() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        UserData userData = new UserData("username123456", "password", "email");
        mySqlDataAccess.createUser(userData);
        AuthData authData = mySqlDataAccess.createAuth("username");
        Assertions.assertNotNull(authData, "AuthData should not be null");
    }

    @Test
    @DisplayName("Create Auth Token for Non User")
    public void testCreateAuthFailure() {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        Assertions.assertThrows(DataAccessException.class, () -> {
            mySqlDataAccess.createAuth("nonExistentUser");
        }, "Expected DataAccessException for non-existent username");
    }

    @Test
    @DisplayName("Get Auth Token")
    public void testGetAuthSuccess() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        UserData userData = new UserData("userusesr", "password", "email");
        mySqlDataAccess.createUser(userData);
        AuthData authData = mySqlDataAccess.createAuth("username");
        AuthData getAuth = mySqlDataAccess.getAuth(authData.authToken());
        Assertions.assertNotNull(getAuth, "Retrieved AuthData should not be null");
    }

    @Test
    @DisplayName("Get Auth Token with Invalid Token")
    public void testGetAuthFailure() {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        String invalidAuthToken = null;
        Assertions.assertThrows(DataAccessException.class, () -> {
            mySqlDataAccess.getAuth(invalidAuthToken);
        }, "Expected DataAccessException for non-existent auth token");
    }

    @Test
    @DisplayName("Delete Auth Token")
    public void testDeleteAuthSuccess() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        UserData userData = new UserData("userad;obfgklnv", "password", "email");
        mySqlDataAccess.createUser(userData);
        AuthData authData = mySqlDataAccess.createAuth("username");
        mySqlDataAccess.deleteAuth(authData);
        AuthData deletedAuth = mySqlDataAccess.getAuth(authData.authToken());
        Assertions.assertNull(deletedAuth, "Auth token should be null after deletion");
    }

    @Test
    @DisplayName("Delete without valid Auth Token")
    public void testDeleteAuthFailure() throws DataAccessException {
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        AuthData notValidAuth = new AuthData("nope", "username");
        mySqlDataAccess.deleteAuth(notValidAuth);
        AuthData retrievedAuth = mySqlDataAccess.getAuth(notValidAuth.authToken());
        Assertions.assertNull(retrievedAuth, "Auth token should remain null for a non-existent token deletion attempt");
    }

}