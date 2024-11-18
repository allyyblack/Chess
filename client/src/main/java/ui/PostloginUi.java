package ui;

import com.google.gson.Gson;
import model.GameData;

import java.util.Arrays;

public class PostloginUi extends ClientUI{
    private final ServerFacade server;
    private final String serverUrl;

    public PostloginUi() {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
                case "create game" -> createGame(params);
                case "list games" -> listGames(params);
//                case "play game" -> playGame(params);
//                case "observe game" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout(String... params) throws ResponseException {
        if (params.length >= 1) {
            var authToken = params[0];
            server.logout(authToken);
            return String.format("Goodbye");
        }
        throw new ResponseException(400, "Expected: <authToken>");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            var authToken = params[0];
            var gameName = params[1];
            var gameData = new GameData(0, null, null, gameName, null);
            server.createGame(gameData, authToken);
            return String.format("Game " + gameName + " created.");
        }
        throw new ResponseException(400, "Expected: <authToken> <gameName>");
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length >= 1) {
            var authToken = params[0];
            var games = server.listGames();
            var result = new StringBuilder();
            var gson = new Gson();
            for (var game : games) {
                result.append(gson.toJson(game)).append('\n');
            }
            return result.toString();
        }
        throw new ResponseException(400, "Expected: <authToken>");
    }
    public String help() {

        return """
                - logout <authToken>
                - create game <authToken> <gameName>
                - list games <authToken>
                - play game
                - observe game
                - quit
                """;
    }
}