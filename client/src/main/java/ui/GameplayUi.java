package ui;

import java.util.Arrays;

import chess.*;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedAccessException;
import model.GameData;
import model.PlayerGame;
import service.ChessService;
import dataaccess.MySqlDataAccess;






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
    private ChessService service;


    public GameplayUi(String authToken, GameData gameData, String color, NotificationHandler notificationHandler, WebSocketFacade ws) {
        this.notificationHandler = notificationHandler;
        this.server = new ServerFacade("http://localhost:8080");
        this.authToken = authToken;
        this.color = color;
        this.gameData = gameData;
        this.ws = ws;
        this.service = new ChessService(new MySqlDataAccess());
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
//                case "resign" -> resign(params);
//                case "highlightmoves" -> highlightMoves(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        } catch (UnauthorizedAccessException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

//    public String highlightMoves(String... params) throws ResponseException {
//        if (params.length == 1) {
//            var position = params[0];
//        }
//
//    }

    public String redrawBoard(String... params) throws ResponseException {
        if (color.equalsIgnoreCase("WHITE")) {
            printBoard(board, true);
        }
        else {
            printBoard(board, false);
        }
        return String.format("Board redrawn");
    }


    public String leave(String... params) throws ResponseException {
        try {
            if (ws != null) {
                ws.leave(playergame, authToken);
                System.out.println("WebSocket connection closed.");
            } else {
                System.out.println("No WebSocket connection found.");
            }
            service.leave(playergame, authToken);
            System.out.println("You have successfully left the game.");
            return "You have successfully left the game.\n";
        } catch (Exception e) {
            System.err.println("An error occurred while trying to leave the game: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseException(500, "An error occurred while trying to leave the game.\n");
        }
    }



    public String makeMove(String... params) throws ResponseException, UnauthorizedAccessException, DataAccessException {
        try {
            if (params.length == 2 || params.length == 3) {
                var position = params[0];
                var destination = params[1];
                ChessPiece.PieceType promotionPiece = null;

                if (params.length == 3) {
                    promotionPiece = ChessPiece.PieceType.valueOf(params[2].toUpperCase());
                }
                ChessMove move = convertToChessMove(position, destination, promotionPiece);
                service.makeMove(move, gameData.gameID(), authToken);
                if (ws != null) {
                    ws.makeMove(playergame, authToken);
                } else {
                    System.out.println("No WebSocket connection found.");
                }
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


        public static void printBoard(ChessBoard board, boolean whiteAtBottom) {
        String[][] chessBoard = new String[8][8];
        String whitePieceColor = SET_TEXT_COLOR_WHITE;
        String blackPieceColor = SET_TEXT_COLOR_BLACK;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int actualRow = whiteAtBottom ? 7 - row : row;
                int actualCol = whiteAtBottom ? col : 7 - col;
                ChessPiece piece = board.getPiece(new ChessPosition(actualRow + 1, actualCol + 1));
                if (piece == null) {
                    chessBoard[row][col] = EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.EMPTY + EscapeSequences.RESET_TEXT_COLOR;
                } else {
                    String pieceSymbol = getPieceSymbol(piece);
                    String color = piece.getTeamColor().toString().equalsIgnoreCase("WHITE") ? whitePieceColor : blackPieceColor;
                    chessBoard[row][col] = color + pieceSymbol + EscapeSequences.RESET_TEXT_COLOR;
                }
            }
        }
        printColumnHeaders(whiteAtBottom);
        for (int row = 0; row < 8; row++) {
            int displayRow = whiteAtBottom ? 8 - row : row + 1;
            System.out.print(SET_TEXT_COLOR_GREEN + displayRow + " ");

            for (int col = 0; col < 8; col++) {
                boolean isLightSquare = (row + col) % 2 == 0;

                String bgColor = isLightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREEN;
                System.out.print(bgColor + chessBoard[row][col] + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.print(SET_TEXT_COLOR_GREEN + " " + displayRow);
            System.out.println();
        }

        printColumnHeaders(whiteAtBottom);
    }

    private static String getPieceSymbol(ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING:
                return piece.getTeamColor().equals("WHITE") ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN:
                return piece.getTeamColor().equals("WHITE") ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP:
                return piece.getTeamColor().equals("WHITE") ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT:
                return piece.getTeamColor().equals("WHITE") ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK:
                return piece.getTeamColor().equals("WHITE") ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN:
                return piece.getTeamColor().equals("WHITE") ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            default:
                return EscapeSequences.EMPTY;
        }
    }

    private static void printColumnHeaders(boolean whiteAtBottom) {
        System.out.print("   ");
        if (whiteAtBottom) {
            for (char letter = 'a'; letter <= 'h'; letter++) {
                System.out.print(SET_TEXT_COLOR_GREEN + " " + letter + " ");
            }
        } else {
            for (char letter = 'h'; letter >= 'a'; letter--) {
                System.out.print(SET_TEXT_COLOR_GREEN + " " + letter + " ");
            }
        }
        System.out.println();
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