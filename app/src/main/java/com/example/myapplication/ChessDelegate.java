package com.example.myapplication;



public interface ChessDelegate {
    ChessPiece pieceAt(Square square);
    void movePiece(Square from, Square to);
}

