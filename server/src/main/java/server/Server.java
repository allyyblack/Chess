package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.MemoryDataAccess;
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

    public Server() {
        this.service = new ChessService(new MemoryDataAccess());
    }

    public Server(ChessService service) {
        this.service = service;
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/session", this::Login);
        Spark.post("/user", this::Register);
        Spark.delete("/session", this::Logout);
        Spark.post("/game", this::CreateGame);
        Spark.get("/game", this::ListGames);
        Spark.delete("/db", this::ClearApplication);
        Spark.put("/game", this::JoinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

//    Joins game given a valid authToken, player color, and gameID
    private Object JoinGame(Request req, Response res) {
        try {
            var playerGame = new Gson().fromJson(req.body(), PlayerGame.class);
            String authToken = req.headers("Authorization");
            if (playerGame.gameID() <= 0 || playerGame.playerColor() == null) {
                res.status(400);
                return new Gson().toJson(Map.of("message", "Error: Game ID is required"));
            }
            service.JoinGame(playerGame, authToken);
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
    private Object ClearApplication(Request req, Response res) {
        try {
            service.ClearApplication();
            res.status(200);
            return new Gson().toJson(Map.of("message", "Application data cleared"));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: internal server error"));
        }
    }

//    lists games given a valid authToken
    private Object ListGames(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            res.status(200);
            Collection<GameData> games = service.ListGames(authToken);
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
    private Object CreateGame(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            var gameInfo = new Gson().fromJson(req.body(), GameData.class);
            String gameName = gameInfo.gameName();
            GameData gameData = service.CreateGame(gameName, authToken);
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
    private Object Logout(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            service.Logout(authToken);
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
    private Object Register(Request req, Response res) throws DataAccessException {
        try {
            var registerInfo = new Gson().fromJson(req.body(), UserData.class);
            if (registerInfo.username() == null || registerInfo.username().isEmpty() ||
                    registerInfo.password() == null || registerInfo.password().isEmpty() ||
                    registerInfo.email() == null || registerInfo.email().isEmpty()) {

                res.status(400);
                return new Gson().toJson(Map.of("message", "Error: username, password, and email are required"));
            }
            service.Register(registerInfo.username(), registerInfo.password(), registerInfo.email());
            var authData = service.Login(registerInfo.username(), registerInfo.password());
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
    private Object Login(Request req, Response res) throws DataAccessException {
        try {
            var loginInfo = new Gson().fromJson(req.body(), UserData.class);
            var authData = service.Login(loginInfo.username(), loginInfo.password());
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
