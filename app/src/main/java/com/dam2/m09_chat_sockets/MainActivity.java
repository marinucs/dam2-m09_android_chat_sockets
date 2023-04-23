package com.dam2.m09_chat_sockets;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private TextInputEditText connectText;
    private Button connectButton;
    private TextInputEditText inputText;
    private Button sendButton;
    private TextView chatField;
    private static final int SERVERPORT = 4000;
    private ObjectOutputStream output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        connectText = findViewById(R.id.connectText);
        connectButton = findViewById(R.id.connectButton);
        inputText = findViewById(R.id.inputText);
        sendButton = findViewById(R.id.sendButton);
        chatField = findViewById(R.id.chatField);

        connectButton.setOnClickListener(view -> {
            if (Objects.requireNonNull(connectText.getText()).toString().length() > 0) {
                try {
                    chatField.append("Intentando realizar conexion...");
                    System.out.println(connectText.getText().toString());
                    Socket client = new Socket(connectText.getText().toString(), SERVERPORT);
                    chatField.append("\nConectado a: " + client.getInetAddress() + "\n\n");
                    this.output = new ObjectOutputStream(client.getOutputStream());
                    ObjectInputStream input = new ObjectInputStream(client.getInputStream());

                    new ThreadClient(input, chatField).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(context, "Address cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(view -> {
            if (Objects.requireNonNull(inputText.getText()).toString().length() > 0) {
                try {
                    String message = inputText.getText().toString();
                    output.writeObject(message + "\n");
                    output.flush();
                    inputText.setText("");
                    chatField.append("CLIENTE >>> " + message + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(context, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}