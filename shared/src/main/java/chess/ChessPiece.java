package chess;

import java.util.ArrayList;
import java.util.Collection;

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
        switch(type)
        {
            case KING :

            case QUEEN :
                // Statements
                break;
            case BISHOP :
                for (int i = 1; i + myPosition.getRow() <=8; i++) {
                    if (i + myPosition.getColumn() >= 8) {
                        break;
                    }
                    ChessPosition nextMove = new ChessPosition(i + myPosition.getRow(), i + myPosition.getColumn());
                    if(board.getPiece(nextMove) != null) {
                        if(board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                            break;
                        }
                        else {

                            break;
                        }
                    }
                }
            case KNIGHT :
                // Statements
                break;
            case ROOK :
                // Statements
                break;
            case PAWN :
                // Statements
                break;
            default :
                // default Statement
        }
        return new ArrayList<>();
    }
}
