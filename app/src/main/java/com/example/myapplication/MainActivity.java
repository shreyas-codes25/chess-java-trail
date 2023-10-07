package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements ChessDelegate {
    private final String socketHost = "127.0.0.1";
    private final int socketPort = 50000;
    private final int socketGuestPort = 50001; // used for socket server on emulator
    private ChessView chessView;
    private Button resetButton;
    private Button listenButton;
    private Button connectButton;
    private PrintWriter printWriter;
    private ServerSocket serverSocket;
    private boolean isEmulator = Build.FINGERPRINT.contains("generic");

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chessView = findViewById(R.id.chess_view);
        resetButton = findViewById(R.id.reset_button);
        listenButton = findViewById(R.id.listen_button);
        connectButton = findViewById(R.id.connect_button);
        chessView.setChessDelegate(this);

        resetButton.setOnClickListener(view -> {
            ChessGame.reset();
            chessView.invalidate();
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing server socket", e);
                }
            }
            listenButton.setEnabled(true);
        });

        listenButton.setOnClickListener(view -> {
            listenButton.setEnabled(false);
            int port = isEmulator ? socketGuestPort : socketPort;
            Toast.makeText(this, "listening on " + port, Toast.LENGTH_SHORT).show();
            Executors.newSingleThreadExecutor().execute(() -> {
                try (ServerSocket srvSkt = new ServerSocket(port)) {
                    serverSocket = srvSkt;
                    try {
                        Socket socket = srvSkt.accept();
                        receiveMove(socket);
                    } catch (SocketException e) {
                        // ignored, socket closed
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error creating server socket", e);
                }
            });
        });

        connectButton.setOnClickListener(view -> {
            Log.d(TAG, "socket client connecting ...");
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    Socket socket = new Socket(socketHost, socketPort);
                    receiveMove(socket);
                } catch (ConnectException e) {
                    runOnUiThread(() -> Toast.makeText(this, "connection failed", Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    Log.e(TAG, "Error connecting to socket", e);
                }
            });
        });
    }

    private void receiveMove(Socket socket) {
        try (Scanner scanner = new Scanner(socket.getInputStream())) {
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            while (scanner.hasNextLine()) {
                String[] moveStr = scanner.nextLine().split(",");
                int[] move = new int[moveStr.length];
                for (int i = 0; i < moveStr.length; i++) {
                    move[i] = Integer.parseInt(moveStr[i]);
                }
                runOnUiThread(() -> {
                    ChessGame.movePiece(new Square(move[0], move[1]), new Square(move[2], move[3]));
                    chessView.invalidate();
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error receiving move", e);
        }
    }

    @Override
    public ChessPiece pieceAt(Square square) {
        return ChessGame.pieceAt(square);
    }

    @Override
    public void movePiece(Square from, Square to) {
        ChessGame.movePiece(from, to);
        chessView.invalidate();

        if (printWriter != null) {
            String moveStr = from.getCol() + "," + from.getRow() + "," + to.getCol() + "," + to.getRow();
            Executors.newSingleThreadExecutor().execute(() -> printWriter.println(moveStr));
        }
    }
}