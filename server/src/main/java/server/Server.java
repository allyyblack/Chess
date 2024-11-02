package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.MySqlDataAccess;
import dataaccess.UnauthorizedAccessException;
import spark.*;
import service.ChessService;
import model.GameData;
import model.UserData;
import model.PlayerGame;
import dataaccess.DataAccessException;
import java.util.Collection;
import java.util.Map;


public class Server {
    private final ChessService service;

    public Server() throws DataAccessException {
        this.service = new ChessService(new MySqlDataAccess());
    }

    public Server(ChessService service) {
        this.service = service;
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/session", this::login);
        Spark.post("/user", this::register);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.delete("/db", this::clearApplication);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

//    Joins game given a valid authToken, player color, and gameID
    private Object joinGame(Request req, Response res) {
        try {
            var playerGame = new Gson().fromJson(req.body(), PlayerGame.class);
            String authToken = req.headers("Authorization");
            if (playerGame.gameID() <= 0 || playerGame.playerColor() == null) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Error: Game ID is required"));
            }
            service.joinGame(playerGame, authToken);
            res.status(200);
            return new Gson().toJson(Map.of("message", "Joined game"));
        } catch (UnauthorizedAccessException e) {
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        } catch (DataAccessException e) {
            res.status(403);
            return new Gson().toJson(Map.of("message", "Error: already taken"));
        } catch (JsonSyntaxException e) {
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: internal server error"));
        }
    }

//    Clears users, games, and authTokens with no user input
    private Object clearApplication(Request req, Response res) {
        try {
            service.clearApplication();
            res.status(200);

            return new Gson().toJson(Map.of("message", "Application data cleared"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: internal server error"));
        }
    }

//    lists games given a valid authToken
    private Object listGames(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            res.status(200);
            Collection<GameData> games = service.listGames(authToken);
            return new Gson().toJson(Map.of("games", games));
        } catch (UnauthorizedAccessException e) {
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: internal server error"));
        }
    }

//    Creates game given a valid authToken
    private Object createGame(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            var gameInfo = new Gson().fromJson(req.body(), GameData.class);
            String gameName = gameInfo.gameName();
            GameData gameData = service.createGame(gameName, authToken);
            res.status(200);
            return new Gson().toJson(Map.of("gameID", gameData.gameID()));
        } catch (JsonSyntaxException e) {
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        } catch (UnauthorizedAccessException e) {
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: internal server error"));
        }
    }

//    Logs out user given a valid authToken
    private Object logout(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            service.logout(authToken);
            res.status(200);
            return new Gson().toJson(Map.of("message", "Logged out"));
        } catch (UnauthorizedAccessException e) {
                res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: internal server error"));
        }
    }

//    registers user with a username, password, and email
    private Object register(Request req, Response res) throws DataAccessException {
        try {
            var registerInfo = new Gson().fromJson(req.body(), UserData.class);
            if (registerInfo.username() == null || registerInfo.username().isEmpty() ||
                    registerInfo.password() == null || registerInfo.password().isEmpty() ||
                    registerInfo.email() == null || registerInfo.email().isEmpty()) {

                res.status(400);
                return new Gson().toJson(Map.of("message", "Error: username, password, and email are required"));
            }
            service.register(registerInfo.username(), registerInfo.password(), registerInfo.email());
            var authData = service.login(registerInfo.username(), registerInfo.password());
            res.status(200);
            return new Gson().toJson(authData);
        } catch (DataAccessException e) {
            res.status(403);
            return new Gson().toJson(Map.of("message", "Error: already taken"));
        } catch (JsonSyntaxException e) {
            res.status(400);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: internal server error"));
        }
    }

//    logs in user with valid username and password
    private Object login(Request req, Response res) throws DataAccessException {
        try {
            var loginInfo = new Gson().fromJson(req.body(), UserData.class);
            var authData = service.login(loginInfo.username(), loginInfo.password());
            res.status(200);
            return new Gson().toJson(authData);

        } catch (DataAccessException e) {
            res.status(401);
            return new Gson().toJson(Map.of("message", "Error: unauthorized"));
        } catch (JsonSyntaxException e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: bad request"));
        }
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
