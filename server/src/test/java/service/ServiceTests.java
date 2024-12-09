package service;

import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedAccessException;
import model.AuthData;
import model.Game_Data;
import model.PlayerGame;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import dataaccess.DataAccessException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServiceTests {

    @Test
    @DisplayName("Clear Application")
    public void testClearApplication() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String gameName = "Test Game";
        AuthData validAuthToken = memoryDataAccess.createAuth("username");
        service.createGame(gameName, validAuthToken.authToken());

        service.clearApplication();

        Assertions.assertTrue(memoryDataAccess.listGames().isEmpty(), "Games should be cleared");
        Assertions.assertNull(memoryDataAccess.getUser("username"), "User should be cleared");
        Assertions.assertNull(memoryDataAccess.getAuth(validAuthToken.authToken()), "Auth token should be cleared");
    }

    @Test
    @DisplayName("Register")
    public void testRegisterSuccess() throws DataAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        UserData userData = service.register(username, password, email);
        Assertions.assertEquals(username, userData.username(), "Username should match the provided username");
    }

    @Test
    @DisplayName("Register with taken username")
    public void testRegisterFailure() throws DataAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.register(username, password, email);
        assertThrows(DataAccessException.class, () -> {
            service.register(username, "difpassword", "difemail");
        });
    }

    @Test
    @DisplayName("Login")
    public void testLoginSuccess() throws DataAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.register(username, password, email);
        AuthData authData = service.login(username, password);
        Assertions.assertEquals(username, authData.username(), "The username should match after login");
    }

    @Test
    @DisplayName("Login with wrong password")
    public void testLoginFailure() throws DataAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.register(username, password, email);
        AuthData authData = service.login(username, password);
        assertThrows(DataAccessException.class, () -> {
            service.login(username, "wrong password");
        });
    }

    @Test
    @DisplayName("Logout")
    public void testLogoutSuccess() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.register(username, password, email);
        AuthData authData = service.login(username, password);
        service.logout(authData.authToken());
        Assertions.assertThrows(UnauthorizedAccessException.class, () -> {
            service.logout(authData.authToken());
        });
    }

    @Test
    @DisplayName("Logout with invalid authToken")
    public void testLogoutFailure() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.register(username, password, email);
        AuthData authData = service.login(username, password);
        Assertions.assertThrows(UnauthorizedAccessException.class, () -> {
            service.logout("invalid authToken");
        });
    }

    @Test
    @DisplayName("List Games")
    public void testListGamesSuccess() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.register(username, password, email);
        AuthData authData = service.login(username, password);
        service.createGame("gameName", authData.authToken());
        service.createGame("gameName2", authData.authToken());
        Collection<Game_Data> games = service.listGames(authData.authToken());
        Assertions.assertEquals(2, games.size(), "There should be 2 games in the list");
    }

    @Test
    @DisplayName("List Games with invalid authToken")
    public void testListGamesFailure() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.register(username, password, email);
        AuthData authData = service.login(username, password);
        service.createGame("gameName", authData.authToken());
        service.createGame("gameName2", authData.authToken());
        Collection<Game_Data> games = service.listGames(authData.authToken());
        Assertions.assertThrows(UnauthorizedAccessException.class, () -> {
            service.listGames("invalid authToken");
        });
    }


    @Test
    @DisplayName("Create Game")
    public void testCreateGameSuccess() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String gameName = "Test Game";
        AuthData validAuthToken = memoryDataAccess.createAuth("username");
        Game_Data gameData = service.createGame(gameName, validAuthToken.authToken());

        Assertions.assertEquals(gameName, gameData.gameName(), "Game name should match the provided name");
    }

    @Test
    @DisplayName("Create Game with Invalid Authtoken")
    public void testCreateGameFailure() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String gameName = "Test Game";
        String invalidAuthToken = "invalid token";

        assertThrows(UnauthorizedAccessException.class, () -> {
            service.createGame(gameName, invalidAuthToken);
        });
    }

    @Test
    @DisplayName("Join Game")
    public void testJoinGameSuccess() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        String gameName = "gamename";
        service.register(username, password, email);
        AuthData authData = service.login(username, password);
        Game_Data gameData = service.createGame(gameName, authData.authToken());
        PlayerGame playerGame = new PlayerGame("BLACK", gameData.gameID());
        service.joinGame(playerGame, authData.authToken());
        Game_Data updatedGame = memoryDataAccess.getGame(gameData.gameID());
        Assertions.assertEquals(authData.username(), updatedGame.blackUsername(), "The BLACK player should match the logged-in user.");
    }

    @Test
    @DisplayName("Join Game with same color")
    public void testJoinGameFailure() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        String gameName = "gamename";
        service.register(username, password, email);
        AuthData authData = service.login(username, password);
        Game_Data gameData = service.createGame(gameName, authData.authToken());
        PlayerGame playerGame = new PlayerGame("BLACK", gameData.gameID());
        service.joinGame(playerGame, authData.authToken());
        Game_Data updatedGame = memoryDataAccess.getGame(gameData.gameID());
        assertThrows(DataAccessException.class, () -> {
            service.joinGame(playerGame, authData.authToken());
        });
    }
}