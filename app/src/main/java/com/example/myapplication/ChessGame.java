package com.example.myapplication;

import java.util.HashSet;
import java.util.Set;

public class ChessGame {
    private static Set<ChessPiece> piecesBox = new HashSet<>();

    static {
        reset();
    }

    public static void clear() {
        piecesBox.clear();
    }

    public static void addPiece(ChessPiece piece) {
        piecesBox.add(piece);
    }

    private static boolean canKnightMove(Square from, Square to) {
        return Math.abs(from.getCol() - to.getCol()) == 2 && Math.abs(from.getRow() - to.getRow()) == 1 ||
                Math.abs(from.getCol() - to.getCol()) == 1 && Math.abs(from.getRow() - to.getRow()) == 2;
    }

    private static boolean canRookMove(Square from, Square to) {
        if (from.getCol() == to.getCol() && isClearVerticallyBetween(from, to) ||
                from.getRow() == to.getRow() && isClearHorizontallyBetween(from, to)) {
            return true;
        }
        return false;
    }

    private static boolean isClearVerticallyBetween(Square from, Square to) {
        if (from.getCol() != to.getCol()) return false;
        int gap = Math.abs(from.getRow() - to.getRow()) - 1;
        if (gap == 0) return true;
        for (int i = 1; i <= gap; i++) {
            int nextRow = (to.getRow() > from.getRow()) ? from.getRow() + i : from.getRow() - i;
            if (pieceAt(new Square(from.getCol(), nextRow)) != null) {
                return false;
            }
        }
        return true;
    }

    private static boolean isClearHorizontallyBetween(Square from, Square to) {
        if (from.getRow() != to.getRow()) return false;
        int gap = Math.abs(from.getCol() - to.getCol()) - 1;
        if (gap == 0) return true;
        for (int i = 1; i <= gap; i++) {
            int nextCol = (to.getCol() > from.getCol()) ? from.getCol() + i : from.getCol() - i;
            if (pieceAt(new Square(nextCol, from.getRow())) != null) {
                return false;
            }
        }
        return true;
    }

    private static boolean isClearDiagonally(Square from, Square to) {
        if (Math.abs(from.getCol() - to.getCol()) != Math.abs(from.getRow() - to.getRow())) return false;
        int gap = Math.abs(from.getCol() - to.getCol()) - 1;
        for (int i = 1; i <= gap; i++) {
            int nextCol = (to.getCol() > from.getCol()) ? from.getCol() + i : from.getCol() - i;
            int nextRow = (to.getRow() > from.getRow()) ? from.getRow() + i : from.getRow() - i;
            if (pieceAt(new Square(nextCol, nextRow)) != null) {
                return false;
            }
        }
        return true;
    }

    private static boolean canBishopMove(Square from, Square to) {
        if (Math.abs(from.getCol() - to.getCol()) == Math.abs(from.getRow() - to.getRow())) {
            return isClearDiagonally(from, to);
        }
        return false;
    }

    private static boolean canQueenMove(Square from, Square to) {
        return canRookMove(from, to) || canBishopMove(from, to);
    }

    private static boolean canKingMove(Square from, Square to) {
        if (canQueenMove(from, to)) {
            int deltaCol = Math.abs(from.getCol() - to.getCol());
            int deltaRow = Math.abs(from.getRow() - to.getRow());
            return deltaCol == 1 && deltaRow == 1 || deltaCol + deltaRow == 1;
        }
        return false;
    }

    private static boolean canPawnMove(Square from, Square to) {
        if (from.getCol() == to.getCol()) {
            if (from.getRow() == 1) {
                return to.getRow() == 2 || to.getRow() == 3;
            } else if (from.getRow() == 6) {
                return to.getRow() == 5 || to.getRow() == 4;
            }
        }
        return false;
    }

    public static boolean canMove(Square from, Square to) {
        if (from.getCol() == to.getCol() && from.getRow() == to.getRow()) {
            return false;
        }
        ChessPiece movingPiece = pieceAt(from);
        if (movingPiece == null) {
            return false;
        }
        switch (movingPiece.getChessman()) {
            case KNIGHT:
                return canKnightMove(from, to);
            case ROOK:
                return canRookMove(from, to);
            case BISHOP:
                return canBishopMove(from, to);
            case QUEEN:
                return canQueenMove(from, to);
            case KING:
                return canKingMove(from, to);
            case PAWN:
                return canPawnMove(from, to);
            default:
                return false;
        }
    }

    public static void movePiece(Square from, Square to) {
        if (canMove(from, to)) {
            movePiece(from.getCol(), from.getRow(), to.getCol(), to.getRow());
        }
    }

    private static void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        if (fromCol == toCol && fromRow == toRow) return;
        ChessPiece movingPiece = pieceAt(fromCol, fromRow);
        if (movingPiece == null) return;

        ChessPiece pieceAtTo = pieceAt(toCol, toRow);
        if (pieceAtTo != null) {
            if (pieceAtTo.getPlayer() == movingPiece.getPlayer()) {
                return;
            }
            piecesBox.remove(pieceAtTo);
        }

        piecesBox.remove(movingPiece);
        addPiece(new ChessPiece(toCol, toRow, movingPiece.getPlayer(), movingPiece.getChessman(), movingPiece.getResID()));
    }

    public static void reset() {
        clear();
        for (int i = 0; i < 2; i++) {
            addPiece(new ChessPiece(0 + i * 7, 0, Player.WHITE, ChessMan.ROOK, R.drawable.rook_white));
            addPiece(new ChessPiece(0 + i * 7, 7, Player.BLACK, ChessMan.ROOK, R.drawable.rook_black));

            addPiece(new ChessPiece(1 + i * 5, 0, Player.WHITE, ChessMan.KNIGHT, R.drawable.knight_white));
            addPiece(new ChessPiece(1 + i * 5, 7, Player.BLACK, ChessMan.KNIGHT, R.drawable.knight_black));

            addPiece(new ChessPiece(2 + i * 3, 0, Player.WHITE, ChessMan.BISHOP, R.drawable.bishop_white));
            addPiece(new ChessPiece(2 + i * 3, 7, Player.BLACK, ChessMan.BISHOP, R.drawable.bishop_black));
        }

        for (int i = 0; i < 8; i++) {
            addPiece(new ChessPiece(i, 1, Player.WHITE, ChessMan.PAWN, R.drawable.pawn_white));
            addPiece(new ChessPiece(i, 6, Player.BLACK, ChessMan.PAWN, R.drawable.pawn_black));
        }

        addPiece(new ChessPiece(3, 0, Player.WHITE, ChessMan.QUEEN, R.drawable.queen_white));
        addPiece(new ChessPiece(3, 7, Player.BLACK, ChessMan.QUEEN, R.drawable.queen_black));
        addPiece(new ChessPiece(4, 0, Player.WHITE, ChessMan.KING, R.drawable.king_white));
        addPiece(new ChessPiece(4, 7, Player.BLACK, ChessMan.KING, R.drawable.king_black));
    }

    public static ChessPiece pieceAt(Square square) {
        return pieceAt(square.getCol(), square.getRow());
    }

    private static ChessPiece pieceAt(int col, int row) {
        for (ChessPiece piece : piecesBox) {
            if (col == piece.getCol() && row == piece.getRow()) {
                return piece;
            }
        }
        return null;
    }

    public static String pgnBoard() {
        StringBuilder desc = new StringBuilder(" \n");
        desc.append("  a b c d e f g h\n");
        for (int row = 7; row >= 0; row--) {
            desc.append(row + 1);
            desc.append(boardRow(row));
            desc.append(" ").append(row + 1);
            desc.append("\n");
        }
        desc.append("  a b c d e f g h");

        return desc.toString();
    }

    @Override
    public String toString() {
        StringBuilder desc = new StringBuilder(" \n");
        for (int row = 7; row >= 0; row--) {
            desc.append(row);
            desc.append(boardRow(row));
            desc.append("\n");
        }
        desc.append("  0 1 2 3 4 5 6 7");

        return desc.toString();
    }

    private static String boardRow(int row) {
        StringBuilder desc = new StringBuilder();
        for (int col = 0; col < 8; col++) {
            desc.append(" ");
            ChessPiece piece = pieceAt(col, row);
            if (piece != null) {
                boolean isWhite = piece.getPlayer() == Player.WHITE;
                switch (piece.getChessman()) {
                    case KING:
                        desc.append(isWhite ? "k" : "K");
                        break;
                    case QUEEN:
                        desc.append(isWhite ? "q" : "Q");
                        break;
                    case BISHOP:
                        desc.append(isWhite ? "b" : "B");
                        break;
                    case ROOK:
                        desc.append(isWhite ? "r" : "R");
                        break;
                    case KNIGHT:
                        desc.append(isWhite ? "n" : "N");
                        break;
                    case PAWN:
                        desc.append(isWhite ? "p" : "P");
                        break;
                }
            } else {
                desc.append(".");
            }
        }
        return desc.toString();
    }
}
