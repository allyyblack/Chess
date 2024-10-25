package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }
    public ChessPiece clone() {
        return new ChessPiece(this.pieceColor, this.type);
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    public PieceType getPieceType() {
        return this.type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        switch(type)
        {
            case KING:
                addKingMoves(board, myPosition, validMoves);
                break;

            case QUEEN :
                addQueenMoves(board, myPosition, validMoves);
                break;

            case BISHOP :
                addBishopMoves(board, myPosition, validMoves);
                break;

            case KNIGHT :
                addKnightMoves(board, myPosition, validMoves);
                break;

            case ROOK :
                addRookMoves(board, myPosition, validMoves);
                break;

            case PAWN :
                addPawnMoves(board, myPosition, validMoves);
                break;

            default :
        }
        return validMoves;
    }

    private void addKingMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves) {
        if (myPosition.getRow() + 1 <= 8) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() - 1 >= 1) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getColumn() + 1 <= 8) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getColumn() - 1 >= 1) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() + 1 <= 8) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() - 1 >= 1 && myPosition.getColumn() + 1 <= 8) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() - 1 >= 1 && myPosition.getColumn() - 1 >= 1) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() - 1 >= 1) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
    }

    private void addQueenMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves) {
        int[][] positions = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1},
                {1, 1},
                {-1, 1},
                {-1, -1},
                {1, -1}
        };

        for (int[] position : positions) {
            for (int i = 1; ; i++) {
                int newRow = myPosition.getRow() + i * position[0];
                int newColumn = myPosition.getColumn() + i * position[1];

                if (newRow < 1 || newRow > 8 || newColumn < 1 || newColumn > 8) {
                    break;
                }

                ChessPosition nextMove = new ChessPosition(newRow, newColumn);
                ChessPiece otherPiece = board.getPiece(nextMove);

                if (otherPiece != null) {
                    if (otherPiece.pieceColor != board.getPiece(myPosition).pieceColor) {
                        validMoves.add(new ChessMove(myPosition, nextMove));
                    }
                    break;
                } else {
                    validMoves.add(new ChessMove(myPosition, nextMove));
                }
            }
        }
    }

    private void addBishopMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves) {
        for (int i = 1; i + myPosition.getRow() <= 8; i++) {
            if (i + myPosition.getColumn() > 8) {
                break;
            }
            ChessPosition nextMove = new ChessPosition(i + myPosition.getRow(), i + myPosition.getColumn());
            if (board.getPiece(nextMove) != null) {

                if (board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                    break;
                } else {
                    validMoves.add(new ChessMove(myPosition, nextMove));
                    break;
                }
            } else {
                validMoves.add(new ChessMove(myPosition, nextMove));
            }
        }
        for (int i = 1; myPosition.getRow() - i >= 1; i++) {
            if (i + myPosition.getColumn() > 8) {
                break;
            }
            ChessPosition nextMove = new ChessPosition(myPosition.getRow() - i, i + myPosition.getColumn());
            if (board.getPiece(nextMove) != null) {
                if (board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                    break;
                } else {
                    validMoves.add(new ChessMove(myPosition, nextMove));
                    break;
                }
            } else {
                validMoves.add(new ChessMove(myPosition, nextMove));
            }
        }
        for (int i = 1; myPosition.getRow() - i >= 1; i++) {
            if (myPosition.getColumn() - i < 1) {
                break;
            }
            ChessPosition nextMove = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
            if (board.getPiece(nextMove) != null) {
                if (board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                    break;
                } else {
                    validMoves.add(new ChessMove(myPosition, nextMove));

                    break;
                }
            } else {
                validMoves.add(new ChessMove(myPosition, nextMove));
            }
        }
        for (int i = 1; myPosition.getRow() + i <= 8; i++) {
            if (myPosition.getColumn() - i < 1) {
                break;
            }
            ChessPosition nextMove = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i);
            if (board.getPiece(nextMove) != null) {
                if (board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                    break;
                } else {
                    validMoves.add(new ChessMove(myPosition, nextMove));
                    break;
                }
            } else {
                validMoves.add(new ChessMove(myPosition, nextMove));
            }
        }
    }

    private void addKnightMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves) {
        //Move up and left
        if (myPosition.getRow() + 2 <= 8 && myPosition.getColumn() - 1 >= 1) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() + 2 <= 8 && myPosition.getColumn() + 1 <= 8) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() - 2 >= 1 && myPosition.getColumn() - 1 >= 1) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() - 2 >= 1 && myPosition.getColumn() + 1 <= 8) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() + 2 <= 8) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() - 1 >= 1 && myPosition.getColumn() + 2 <= 8) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() - 1 >= 1 && myPosition.getColumn() - 2 >= 1) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
        if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() - 2 >= 1) {
            ChessPosition kingMoveUp = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2);
            if (board.getPiece(kingMoveUp) != null) {
                if (board.getPiece(kingMoveUp).pieceColor != board.getPiece(myPosition).pieceColor) {
                    validMoves.add(new ChessMove(myPosition, kingMoveUp));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, kingMoveUp));
            }
        }
    }

    private void addRookMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves) {
        for (int i = 1; myPosition.getRow() + i <= 8; i++) {
            ChessPosition nextMove = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn());
            if (board.getPiece(nextMove) != null) {
                if (board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                    break;
                } else {
                    validMoves.add(new ChessMove(myPosition, nextMove));

                    break;
                }
            } else {
                validMoves.add(new ChessMove(myPosition, nextMove));

            }
        }
        for (int i = 1; myPosition.getRow() - i >= 1; i++) {
            ChessPosition nextMove = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn());
            if (board.getPiece(nextMove) != null) {
                if (board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                    break;
                } else {
                    validMoves.add(new ChessMove(myPosition, nextMove));

                    break;
                }
            } else {
                validMoves.add(new ChessMove(myPosition, nextMove));

            }
        }
        for (int i = 1; myPosition.getColumn() + i <= 8; i++) {
            ChessPosition nextMove = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i);
            if (board.getPiece(nextMove) != null) {
                if (board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                    break;
                } else {
                    validMoves.add(new ChessMove(myPosition, nextMove));

                    break;
                }
            } else {
                validMoves.add(new ChessMove(myPosition, nextMove));

            }
        }
        for (int i = 1; myPosition.getColumn() - i >= 1; i++) {
            ChessPosition nextMove = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);
            if (board.getPiece(nextMove) != null) {
                if (board.getPiece(nextMove).pieceColor == board.getPiece(myPosition).pieceColor) {
                    break;
                } else {
                    validMoves.add(new ChessMove(myPosition, nextMove));

                    break;
                }
            } else {
                validMoves.add(new ChessMove(myPosition, nextMove));

            }
        }
    }

    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves) {
        int direction;
        int startRow;
        int promotionRow;

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startRow = 2;
            promotionRow = 8;
        } else {
            direction = -1;
            startRow = 7;
            promotionRow = 1;
        }

        // Forward move
        if ((myPosition.getRow() + direction <= 8 && pieceColor == ChessGame.TeamColor.WHITE) ||
                (myPosition.getRow() - direction >= 1 && pieceColor == ChessGame.TeamColor.BLACK)) {

            ChessPosition move = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
            if (board.getPiece(move) == null) {
                if (myPosition.getRow() + direction == promotionRow) {
                    addPromotionMoves(validMoves, myPosition, move);
                } else {
                    validMoves.add(new ChessMove(myPosition, move));
                }
            }
            ChessPosition diagonalLeftMove = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
            if (board.getPiece(diagonalLeftMove) != null && board.getPiece(diagonalLeftMove).pieceColor != this.pieceColor) {
                if (myPosition.getRow() + direction == promotionRow) {
                    if (board.getPiece(diagonalLeftMove).getPieceType() == PieceType.KING) {
                        validMoves.add(new ChessMove(myPosition, diagonalLeftMove, null));
                    } else {
                        addPromotionMoves(validMoves, myPosition, diagonalLeftMove);
                    }
                } else {
                    validMoves.add(new ChessMove(myPosition, diagonalLeftMove));
                }
            }

// Diagonal right move
            ChessPosition diagonalRightMove = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
            if (board.getPiece(diagonalRightMove) != null && board.getPiece(diagonalRightMove).pieceColor != this.pieceColor) {
                if (myPosition.getRow() + direction == promotionRow) {
                    if (board.getPiece(diagonalRightMove).getPieceType() == PieceType.KING) {
                        validMoves.add(new ChessMove(myPosition, diagonalRightMove, null));
                    } else {
                        addPromotionMoves(validMoves, myPosition, diagonalRightMove);
                    }
                } else {
                    validMoves.add(new ChessMove(myPosition, diagonalRightMove));
                }
            }
        }
        if (myPosition.getRow() == startRow) {
            ChessPosition doubleMove = new ChessPosition(myPosition.getRow() + (direction * 2), myPosition.getColumn());
            ChessPosition intermediateMove = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
            if (board.getPiece(intermediateMove) == null && board.getPiece(doubleMove) == null) {
                validMoves.add(new ChessMove(myPosition, doubleMove));
            }
        }
    }


    private void addPromotionMoves(ArrayList<ChessMove> validMoves, ChessPosition from, ChessPosition to) {
        validMoves.add(new ChessMove(from, to, PieceType.QUEEN));
        validMoves.add(new ChessMove(from, to, PieceType.KNIGHT));
        validMoves.add(new ChessMove(from, to, PieceType.ROOK));
        validMoves.add(new ChessMove(from, to, PieceType.BISHOP));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
