package com.dam2.m09_chat_sockets;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private EditText connectText;
    private Button connectButton;
    private TextInputEditText inputText;
    private Button sendButton;
    private TextView chatField;
    private static final int SERVERPORT = 4000;

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
            if (connectText.getText().toString().length() > 0) {
                AsyncTaskConnectSocket AsyncTaskConnectSocket = new AsyncTaskConnectSocket();
                AsyncTaskConnectSocket.execute(connectText.getText().toString());
            } else {
                Toast.makeText(context, "Address cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(view -> {
            if (Objects.requireNonNull(inputText.getText()).toString().length() > 0) {
                // AsyncTaskSendMessage asyncTaskSendMessage = new AsyncTaskSendMessage();
                // asyncTaskSendMessage.execute(inputText.getText().toString());
            } else {
                Toast.makeText(context, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    class AsyncTaskConnectSocket extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // chatField.setText(chatField.getText().toString().append("Connecting to server...\n"));
        }

        @Override
        protected String doInBackground(String... values) {
            try {
                //Se conecta al servidor
                InetAddress serverAddr = InetAddress.getByName(connectText.getText().toString());
                Socket socket = new Socket(serverAddr, SERVERPORT);

                //envia peticion de cliente
                /*Log.i("I/TCP Client", "Send data to server");
                PrintStream output = new PrintStream(socket.getOutputStream());
                String request = values[0];
                output.println(request);*/

                //recibe respuesta del servidor y formatea a String
                /*Log.i("I/TCP Client", "Received data to server");
                InputStream stream = socket.getInputStream();
                byte[] lenBytes = new byte[256];
                stream.read(lenBytes,0,256);
                String received = new String(lenBytes,"UTF-8").trim();*/
                //Log.i("I/TCP Client", "Received " + received);
                //Log.i("I/TCP Client", "");
                //cierra conexion
                socket.close();
                // return received;
                return null;
            } catch (UnknownHostException ex) {
                //Log.e("E/TCP Client", "" + ex.getMessage());
                return ex.getMessage();
            } catch (IOException ex) {
                //Log.e("E/TCP Client", "" + ex.getMessage());
                return ex.getMessage();
            }
        }

        /*@Override
        protected void onPostExecute(String[] libros) {
            //
        }*/
    }
}