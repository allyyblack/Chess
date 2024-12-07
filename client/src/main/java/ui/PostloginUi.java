package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.PlayerGame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import ui.NotificationHandler;
import ui.ServerFacade;
import ui.WebSocketFacade;




import static ui.EscapeSequences.*;

public class PostloginUi extends ClientUI{
    private final ServerFacade server;
    private String authToken;
    public static final Map<Integer, GameData> gameMap = new HashMap<>();
    public ChessGame game;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    String serverUrl = "http://localhost:8080";



    public PostloginUi(String authToken, NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
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
        if (params.length == 1) {
            var gameName = params[0];
            var gameData = new GameData(0, null, null, gameName, null);
            gameData = server.createGame(gameData, authToken);
            System.out.println(SET_TEXT_COLOR_YELLOW + "Game " + gameName + " created.");
            game = gameData.game();
            return String.format("Game " + gameName + " created.\n");
        }
        System.out.println(SET_TEXT_COLOR_RED + "Expected: <gameName>\n");
        throw new ResponseException(400, SET_TEXT_COLOR_RED + "Expected: <gameName>\n");
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length == 2) {
            try {
                int id = Integer.parseInt(params[0]);
                String color = params[1].toUpperCase();
                if (!color.equals("WHITE") && !color.equals("BLACK")) {
                    System.out.println(SET_TEXT_COLOR_RED + "Color must be 'WHITE' or 'BLACK'.\n");
                    throw new ResponseException(400, SET_TEXT_COLOR_RED + "Color must be 'WHITE' or 'BLACK'.\n");
                }
                var game = gameMap.get(id);
                if (game == null) {
                    System.out.println(SET_TEXT_COLOR_RED + "Game with ID " + id + " not found.\n");
                    throw new ResponseException(404, SET_TEXT_COLOR_RED + "Game with ID " + id + " not found.\n");
                }
                var playerGame = new PlayerGame(color, game.gameID());
                server.joinGame(playerGame, authToken);
                try {
                    ws = new WebSocketFacade(serverUrl, notificationHandler);
                    ws.joinGame(playerGame, authToken);
                } catch (Exception e) {
                    System.err.println("WebSocket connection failed: " + e.getMessage());
                    e.printStackTrace();
                }
                System.out.println(SET_TEXT_COLOR_GREEN + "Successfully joined game " + game.gameName() + " as " + color);
                return String.format(SET_TEXT_COLOR_GREEN + "Successfully joined game '%s' as '%s'", game.gameName(), color);
            } catch (NumberFormatException e) {
                System.out.println(SET_TEXT_COLOR_RED + "Game ID must be an integer. \n");
                throw new ResponseException(400, SET_TEXT_COLOR_RED + "Game ID must be an integer. \n");
            }
        }
        System.out.println(SET_TEXT_COLOR_RED + "invalid input");
        System.out.println(SET_TEXT_COLOR_RED + "Expected: <id> <color>\n");
        throw new ResponseException(400, SET_TEXT_COLOR_RED + "Expected: <id> <color>\n");
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length < 1) {
            System.out.println(SET_TEXT_COLOR_RED + "Expected: <id>");
            throw new ResponseException(400,SET_TEXT_COLOR_RED + "Expected: <id>");
        }

        try {
            int id = Integer.parseInt(params[0]);
            var game = gameMap.get(id);
            if (game == null) {
                System.out.println(SET_TEXT_COLOR_RED + "Game with ID " + id + " not found.\n");
                throw new ResponseException(404, SET_TEXT_COLOR_RED + "Game with ID " + id + " not found.\n");
            }
            System.out.println(SET_TEXT_COLOR_GREEN + "Successfully observing game " + game.gameName());
            return String.format("Successfully observing game '%s'", game.gameName() + "\n");
        } catch (NumberFormatException e) {
            System.out.println(SET_TEXT_COLOR_RED + "Game ID must be an integer.\n");
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
                - joingame <id> <color>
                - observegame
                - quit
                """);
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
