package com.dam2.m09_chat_sockets;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ObjectOutputStream output;
    private Context context;
    private TextInputEditText connectText;
    private TextInputEditText inputText;
    private TextView chatField;
    private static final int SERVERPORT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        connectText = findViewById(R.id.connectText);
        chatField = findViewById(R.id.chatField);
        inputText = findViewById(R.id.inputText);
        inputText.setScroller(new Scroller(context));
        inputText.setVerticalScrollBarEnabled(true);
        inputText.setMovementMethod(new ScrollingMovementMethod());

        Button connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(view -> {
            if (Objects.requireNonNull(connectText.getText()).toString().length() > 0) {
                AsyncTaskConnect asyncTaskConnect = new AsyncTaskConnect();
                asyncTaskConnect.execute(connectText.getText().toString());
            } else {
                Toast.makeText(context, "Address cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(view -> {
            if (Objects.requireNonNull(inputText.getText()).toString().length() > 0) {
                AsyncTaskSendMessage asyncTaskSendMessage = new AsyncTaskSendMessage();
                asyncTaskSendMessage.execute(inputText.getText().toString());
            } else {
                Toast.makeText(context, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    class AsyncTaskConnect extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... values) {
            try {
                runOnUiThread(() -> chatField.append("Intentando realizar conexion..."));
                Socket client = new Socket(values[0], SERVERPORT);
                runOnUiThread(() -> chatField.append("\nConectado a: " + client.getInetAddress() + "\n\n"));
                output = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(client.getInputStream());
                new ThreadClient(input, chatField).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    class AsyncTaskSendMessage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... values) {
            try {
                String message = values[0];
                output.writeObject(message + "\n");
                output.flush();
                runOnUiThread(() -> {
                    inputText.setText("");
                    chatField.append(message + "\n");
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

}
