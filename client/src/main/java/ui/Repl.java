package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;



public class Repl {
    private ClientUI client;
    private final String serverUrl;

    public Repl(String serverUrl) {
        this.serverUrl = serverUrl;
        client = new PreloginUi(serverUrl);
    }
    public void switchToPostLogin(String authToken) {
        client = new PostloginUi(authToken);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_BLUE + "Welcome to the game of chess. Register or sign in to start.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(result);
                if (line.contains("login") || line.contains("register") && result.contains("Welcome")) {
                    String key = "Your authToken is ";
                    String newAuth = result.substring(result.indexOf(key) + key.length()).trim();
                    switchToPostLogin(newAuth);
                    System.out.println("\nYou are now logged in.");
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
}
