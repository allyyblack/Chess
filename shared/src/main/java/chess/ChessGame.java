package chess;

import java.util.HashSet;
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
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
        team = TeamColor.WHITE;
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
        Collection<ChessMove> potentials = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : potentials) {
            ChessBoard hypotheticalBoard = board.clone();
            makeMoveHypothetical(move, hypotheticalBoard);
            if (!isInCheckHypothetical(piece.getTeamColor(),hypotheticalBoard)) {
                validMoves.add(move);
                System.out.println(move.getEndPosition().getRow() + "" + move.getEndPosition().getColumn());
            }
        }
        HashSet<ChessMove> uniqueNumbers = new HashSet<>(validMoves);

        Collection<ChessMove> numbersWithoutDuplicates = new ArrayList<>(uniqueNumbers);
        return numbersWithoutDuplicates;
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
        if (board.getPiece(startPosition) == null) {
            throw new InvalidMoveException("Invalid Move");
        }
        if (board.getPiece(startPosition).getTeamColor() != team) {
            throw new InvalidMoveException("Invalid Move out of turn");
        }
        Collection<ChessMove> validMoves = validMoves(startPosition);
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move");
        }
        ChessPiece piece = new ChessPiece(board.getPiece(startPosition).getTeamColor(), board.getPiece(startPosition).getPieceType());
        if (promotionPiece != null) {
            piece = new ChessPiece(board.getPiece(startPosition).getTeamColor(), promotionPiece);
        }
        if (isInCheck(piece.getTeamColor())) {
            ChessBoard newBoard = board.clone();
            makeMoveHypothetical(move, newBoard);
            if (isInCheckHypothetical(piece.getTeamColor(), newBoard)) {
                throw new InvalidMoveException("Invalid Move");
            }
        }
        board.addPiece(endPosition, piece);
        board.addPiece(startPosition, null);
        setTeamTurn(getOppositeTeamColor(team));
    }
    public void makeMoveHypothetical(ChessMove move, ChessBoard hypotheticalBoard) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        ChessPiece piece = hypotheticalBoard.getPiece(startPosition);
        if (promotionPiece != null) {
            piece = new ChessPiece(hypotheticalBoard.getPiece(startPosition).getTeamColor(), promotionPiece);
        }
        hypotheticalBoard.addPiece(endPosition, piece);
        hypotheticalBoard.addPiece(startPosition, null);
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
                ChessMove checkMove = new ChessMove(opponentPosition, kingPosition);
                if (opponentMoves.contains(checkMove)) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isInCheckHypothetical(TeamColor teamColor, ChessBoard hypotheticalBoard) {
        ChessPosition kingPosition = findKingPositionHypothetical(teamColor, hypotheticalBoard);
        for (ChessPosition opponentPosition : getTeamPositionsHypothetical(getOppositeTeamColor(teamColor), hypotheticalBoard)) {
            ChessPiece opponentPiece = hypotheticalBoard.getPiece(opponentPosition);
            if (opponentPiece != null) {
                Collection<ChessMove> opponentMoves = opponentPiece.pieceMoves(hypotheticalBoard, opponentPosition);
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
    public ChessPosition findKingPositionHypothetical(TeamColor teamColor, ChessBoard hypotheticalBoard) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = hypotheticalBoard.getPiece(position);
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
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    positions.add(position);
                }
            }
        }
        return positions;
    }
    public Collection<ChessPosition> getTeamPositionsHypothetical(TeamColor teamColor, ChessBoard hypotheticalBoard) {
        ArrayList<ChessPosition> positions = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = hypotheticalBoard.getPiece(position);
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
                    ChessBoard newBoard = board.clone();
                    System.out.println("printing original board");
                    newBoard.printBoard();
                    makeMoveHypothetical(newMove, newBoard);
                    System.out.println("printing board with move");

                    newBoard.printBoard();
                    if (!isInCheckHypothetical(teamColor, newBoard)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (ChessPosition position : getTeamPositions(teamColor)) {
            ChessPiece piece = board.getPiece(position);
            if (piece != null) {
                Collection<ChessMove> validMoves = piece.pieceMoves(board, position);
                for (ChessMove newMove : validMoves) {
                    ChessBoard newBoard = board.clone();
                    makeMoveHypothetical(newMove, newBoard);
                    if (!isInCheckHypothetical(teamColor, newBoard)) {
                        return false;
                    }
                }
            }
        }
        return true;
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
