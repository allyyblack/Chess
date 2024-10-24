package service;

import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedAccessException;
import model.AuthData;
import model.GameData;
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
        service.CreateGame(gameName, validAuthToken.authToken());

        service.ClearApplication();

        Assertions.assertTrue(memoryDataAccess.listGames().isEmpty(), "Games should be cleared");
        Assertions.assertNull(memoryDataAccess.getUser("username"), "User should be cleared");
        Assertions.assertNull(memoryDataAccess.getAuth(validAuthToken.authToken()), "Auth token should be cleared");
    }

    @Test
    @DisplayName("Register")
    public void testRegister_Success() throws DataAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        UserData userData = service.Register(username, password, email);
        Assertions.assertEquals(username, userData.username(), "Username should match the provided username");
    }

    @Test
    @DisplayName("Register with taken username")
    public void testRegister_Failure() throws DataAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.Register(username, password, email);
        assertThrows(DataAccessException.class, () -> {
            service.Register(username, "difpassword", "difemail");
        });
    }

    @Test
    @DisplayName("Login")
    public void testLogin_Success() throws DataAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.Register(username, password, email);
        AuthData authData = service.Login(username, password);
        Assertions.assertEquals(username, authData.username(), "The username should match after login");
    }

    @Test
    @DisplayName("Login with wrong password")
    public void testLogin_Failure() throws DataAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.Register(username, password, email);
        AuthData authData = service.Login(username, password);
        assertThrows(DataAccessException.class, () -> {
            service.Login(username, "wrong password");
        });
    }

    @Test
    @DisplayName("Logout")
    public void testLogout_Success() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.Register(username, password, email);
        AuthData authData = service.Login(username, password);
        service.Logout(authData.authToken());
        Assertions.assertThrows(UnauthorizedAccessException.class, () -> {
            service.Logout(authData.authToken());
        });
    }

    @Test
    @DisplayName("Logout with invalid authToken")
    public void testLogout_Failure() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.Register(username, password, email);
        AuthData authData = service.Login(username, password);
        Assertions.assertThrows(UnauthorizedAccessException.class, () -> {
            service.Logout("invalid authToken");
        });
    }

    @Test
    @DisplayName("List Games")
    public void testListGames_Success() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.Register(username, password, email);
        AuthData authData = service.Login(username, password);
        service.CreateGame("gameName", authData.authToken());
        service.CreateGame("gameName2", authData.authToken());
        Collection<GameData> games = service.ListGames(authData.authToken());
        Assertions.assertEquals(2, games.size(), "There should be 2 games in the list");
    }

    @Test
    @DisplayName("List Games with invalid authToken")
    public void testListGames_Failure() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        service.Register(username, password, email);
        AuthData authData = service.Login(username, password);
        service.CreateGame("gameName", authData.authToken());
        service.CreateGame("gameName2", authData.authToken());
        Collection<GameData> games = service.ListGames(authData.authToken());
        Assertions.assertThrows(UnauthorizedAccessException.class, () -> {
            service.ListGames("invalid authToken");
        });
    }


    @Test
    @DisplayName("Create Game")
    public void testCreateGame_Success() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String gameName = "Test Game";
        AuthData validAuthToken = memoryDataAccess.createAuth("username");
        GameData gameData = service.CreateGame(gameName, validAuthToken.authToken());

        Assertions.assertEquals(gameName, gameData.gameName(), "Game name should match the provided name");
    }

    @Test
    @DisplayName("Create Game with Invalid Authtoken")
    public void testCreateGame_Failure() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String gameName = "Test Game";
        String invalidAuthToken = "invalid token";

        assertThrows(UnauthorizedAccessException.class, () -> {
            service.CreateGame(gameName, invalidAuthToken);
        });
    }

    @Test
    @DisplayName("Join Game")
    public void testJoinGame_Success() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        String gameName = "gamename";
        service.Register(username, password, email);
        AuthData authData = service.Login(username, password);
        GameData gameData = service.CreateGame(gameName, authData.authToken());
        PlayerGame playerGame = new PlayerGame("BLACK", gameData.gameID());
        service.JoinGame(playerGame, authData.authToken());
        GameData updatedGame = memoryDataAccess.getGame(gameData.gameID());
        Assertions.assertEquals(authData.username(), updatedGame.blackUsername(), "The BLACK player should match the logged-in user.");
    }

    @Test
    @DisplayName("Join Game with same color")
    public void testJoinGame_Failure() throws DataAccessException, UnauthorizedAccessException {
        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        ChessService service = new ChessService(memoryDataAccess);
        String username = "username";
        String password = "password";
        String email = "email";
        String gameName = "gamename";
        service.Register(username, password, email);
        AuthData authData = service.Login(username, password);
        GameData gameData = service.CreateGame(gameName, authData.authToken());
        PlayerGame playerGame = new PlayerGame("BLACK", gameData.gameID());
        service.JoinGame(playerGame, authData.authToken());
        GameData updatedGame = memoryDataAccess.getGame(gameData.gameID());
        assertThrows(DataAccessException.class, () -> {
            service.JoinGame(playerGame, authData.authToken());
        });
    }
}