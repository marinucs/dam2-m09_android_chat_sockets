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
    private boolean serverOnline = false;

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
            String ip = Objects.requireNonNull(connectText.getText()).toString();
            if (Objects.requireNonNull(connectText.getText()).toString().length() > 0 &&
                    ip.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                AsyncTaskConnect asyncTaskConnect = new AsyncTaskConnect();
                asyncTaskConnect.execute(connectText.getText().toString());
            } else {
                Toast.makeText(context, "Incorrect IP address provided", Toast.LENGTH_SHORT).show();
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
                runOnUiThread(() -> chatField.append("Intentando realizar conexión...\n"));
                Socket client = new Socket(values[0], SERVERPORT);
                runOnUiThread(() -> chatField.append("\nConectado a: " + client.getInetAddress() + "\n\n"));
                output = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(client.getInputStream());
                serverOnline = true;
                new ThreadClient(input, chatField).start();
            } catch (IOException e) {
                // throw new RuntimeException(e);
                runOnUiThread(() -> Toast.makeText(context, "Servidor no disponible", Toast.LENGTH_LONG).show());
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
                if (serverOnline) {
                    output.writeObject(message + "\n");
                    output.flush();
                    runOnUiThread(() -> {
                        inputText.setText("");
                        chatField.append(message + "\n");
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(context, "Servidor no disponible", Toast.LENGTH_LONG).show());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    class ThreadClient extends Thread {
        private final ObjectInputStream input;
        private final TextView chatField;

        public ThreadClient(ObjectInputStream input, TextView chatField) throws IOException {
            this.chatField = chatField;
            this.input = input;
        }

        @Override
        public void run() {
            try {
                boolean end = false;
                do {
                    String message = (String) input.readObject();
                    runOnUiThread(() -> chatField.append("SERVIDOR >>> " + message));
                    if (message.equals("fin\n")) end = true;
                } while (!end);
            } catch (Exception e) {
                System.out.println("Se ha recibido un error: " + e.getMessage());
            }
        }
    }

}
