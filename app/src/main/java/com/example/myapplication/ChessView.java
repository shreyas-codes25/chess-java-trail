package com.example.myapplication;

import static java.util.Set.of;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChessView extends View {
    private final float scaleFactor = 1.0f;
    private float originX = 20f;
    private float originY = 200f;
    private float cellSide = 130f;
    private final int lightColor = Color.parseColor("#EEEEEE");
    private final int darkColor = Color.parseColor("#BBBBBB");
    private final Set<Integer> imgResIDs;

    {
        imgResIDs = new HashSet<>();
        imgResIDs.add(R.drawable.bishop_black);
        imgResIDs.add(R.drawable.bishop_white);
        imgResIDs.add(R.drawable.king_black);
        imgResIDs.add(R.drawable.king_white);
        imgResIDs.add(R.drawable.queen_black);
        imgResIDs.add(R.drawable.queen_white);
        imgResIDs.add(R.drawable.rook_black);
        imgResIDs.add(R.drawable.rook_white);
        imgResIDs.add(R.drawable.knight_black);
        imgResIDs.add(R.drawable.knight_white);
        imgResIDs.add(R.drawable.pawn_black);
        imgResIDs.add(R.drawable.pawn_white);
    }

    private final Map<Integer, Bitmap> bitmaps = new HashMap<>();
    private final Paint paint = new Paint();

    private Bitmap movingPieceBitmap = null;
    private ChessPiece movingPiece = null;
    private int fromCol = -1;
    private int fromRow = -1;
    private float movingPieceX = -1f;
    private float movingPieceY = -1f;

    private ChessDelegate chessDelegate;

    public ChessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadBitmaps();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int smaller = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(smaller, smaller);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float chessBoardSide = Math.min(getWidth(), getHeight()) * scaleFactor;
        cellSide = chessBoardSide / 8f;
        originX = (getWidth() - chessBoardSide) / 2f;
        originY = (getHeight() - chessBoardSide) / 2f;

        drawChessboard(canvas);
        drawPieces(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromCol = (int) ((event.getX() - originX) / cellSide);
                fromRow = 7 - (int) ((event.getY() - originY) / cellSide);

                ChessPiece piece = chessDelegate.pieceAt(new Square(fromCol, fromRow));
                if (piece != null) {
                    movingPiece = piece;
                    movingPieceBitmap = bitmaps.get(piece.getResID());
                }
                break;

            case MotionEvent.ACTION_MOVE:
                movingPieceX = event.getX();
                movingPieceY = event.getY();
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                int toCol = (int) ((event.getX() - originX) / cellSide);
                int toRow = 7 - (int) ((event.getY() - originY) / cellSide);

                if (fromCol != toCol || fromRow != toRow) {
                    chessDelegate.movePiece(new Square(fromCol, fromRow), new Square(toCol, toRow));
                }

                movingPiece = null;
                movingPieceBitmap = null;
                invalidate();
                break;
        }
        return true;
    }

    private void drawPieces(Canvas canvas) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = chessDelegate.pieceAt(new Square(col, row));
                if (piece != null && piece != movingPiece) {
                    drawPieceAt(canvas, col, row, piece.getResID());
                }
            }
        }

        if (movingPieceBitmap != null) {
            canvas.drawBitmap(movingPieceBitmap, null,
                    new RectF(movingPieceX - cellSide / 2, movingPieceY - cellSide / 2,
                            movingPieceX + cellSide / 2, movingPieceY + cellSide / 2), paint);
        }
    }

    private void drawPieceAt(Canvas canvas, int col, int row, int resID) {
        canvas.drawBitmap(bitmaps.get(resID), null,
                new RectF(originX + col * cellSide, originY + (7 - row) * cellSide,
                        originX + (col + 1) * cellSide, originY + ((7 - row) + 1) * cellSide), paint);
    }

    private void loadBitmaps() {
        for (Integer imgResID : imgResIDs) {
            bitmaps.put(imgResID, BitmapFactory.decodeResource(getResources(), imgResID));
        }
    }

    private void drawChessboard(Canvas canvas) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                drawSquareAt(canvas, col, row, (col + row) % 2 == 1);
            }
        }
    }

    private void drawSquareAt(Canvas canvas, int col, int row, boolean isDark) {
        paint.setColor(isDark ? darkColor : lightColor);
        canvas.drawRect(originX + col * cellSide, originY + row * cellSide,
                originX + (col + 1) * cellSide, originY + (row + 1) * cellSide, paint);
    }

    public void setChessDelegate(ChessDelegate chessDelegate) {
        this.chessDelegate = chessDelegate;
    }
}
