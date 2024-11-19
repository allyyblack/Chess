package client;

import model.AuthData;
import model.GameData;
import model.PlayerGame;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import ui.ResponseException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        facade.clearApplication();
    }

    @Test
    void register() throws Exception {
        var user = new UserData("username", "password", "email");
        AuthData authData = facade.register(user);
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerFailure() {
        var user = new UserData("username", "password", "email");
        assertDoesNotThrow(() -> facade.register(user));
    }

    @Test
    void login() throws Exception {
        var user = new UserData("username", "password", "email");
        facade.register(user);
        AuthData authData = facade.login(user);
        assertNotNull(authData);
    }

    @Test
    void loginFailure() {
        var user = new UserData("email", "password", "email");
        assertThrows(ResponseException.class, () -> facade.login(user));
    }

    @Test
    void logout() throws Exception {
        var user = new UserData("username", "password", "email");
        AuthData authData = facade.register(user);
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    void logoutFailure() {
        assertThrows(ResponseException.class, () -> facade.logout("notreal"));
    }

    @Test
    void createGame() throws Exception {
        var user = new UserData("username", "password", "email");
        AuthData authData = facade.register(user);
        var game = new GameData(999, "username", null, "gamename", null);

        GameData createdGame = facade.createGame(game, authData.authToken());
        assertNotNull(createdGame);
    }

    @Test
    void createGameNegative() {
        var game = new GameData(998, "player4", null, "name", null);
        assertThrows(ResponseException.class, () -> facade.createGame(game, "invalidAuthToken"));
    }
    @Test
    void listGamesPositive() throws Exception {
        var user = new UserData("username", "password", "email");
        AuthData authData = facade.register(user);

        var game1 = new GameData(997, "username", null, "game", null);
        var game2 = new GameData(996, "secondusername", null, "gamee", null);
        facade.createGame(game1, authData.authToken());
        facade.createGame(game2, authData.authToken());

        Collection<GameData> games = facade.listGames(authData.authToken());
        assertEquals(2, games.size());
    }
    @Test
    void listGamesFailure() {
        assertThrows(ResponseException.class, () -> facade.listGames("notreal"));
    }

    @Test
    void joinGame() throws Exception {
        var user = new UserData("username", "password", "email");
        AuthData authData = facade.register(user);

        var game = new GameData(995, "username", null, "game", null);
        GameData createdGame = facade.createGame(game, authData.authToken());

        var playerGame = new PlayerGame("WHITE" , createdGame.gameID());
        PlayerGame joinedGame = facade.joinGame(playerGame, authData.authToken());
        assertNotNull(joinedGame);
    }

    @Test
    void joinGameFailure() {
        var playerGame = new PlayerGame("BLACK", 888);
        assertThrows(ResponseException.class, () -> facade.joinGame(playerGame, "notreal"));
    }
}
