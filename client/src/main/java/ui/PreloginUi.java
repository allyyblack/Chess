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
             System.out.println("Welcome " + username);
            return String.format("Welcome " + username + "\nYour authToken is " + response + "\n");
        }
        throw new ResponseException(400, "Expected: <username> <password>\n");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            var userData = new UserData(username, password, email);
            var response = server.register(userData);
            String authToken = response.authToken();
            System.out.println("Welcome " + username);
            return String.format("Welcome " + username + "\nYour authToken is " + authToken + "\n");

            //instead of returning,
            //just print the message without authtoken
            //and then create an instance of postlogin UI here, and run it.
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>\n");
    }

    public String help() {
        return """
                - login <username> <password> <email>
                - register <username> <password> <email>
                - quit
                """;
    }
}
