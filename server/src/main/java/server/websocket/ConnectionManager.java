
package server.websocket;

import chess.ChessMove;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
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

    public void broadcastToGame(String excludeVisitorName, int gameID, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.user.equals(excludeVisitorName)) {
                    c.send(message.toString());
                }
                else {
                    if (gameID == 1) {
                        c.send(new LoadGameMessage(ServerMessage.ServerMessageType.ERROR, null, null).toString());
                    }
                    c.send(new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, gameID).toString());
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

