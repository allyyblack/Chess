package ui;

import java.util.Arrays;
import java.util.Collection;

import chess.*;
import model.GameData;
import model.PlayerGame;






import static ui.EscapeSequences.*;

public class GameplayUi extends ClientUI {
    private final ServerFacade server;
    private String authToken;
    private GameData gameData;
    public String color;
    public ChessBoard board;
    public WebSocketFacade ws;
    private final NotificationHandler notificationHandler;
    private final String serverUrl = "http://localhost:8080";
    private final PlayerGame playergame;


    public GameplayUi(String authToken, GameData gameData, String color, NotificationHandler notificationHandler, WebSocketFacade ws) {
        this.notificationHandler = notificationHandler;
        this.server = new ServerFacade("http://localhost:8080");
        this.authToken = authToken;
        this.color = color;
        this.gameData = gameData;
        this.ws = ws;
        ChessGame game = gameData.game();
        board = game.getBoard();
        playergame = new PlayerGame(color, gameData.gameID());
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redrawboard" -> redrawBoard(params);
                case "leave" -> leave();
                case "makemove" -> makeMove(params);
                case "resign" -> resign(params);
                case "highlightmoves" -> highlightMoves(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String highlightMoves(String... params) throws ResponseException {
        if (params.length == 1) {
            var position = params[0];
            Collection<ChessMove> validMove;
        }
        return null;
    }

    public String redrawBoard(String... params) throws ResponseException {
        try {
            if (ws != null) {
                ws.redrawBoard(playergame, authToken);
            } else {
                System.out.println("No WebSocket connection found.");
            }
        } catch (ResponseException e) {
            return "Redraw board not successful";
        }
        return "Redrew board";
    }

    public String resign(String... params) {
        try {
            if (ws != null) {
                ws.resign(playergame, authToken);
                System.out.println("WebSocket connection closed.");
            } else {
                System.out.println("No WebSocket connection found.");
            }
            return "You have sucessfully resigned";
        } catch (Exception e) {
            System.err.println("An error occurred while trying to resign the game: " + e.getMessage());
            e.printStackTrace();
        }
        return "You have not resigned";
    }


    public String leave(String... params) throws ResponseException {
        try {
            if (ws != null) {
                ws.leave(playergame, authToken);
                System.out.println("WebSocket connection closed.");
            } else {
                System.out.println("No WebSocket connection found.");
            }
            server.leave(playergame, authToken);
            System.out.println("You have successfully left the game.");
            return "You have successfully left the game.\n";
        } catch (Exception e) {
            System.err.println("An error occurred while trying to leave the game: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseException(500, "An error occurred while trying to leave the game.\n");
        }
    }



    public String makeMove(String... params) throws ResponseException {
        try {
            if (params.length == 2 || params.length == 3) {
                var position = params[0];
                var destination = params[1];
                ChessPiece.PieceType promotionPiece = null;

                if (params.length == 3) {
                    promotionPiece = ChessPiece.PieceType.valueOf(params[2].toUpperCase());
                }
                try {
                    if (ws != null) {
                        ws.makeMove(playergame, authToken);
                    } else {
                        System.out.println("No WebSocket connection found.");
                    }
                } catch (ResponseException e) {
                    return "Move not successful";
                }
                ChessMove move = convertToChessMove(position, destination, promotionPiece);
                server.makeMove(move, gameData.gameID(), authToken);
                return "Move successful!";
            } else {
                throw new IllegalArgumentException("Invalid number of parameters. Expected start and end positions, with an optional promotion piece.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred while trying to leave the game: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseException(500, "An error occurred while trying to leave the game.\n");
        }
    }

    public ChessMove convertToChessMove(String start, String end, ChessPiece.PieceType promotionPiece) {
        ChessPosition startPosition = parsePosition(start);
        ChessPosition endPosition = parsePosition(end);

        return new ChessMove(startPosition, endPosition, null);
    }


    public ChessPosition parsePosition(String position) {
            char col = position.charAt(0);
            int row = Character.getNumericValue(position.charAt(1));
            int colIndex = col - 'a' + 1;
            int rowIndex = row;
            return new ChessPosition(rowIndex, colIndex);
        }

    public String help() {

        System.out.println( """
                - redrawboard
                - leave
                - makeMove <initial> <destination>
                - resign
                - highlightMoves <position>
                """);
        return """
                - redrawboard
                - leave
                - makeMove <initial> <destination>
                - resign
                - highlightMoves <position>
                """;
    }
    }