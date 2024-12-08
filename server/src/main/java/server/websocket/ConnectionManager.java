
package server.websocket;

import chess.ChessMove;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, ArrayList<Connection>> gameConnections = new ConcurrentHashMap<>();


    public void joinGame(String user, int gameId, Session session) {
        var connection = new Connection(user, session);
        gameConnections.computeIfAbsent(gameId, k -> new ArrayList<>()).add(connection); // Add player to the game
        connections.put(user, connection);
    }

    public void leaveGame(String user, int gameId) {
        var gameList = gameConnections.get(gameId);
        if (gameList != null) {
            gameList.removeIf(c -> c.user.equals(user)); // Remove the player from the game
            if (gameList.isEmpty()) {
                gameConnections.remove(gameId); // Clean up if the game is empty
            }
        }
        connections.remove(user); // Remove player globally
    }

    public void broadcastToGame(int gameID, ServerMessage message) throws IOException {
        var gameList = gameConnections.get(gameID);
        if (gameList != null) {
            for (var c : gameList) {
                if (c.session.isOpen()) {
                    c.send(message.toString());
                }
            }
        }
    }
}

