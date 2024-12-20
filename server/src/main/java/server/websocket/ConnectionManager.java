
package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, ArrayList<Connection>> GAME_CONNECTIONS = new ConcurrentHashMap<>();


    public void joinGame(String user, int gameId, Session session) {
        var connection = new Connection(user, session);
        GAME_CONNECTIONS.computeIfAbsent(gameId, k -> new ArrayList<>()).add(connection); // Add player to the game
        connections.put(user, connection);
    }


    public void leaveGame(String user, int gameId) {
        var gameList = GAME_CONNECTIONS.get(gameId);
        if (gameList != null) {
            gameList.removeIf(c -> c.user.equals(user)); // Remove the player from the game
            if (gameList.isEmpty()) {
                GAME_CONNECTIONS.remove(gameId); // Clean up if the game is empty
            }
        }
        connections.remove(user); // Remove player globally
    }

    public void broadcastToGame(String excludeVisitorName, int gameId, ServerMessage message) throws IOException {
        var gameList = GAME_CONNECTIONS.get(gameId);
        if (gameList == null) {
            return;
        }
            var removeList = new ArrayList<Connection>();
        for (var c : gameList) {
            if (c.session.isOpen()) {
                if (!c.user.equals(excludeVisitorName)) {
                    if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        message = new Gson().fromJson(message.toString(), LoadGameMessage.class);
                    }
                    // check if message
                    c.send(message.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            gameList.remove(c);
            connections.remove(c.user);
        }
    }

    public void broadcastToUser(String user, ServerMessage m) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.user.equals(user)) {
                    if (m.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        m = new Gson().fromJson(m.toString(), LoadGameMessage.class);

                    }
                    c.send(m.toString());

                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.user);
        }
    }
}

