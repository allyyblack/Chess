
package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
    public void onMessage(Session session, String message) throws IOException, DataAccessException, InvalidMoveException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        try {
            String user = service.getUser(action.getAuthToken());
        } catch (DataAccessException e) {
                String user = action.getAuthToken();
            }
        // IF INVALID, you need to send an errorMessage to the current client

        switch (action.getCommandType()) {
            case CONNECT -> connect(action.getAuthToken(), action.getGameID(), session);
            case MAKE_MOVE -> makeMove(action.getAuthToken(), action.getGameID(), action.getMove());
            case LEAVE -> leave(action.getAuthToken(), action.getGameID());
            case RESIGN -> resign(action.getAuthToken(), action.getGameID());
        }
    }

    private void makeMove(String authToken, int gameId, ChessMove move) throws IOException, DataAccessException, InvalidMoveException {
        String user = service.getUser(authToken);

        if(!service.isMoveValid(gameId,move)) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: invalid move");
            connections.broadcastToUser(user, error);
            return;
        }
        if(service.isGameEnded(gameId)) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: You can't move in a finished game");
            connections.broadcastToUser(user, error);
            return;
        }
        if (!service.isAuthTokenValid(authToken)) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: Not a valid authtoken");
            connections.broadcastToUser(user, error);
            return;
        }
        ChessGame game = service.getgame(gameId).game();
        String otherUser = service.getOtherUser(authToken, gameId);
        String color = service.getUserColor(gameId, authToken);
        if (!color.equalsIgnoreCase("BLACK") && !color.equalsIgnoreCase("WHITE")) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: You can't make a turn as an observer");
            connections.broadcastToUser(user, error);
            return;
        }
        ChessGame.TeamColor teamTurn = service.getTeamTurn(gameId);

        if (teamTurn != ChessGame.TeamColor.valueOf(color)) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: Not your turn");
            connections.broadcastToUser(user, error);
            return;
        }
            String otherColor = service.getUserColor(gameId, otherUser);
        boolean whiteAtBottom = color.equals("WHITE");
        boolean isInCheck = service.isInCheck(game, ChessGame.TeamColor.valueOf(otherColor));
        boolean isInCheckMate = service.isInCheckmate(game, ChessGame.TeamColor.valueOf(otherColor));
        if (isInCheck || isInCheckMate) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: Game is over");
            connections.broadcastToUser(user, error);
        } else {
            service.changeTeamTurn(gameId);
            var message = String.format("%s made the move FILL IN", user);
            var sendToSelf = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, service.getgame(gameId).game(), whiteAtBottom);
            var sendToOther = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, service.getgame(gameId).game(), !whiteAtBottom);
            var allOtherUsers = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

            connections.broadcastToUser(user, sendToSelf);
            connections.broadcastToGame(user, gameId, sendToOther);
            connections.broadcastToGame(user, gameId, allOtherUsers);
            isInCheck = service.isInCheck(game, ChessGame.TeamColor.valueOf(otherColor));
            isInCheckMate = service.isInCheckmate(game, ChessGame.TeamColor.valueOf(otherColor));
            if (isInCheck) {
                var checkMessage = String.format("move results in check");
                var everrrbody = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMessage);
                connections.broadcastToGame(null, gameId, everrrbody);
            }
            if (isInCheckMate) {

                var checkmateMessage = String.format("move results in checkmate");
                var everrrbody = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkmateMessage);
                //                 var everrrbody = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMessage);

                connections.broadcastToGame(null, gameId, everrrbody);
            }


            //         connections.broadcastToGame(null, everrrbody);

        }
    }

    public boolean isMoveValid(int gameId, ChessMove move) {
        return true;
    }

    public void connect(String authToken, int gameId, Session session) throws IOException, DataAccessException {
        String user;
        try {
            user = service.getUser(authToken);
        } catch(DataAccessException e) {
            user = authToken;
        }
        connections.joinGame(user, gameId, session);
        if (!service.isAuthTokenValid(authToken)) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: Not a valid authtoken");
            connections.broadcastToUser(user, error);
            return;
        }
        if (!service.isGameValid(gameId)) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: Not a real game");
            connections.broadcastToUser(user, error);
            return;
        }
        String color = service.getUserColor(gameId, authToken);
        boolean whiteAtBottom = color.equals("WHITE");
        var message = String.format("%s joined the game as %s", user, color);
        var m = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, service.getgame(gameId).game(), whiteAtBottom);
        var all = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

        connections.broadcastToUser(user, m);
        connections.broadcastToGame(user, gameId, all);
    }

    public void sendErrorMessage(String user, String errorMessage) throws IOException {
        var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, errorMessage);
        connections.broadcastToUser(user, error);
    }


    public void leave(String authToken, int gameId) throws IOException, DataAccessException {
        String user = service.getUser(authToken);
        connections.leaveGame(user, gameId);
        var message = String.format("%s left the game", user);
        var m = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

        connections.broadcastToGame(user, gameId, m);
    }

    public void resign(String authToken, int gameId) throws IOException, DataAccessException {
        ChessGame game = service.getgame(gameId).game();
        String user = service.getUser(authToken);
        if(service.isGameEnded(gameId)) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: You can't resign a finished game");
            connections.broadcastToUser(user, error);
            return;
        }
        String color = service.getUserColor(gameId, authToken);
        if (!color.equalsIgnoreCase("BLACK") && !color.equalsIgnoreCase("WHITE")) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: You can't resign as an observer");
            connections.broadcastToUser(user, error);
            return;
        }
        var message = String.format("%s resigned the game", user);
        var m = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastToGame(null, gameId, m);
        service.endGame(gameId);
    }
}
