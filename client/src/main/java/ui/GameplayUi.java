package ui;

import java.util.Arrays;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;



import static ui.EscapeSequences.*;

public class GameplayUi extends ClientUI {
    private final ServerFacade server;
    private String authToken;
    private ChessGame game;
    public String color;
    public ChessBoard board;
    private WebSocketFacade ws;
    private final NotificationHandler notificationHandler;




    public GameplayUi(String authToken, ChessGame game, String color, NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
        this.server = new ServerFacade("http://localhost:8080");
        this.authToken = authToken;
        this.game = game;
        this.color = color;
        board = game.getBoard();
        if (color.equalsIgnoreCase("BLACK")) {
            printBoard(board, false);
        }
        else {
            printBoard(board, true);
        }
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redrawboard" -> redrawBoard(params);
//                case "leave" -> leave();
//                case "makemove" -> makeMove(params);
//                case "resign" -> resign(params);
//                case "highlightmoves" -> highlightMoves(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String redrawBoard(String... params) throws ResponseException {
        if (color.equalsIgnoreCase("WHITE")) {
            printBoard(board, true);
        }
        else {
            printBoard(board, false);
        }
        return String.format("Board redrawn");
    }

    public void makeMove(String... params) throws ResponseException {
        if (params.length == 2) {
            var initial = params[0];
            var destination = params[1];

        }
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
            int displayRow = whiteAtBottom ? 8 - row : row + 1; // Adjust row labels
            System.out.print(SET_TEXT_COLOR_GREEN + displayRow + " "); // Row labels on the left

            for (int col = 0; col < 8; col++) {
                boolean isLightSquare = (row + col) % 2 == 0;

                String bgColor = isLightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREEN;
                System.out.print(bgColor + chessBoard[row][col] + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.print(SET_TEXT_COLOR_GREEN + " " + displayRow); // Row labels on the right
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
                - highlightMoves
                """);
        return """
                - redrawboard
                - leave
                - makeMove
                - resign
                - highlightMoves
                """;
    }
    }