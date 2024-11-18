package ui;

import model.AuthData;
import model.UserData;

import java.util.Arrays;
import com.google.gson.Gson;
import ui.ServerFacade;
import ui.ResponseException;

public class PreloginUi extends ClientUI{
    private final ServerFacade server;
    private final String serverUrl;

    public PreloginUi(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            var username = params[0];
            var password = params[1];
            var userData = new UserData(username, password, null);
             var response = server.login(userData).authToken();
            return String.format("Welcome " + username + "\nYour authToken is " + response);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            var userData = new UserData(username, password, email);
            var response = server.register(userData);
            String authToken = response.authToken();
            return String.format("Welcome " + username);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String help() {
        return """
                - login <username> <password> <email>
                - register <username> <password> <email>
                - quit
                """;
    }
}
