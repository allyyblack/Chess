
package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.PlayerGame;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import service.ChessService;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final ChessService service;
    private PlayerGame playerGame;

    public WebSocketHandler(ChessService service) {
        this.service = service;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        // FIXME: VALIDATE AUTH TOKEN AND GAMEID
        // IF INVALID, you need to send an errorMessage to the current client
        // EX: connections.broadcastToUser(currentUser, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: something bad happened"));

        switch (action.getCommandType()) {
            case CONNECT -> connect(action.getAuthToken(), action.getGameID(), session);
            case MAKE_MOVE -> makeMove(action.getAuthToken(), action.getGameID());
            case LEAVE -> leave(action.getAuthToken(), action.getGameID());
//            case RESIGN -> resign(action.user);
        }
    }

    private void makeMove(String authToken, int gameId) throws IOException, DataAccessException {
//        var sendBoard = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, board);
        String user = service.getUser(authToken);

        var message = String.format("%s made the move FILL IN", user);
        var sendToAllUsersIncludingCurrent = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, service.getgame(gameId).game(), true); //FIXME change true to whether white or black
        var allOtherUsers = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

        connections.broadcastToGame(null, sendToAllUsersIncludingCurrent);
        connections.broadcastToGame(user, allOtherUsers);

        // FIXME If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.
        //                 var checkMessage = String.format("move results in check, checkmate, or stalemate")
        //                 var everrrbody = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMessage);
        //         connections.broadcastToGame(null, everrrbody);


    }

    public void connect(String authToken, int gameId, Session session) throws IOException, DataAccessException {
        String user = service.getUser(authToken);
        connections.joinGame(user, gameId, session);
        var message = String.format("%s joined the game as %s", user, service.getgame(gameId).blackUsername()); //FIXME add in what color is joining or observer
        var m = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, service.getgame(gameId).game(), true); //FIXME change true to whether white or black
        var all = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

        connections.broadcastToUser(user, m);
        connections.broadcastToGame(user, all);
    }

    public void leave(String authToken, int gameId) throws IOException, DataAccessException {
        String user = service.getUser(authToken);
        connections.leaveGame(user, gameId);
        var message = String.format("%s left the game", user);
        var m = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

        connections.broadcastToGame(user, m);
    }
}
