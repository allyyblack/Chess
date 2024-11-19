package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;



public class Repl {
    private ClientUI client;
    private final String serverUrl;
    String newAuth = "";

    public Repl(String serverUrl) {
        this.serverUrl = serverUrl;
        client = new PreloginUi(serverUrl);
    }
    public void switchToPostLogin(String authToken) {
        client = new PostloginUi(authToken);
    }
    public void switchToGameplayUi() {
        client = new GameplayUi();
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
                    switchToGameplayUi();
                    if(line.contains("joingame") && result.contains("BLACK")) {
                        ((GameplayUi) client).main("BLACK");
                    }
                    else {
                        ((GameplayUi) client).main("WHITE");
                    }
                    switchToPostLogin(newAuth);
                }
                if (line.contains("logout") && result.contains("Goodbye")) {
                    switchToPreloginUi();
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg + SET_TEXT_COLOR_RED);
            }
        }
        System.out.println();
    }
}
