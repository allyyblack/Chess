package ui;

import com.google.gson.Gson;
import model.GameData;
import model.PlayerGame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.*;

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
            System.out.println(SET_TEXT_BLINKING + "Goodbye\n");
            return String.format("Goodbye\n");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            var gameName = params[0];
            var gameData = new GameData(0, null, null, gameName, null);
            server.createGame(gameData, authToken);
            System.out.println(SET_TEXT_COLOR_YELLOW + "Game " + gameName + " created.");
            return String.format("Game " + gameName + " created.\n");
        }
        throw new ResponseException(400, SET_TEXT_COLOR_RED + "Expected: <gameName>\n");
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length < 2) {
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "Expected: <id> <color>\n");
        }

        try {
            int id = Integer.parseInt(params[0]);
            String color = params[1].toUpperCase();
            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                throw new ResponseException(400, SET_TEXT_COLOR_RED + "Color must be 'WHITE' or 'BLACK'.\n");
            }
            var game = gameMap.get(id);
            if (game == null) {
                throw new ResponseException(404, SET_TEXT_COLOR_RED + "Game with ID " + id + " not found.\n");
            }
            var playerGame = new PlayerGame(color, game.gameID());
            server.joinGame(playerGame, authToken);
            System.out.println(SET_TEXT_COLOR_GREEN + "Successfully joined game " + game.gameName() + " as " + color);
            return String.format(SET_TEXT_COLOR_GREEN + "Successfully joined game '%s' as '%s'", game.gameName(), color);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "Game ID must be an integer. \n");
        }
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length < 1) {
            throw new ResponseException(400,SET_TEXT_COLOR_RED + "Expected: <id>");
        }

        try {
            int id = Integer.parseInt(params[0]);
            var game = gameMap.get(id);
            if (game == null) {
                throw new ResponseException(404, SET_TEXT_COLOR_RED + "Game with ID " + id + " not found.\n");
            }
            System.out.println(SET_TEXT_COLOR_GREEN + "Successfully observing game " + game.gameName());
            return String.format("Successfully observing game '%s'", game.gameName() + "\n");
        } catch (NumberFormatException e) {
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "Game ID must be an integer.\n");
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
        System.out.println(result.toString());
        return result.toString();
    }


    public String help() {

        System.out.println( """
                - logout
                - creategame <gameName>
                - listgames
                - playgame
                - observegame
                - quit
                """);
        return null;
    }
}
