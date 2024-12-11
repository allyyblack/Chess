package ui;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.PlayerGame;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            try {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                this.session = container.connectToServer(this, socketURI);
            } catch (Exception e) {
                System.err.println("WebSocket connection failed: " + e.getMessage());
                e.printStackTrace();
            }

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        LoadGameMessage m = new Gson().fromJson(message, LoadGameMessage.class);
                        notificationHandler.notify(m);

                    } else {
                        notificationHandler.notify(serverMessage);
                    }
                }
            });
        } catch (URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(PlayerGame playerGame, String authToken) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, playerGame.gameID(),null, null, null);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void observeGame(int gameId, String authToken) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.OBSERVE, authToken, gameId, null, null, null);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }



    public void leave(PlayerGame playerGame, String authToken) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, playerGame.gameID(), null, null, playerGame);
            this.session.getBasicRemote().sendText((new Gson().toJson(command)));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
      }

      public void resign(PlayerGame playerGame, String authToken) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, playerGame.gameID(), null, null, null);
            this.session.getBasicRemote().sendText((new Gson().toJson(command)));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
      }

      public void redrawBoard(PlayerGame playerGame, String authToken) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, playerGame.gameID(), null, null, null);
            this.session.getBasicRemote().sendText((new Gson().toJson(command)));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
      }

    public void highlightMoves(PlayerGame playerGame, String authToken, ChessPosition position1) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.REDRAW, authToken, playerGame.gameID(), null, position1, null);
            this.session.getBasicRemote().sendText((new Gson().toJson(command)));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(PlayerGame playerGame, String authToken, ChessMove move) throws ResponseException {
            try {
            var command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, playerGame.gameID(), move, null, null);
            this.session.getBasicRemote().sendText((new Gson().toJson(command)));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}