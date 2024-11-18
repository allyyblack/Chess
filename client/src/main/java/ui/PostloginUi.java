package ui;

import com.google.gson.Gson;
import model.GameData;
import model.PlayerGame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PostloginUi extends ClientUI{
    private final ServerFacade server;
    private String authToken;
    private final Map<Integer, GameData> gameMap = new HashMap<>();

    public PostloginUi(String authToken) {
        this.server = new ServerFacade("http://localhost:8080");
        this.authToken = authToken;
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
                case "creategame" -> createGame(params);
                case "listgames" -> listGames(params);
                case "joingame" -> joinGame(params);
                case "observegame" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout(String... params) throws ResponseException {
            server.logout(authToken);
            return String.format("Goodbye");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            var gameName = params[0];
            var gameData = new GameData(0, null, null, gameName, null);
            server.createGame(gameData, authToken);
            return String.format("Game " + gameName + " created.");
        }
        throw new ResponseException(400, "Expected: <gameName>");
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length < 2) {
            throw new ResponseException(400, "Expected: <id> <color>");
        }

        try {
            int id = Integer.parseInt(params[0]);
            String color = params[1].toUpperCase();
            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                throw new ResponseException(400, "Color must be 'WHITE' or 'BLACK'.");
            }
            var game = gameMap.get(id);
            if (game == null) {
                throw new ResponseException(404, "Game with ID " + id + " not found.");
            }
            var playerGame = new PlayerGame(color, game.gameID());
            server.joinGame(playerGame, authToken);
            return String.format("Successfully joined game '%s' as %s.", game.gameName(), color);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Game ID must be an integer.");
        }
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length < 1) {
            throw new ResponseException(400, "Expected: <id>");
        }

        try {
            int id = Integer.parseInt(params[0]);
            var game = gameMap.get(id);
            if (game == null) {
                throw new ResponseException(404, "Game with ID " + id + " not found.");
            }
            return String.format("Successfully observing game '%s'", game.gameName());
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Game ID must be an integer.");
        }
    }


    public String listGames(String... params) throws ResponseException {
        gameMap.clear();
        var games = server.listGames(authToken);
        var result = new StringBuilder();
        int i = 0;

        for (var game : games) {
            i++;
            gameMap.put(i, game);
            result.append("Game ").append(i).append(": ");
            result.append(game.gameName()).append('\n');
            result.append("White Player: ").append(game.whiteUsername() == null ? "None" : game.whiteUsername()).append('\n');
            result.append("Black Player: ").append(game.blackUsername() == null ? "None" : game.blackUsername()).append('\n');
            result.append('\n');
        }
        return result.toString();
    }


    public String help() {

        return """
                - logout
                - creategame <gameName>
                - listgames
                - playgame
                - observegame
                - quit
                """;
    }
}
