package ui;
import chess.ChessMove;
import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.*;
import java.util.Collection;


public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userData) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, userData, AuthData.class, null);
    }

    public AuthData login(UserData userData) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, userData, AuthData.class, null);
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, authToken);
    }

    public GameData createGame(GameData gameData, String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, gameData, GameData.class, authToken);
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        var path = "/game";
        record ListGameResponse(Collection<GameData> games) {
        }
        var response = this.makeRequest("GET", path, null, ListGameResponse.class, authToken);
        return response.games();
    }

    public void clearApplication() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public PlayerGame joinGame(PlayerGame playerGame, String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("PUT", path, playerGame, PlayerGame.class, authToken);
    }

    public void leave(PlayerGame playergame, String authToken) throws ResponseException {
        var path = "/leave";
        this.makeRequest("POST", path, playergame, null, authToken);
    }

    public void makeMove(ChessMove move, int i, String authToken) throws ResponseException {
        var path = "/makeMove";

        this.makeRequest("POST", path, new Move(move, i), null, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);  // Add the authToken to headers
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
