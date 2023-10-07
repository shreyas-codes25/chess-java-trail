package com.example.myapplication;


public class ChessPiece {
    private final int col;
    private final int row;
    private final Player player;
    private final ChessMan chessman;
    private final int resID;

    public ChessPiece(int col, int row, Player player, ChessMan chessman, int resID) {
        this.col = col;
        this.row = row;
        this.player = player;
        this.chessman = chessman;
        this.resID = resID;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public Player getPlayer() {
        return player;
    }

    public ChessMan getChessman() {
        return chessman;
    }

    public int getResID() {
        return resID;
    }
}

