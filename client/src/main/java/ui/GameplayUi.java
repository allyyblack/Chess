package ui;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class GameplayUi extends ClientUI {

        public void main(String color) {
            if(color.equals("WHITE")) {
                drawBoard(true);
            }
            else {
                drawBoard(false);
            }
        }

    public static void drawBoard(boolean whiteAtBottom) {
        String[][] board = new String[8][8];
        initializeBoard(board, whiteAtBottom);
        System.out.print("\n " + SET_TEXT_COLOR_BLUE);
        System.out.print("   ");
        if (whiteAtBottom) {
            for (char letter = 'a'; letter <= 'h'; letter++) {
                System.out.print(" " + letter + " ");
            }
        } else {
            for (char letter = 'h'; letter >= 'a'; letter--) {
                System.out.print(" " + letter + " ");
            }
        }
        System.out.println();
        for (int row = 0; row < 8; row++) {
            int displayRow = whiteAtBottom ? 8 - row : row + 1;

            System.out.print(displayRow + " ");

            for (int col = 0; col < 8; col++) {
                int boardRow = whiteAtBottom ? row : 7 - row;
                int boardCol = whiteAtBottom ? col : 7 - col;

                if ((boardRow + boardCol) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE + board[boardRow][boardCol] + EscapeSequences.RESET_BG_COLOR);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + board[boardRow][boardCol] + EscapeSequences.RESET_BG_COLOR);
                }
            }

            System.out.print(" " + displayRow);
            System.out.println();
        }

        System.out.print("   ");
        if (whiteAtBottom) {
            for (char letter = 'a'; letter <= 'h'; letter++) {
                System.out.print(" " + letter + " ");
            }
        } else {
            for (char letter = 'h'; letter >= 'a'; letter--) {
                System.out.print(" " + letter + " ");
            }
        }
        System.out.println();
    }





    public static void initializeBoard(String[][] board, boolean whiteAtBottom) {
            String[] whiteBottom = {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP,
                    EscapeSequences.WHITE_KING, EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_BISHOP,
                    EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK};
            String[] whiteTop = {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP,
                    EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP,
                    EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK};
            String[] blackBottom = {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP,
                    EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP,
                    EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK};
            String[] blackTop = {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP,
                    EscapeSequences.BLACK_KING, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_BISHOP,
                    EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK};
            String[] whitePawns = {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN,
                    EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN,
                    EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN};
            String[] blackPawns = {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN,
                    EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN,
                    EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN};

                board[0] = blackTop;
                board[1] = blackPawns;
                board[6] = whitePawns;
                board[7] = whiteBottom;

            for (int row = 2; row < 6; row++) {
                for (int col = 0; col < 8; col++) {
                    board[row][col] = EscapeSequences.EMPTY;
                }
            }
        }
    }