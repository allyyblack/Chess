package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor team;
    private ChessBoard board;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        ChessPiece piece = board.getPiece(endPosition);
        if (promotionPiece != null) {
            piece = new ChessPiece(board.getPiece(startPosition).getTeamColor(), promotionPiece);
        }
        board.addPiece(endPosition, piece);
        board.addPiece(startPosition, null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        for (ChessPosition opponentPosition : getTeamPositions(getOppositeTeamColor(teamColor))) {
            ChessPiece opponentPiece = board.getPiece(opponentPosition);
            if (opponentPiece != null) {
                Collection<ChessMove> opponentMoves = opponentPiece.pieceMoves(board, opponentPosition);
                if (opponentMoves.contains(new ChessMove(opponentPosition, kingPosition))) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isInCheckHypothetical(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        for (ChessPosition opponentPosition : getTeamPositions(getOppositeTeamColor(teamColor))) {
            ChessPiece opponentPiece = board.getPiece(opponentPosition);
            if (opponentPiece != null) {
                Collection<ChessMove> opponentMoves = opponentPiece.pieceMoves(board, opponentPosition);
                if (opponentMoves.contains(new ChessMove(opponentPosition, kingPosition))) {
                    return true;
                }
            }
        }
        return false;
    }

    public TeamColor getOppositeTeamColor(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            return TeamColor.BLACK;
        }
        else {
            return TeamColor.WHITE;
        }
    }


    public ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }
    public Collection<ChessPosition> getTeamPositions(TeamColor teamColor) {
        ArrayList<ChessPosition> positions = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 0; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        for (ChessPosition position : getTeamPositions(teamColor)) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null) {
                Collection<ChessMove> validMoves = piece.pieceMoves(board, position);
                for (ChessMove newMove : validMoves) {
                    ChessBoard newBoard = board.makeMove(newMove);  // Create a hypothetical board after the move
                    if (!newBoard.isInCheck(teamColor)) {  // If the move gets the team out of check, it's not checkmate
                        return false;
                    }
                }
            }
        }

        return true;  // No valid moves found, so the team is in checkmate
    }
    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
