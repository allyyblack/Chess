package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        switch(type)
        {
            case KING :
//King
//Move up
                if (myPosition.getRow() + 1 <= 8) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor){
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move down
                if (myPosition.getRow() - 1 >= 1) {
                ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move right
                if (myPosition.getColumn() + 1 <= 8) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move left
                if (myPosition.getColumn() - 1 >= 1) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move diagonally up right
                if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() + 1 <= 8) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move diagonally down right

                if (myPosition.getRow() - 1 >= 1 && myPosition.getColumn() + 1 <= 8) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move diagonally down left
                if (myPosition.getRow() - 1 >= 1 && myPosition.getColumn() - 1 >= 1) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move diagonally up left
                if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() - 1 >= 1) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
            break;

            case QUEEN :
//Queen
//Move upper right diagonal
                for (int i = 1; i + myPosition.getRow() <= 8; i++) {
                    if (i + myPosition.getColumn() > 8) {
                        break;
                    }
                    ChessPosition nextMove = new ChessPosition(i + myPosition.getRow(), i + myPosition.getColumn());
                    if(board.getPiece(nextMove) != null) {

                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());
                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());
                    }
                }
//move lower right diagonal
                for (int i = 1; myPosition.getRow() - i >= 1; i++) {
                    if (i + myPosition.getColumn() > 8) {
                        break;
                    }
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow() - i, i + myPosition.getColumn());
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());
                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());


                    }
                }
//move lower left diagonal
                for (int i = 1; myPosition.getRow() - i >= 1; i++) {
                    if (myPosition.getColumn() - i < 1) {
                        break;
                    }
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());


                    }
                }
//move upper left diagonal
                for (int i = 1; myPosition.getRow() + i <= 8; i++) {
                    if (myPosition.getColumn() - i < 1) {
                        break;
                    }
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                    }
                }
//Move up
                for (int i = 1; myPosition.getRow() + i <= 8; i++) {
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn());
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                    }
                }

//Move down
                for (int i = 1; myPosition.getRow() - i >= 1; i++) {
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn());
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                    }
                }

//Move right
                for (int i = 1; myPosition.getColumn() + i <= 8; i++) {
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i);
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                    }
                }

//Move left
                for (int i = 1; myPosition.getColumn() - i >= 1; i++) {
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                    }
                }
//Bishop
                    case BISHOP :
//move upper right diagonal
                for (int i = 1; i + myPosition.getRow() <= 8; i++) {
                    if (i + myPosition.getColumn() > 8) {
                        break;
                    }
                    ChessPosition nextMove = new ChessPosition(i + myPosition.getRow(), i + myPosition.getColumn());
                    if(board.getPiece(nextMove) != null) {

                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());
                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());
                    }
                }
//move lower right diagonal
                for (int i = 1; myPosition.getRow() - i >= 1; i++) {
                    if (i + myPosition.getColumn() > 8) {
                        break;
                    }
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow() - i, i + myPosition.getColumn());
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());
                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());


                    }
                }
//move lower left diagonal
                for (int i = 1; myPosition.getRow() - i >= 1; i++) {
                    if (myPosition.getColumn() - i < 1) {
                        break;
                    }
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());


                    }
                }
//move upper left diagonal
                for (int i = 1; myPosition.getRow() + i <= 8; i++) {
                    if (myPosition.getColumn() - i < 1) {
                        break;
                    }
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                    }
                }
            case KNIGHT :
//Move up and left
                if (myPosition.getRow() + 2 <= 8 && myPosition.getColumn() - 1 >= 1) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor){
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move up and right
                if (myPosition.getRow() + 2 <= 8 && myPosition.getColumn() + 1 <= 8) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor){
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move down and left
                if (myPosition.getRow() - 2 >= 1 && myPosition.getColumn() - 1 >= 1) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor){
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move down and right
                if (myPosition.getRow() - 2 >= 1 && myPosition.getColumn() + 1 <= 8) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor){
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move right and up
                if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() + 2 <= 8) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor){
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move right and down
                if (myPosition.getRow() - 1 >= 1 && myPosition.getColumn() + 2 <= 8) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor){
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move left and down
                if (myPosition.getRow() - 1 >= 1 && myPosition.getColumn() - 2 >= 1) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor){
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }
//Move left and up
                if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() - 2 >= 1) {
                    ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2);
                    if (board.getPiece(kingMoveUp) != null) {
                        if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor){
                            validMoves.add(new ChessMove(myPosition, kingMoveUp));
                            System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, kingMoveUp));
                        System.out.println(kingMoveUp.getRow() + "" + kingMoveUp.getColumn());
                    }
                }

                break;
            case ROOK :
//Rook

//Move up
                for (int i = 1; myPosition.getRow() + i <= 8; i++) {
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn());
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                    }
                }

//Move down
                for (int i = 1; myPosition.getRow() - i >= 1; i++) {
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn());
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                    }
                }

//Move right
                for (int i = 1; myPosition.getColumn() + i <= 8; i++) {
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i);
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                    }
                }

//Move left
                for (int i = 1; myPosition.getColumn() - i >= 1; i++) {
                    ChessPosition nextMove = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {
                            validMoves.add(new ChessMove(myPosition, nextMove));
                            System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                            break;
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                        System.out.println(nextMove.getRow() + "" + nextMove.getColumn());

                    }
                }
            case PAWN :
                // Statements
                break;
            default :
                // default Statement
        }
        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
