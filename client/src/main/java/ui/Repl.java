package ui;

import model.Game_Data;
import websocket.messages.ServerMessage;

import java.util.Scanner;
import static ui.EscapeSequences.*;


public class Repl implements NotificationHandler {
    private ClientUI client;
    private final String serverUrl;
    String newAuth = "";

    public Repl(String serverUrl) {
        this.serverUrl = serverUrl;
        client = new PreloginUi(serverUrl);
    }
    public void switchToPostLogin(String authToken) {
        client = new PostloginUi(authToken, this);
    }
    public void switchToGameplayUi(String authToken, Game_Data game, String color) {
        var ws = PostloginUi.ws; // Retrieve the WebSocket instance
        client = new GameplayUi(authToken, game, color, this, ws);
    }
    public void switchToPreloginUi() {
        client = new PreloginUi(serverUrl);
    }


    public void run() {
        System.out.println(SET_TEXT_COLOR_BLUE + "Welcome to the game of chess. Register or sign in to start. \n");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
//                System.out.print(result);
                if ((line.contains("login") || line.contains("register")) && result.contains("Welcome")) {
                    String key = "Your authToken is ";
                    newAuth = result.substring(result.indexOf(key) + key.length()).trim();
                    switchToPostLogin(newAuth);
                    System.out.println(SET_TEXT_COLOR_GREEN + "\nYou are now logged in.\n");
                }
                if ((line.contains("joingame") || line.contains("observegame")) && result.contains("Successful")) {
                    String[] parts = line.split(" ");
                    String color = parts[2];
                    int gameNumber = Integer.parseInt(parts[1]);
                    var game = PostloginUi.gameMap.get(gameNumber);
                    switchToGameplayUi(newAuth, game, color);
                }
                if (line.contains("logout") && result.contains("Goodbye")) {
                    switchToPreloginUi();
                }
                if (line.equalsIgnoreCase("leave")) {
                    switchToPostLogin(newAuth);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg + SET_TEXT_COLOR_RED);
            }
        }
        System.out.println();
    }

    public void notify(ServerMessage notification) {
        System.out.println(SET_TEXT_COLOR_RED + notification.getMessage());

        printPrompt();

    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN + "\n");
    }
}
