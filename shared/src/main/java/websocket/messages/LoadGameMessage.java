package websocket.messages;
import chess.*;
import com.google.gson.Gson;

import java.util.Collection;


public class LoadGameMessage extends ServerMessage {
    ChessGame game;
    boolean whiteAtBottom;
    public LoadGameMessage(ServerMessageType type, ChessGame game, boolean whiteAtBottom) {
        super(type);
        this.game = game;
        this.whiteAtBottom = whiteAtBottom;
    }

    @Override
    public String getMessage() {
        ChessBoard board = game.getBoard();
        ChessPosition startPosition;

        String[][] chessBoard = new String[8][8];
        String whitePieceColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
        String blackPieceColor = EscapeSequences.SET_TEXT_COLOR_BLACK;

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
            System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + displayRow + " ");

            for (int col = 0; col < 8; col++) {
                boolean isLightSquare = (row + col) % 2 == 0;

                String bgColor = isLightSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                System.out.print(bgColor + chessBoard[row][col] + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + " " + displayRow);
            System.out.println();
        }

        printColumnHeaders(whiteAtBottom);
        return "";
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
                System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + " " + letter + " ");
            }
        } else {
            for (char letter = 'h'; letter >= 'a'; letter--) {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + " " + letter + " ");
            }
        }
        System.out.println();
    }
}
