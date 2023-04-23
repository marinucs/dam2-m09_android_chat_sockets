package com.dam2.m09_chat_sockets;

import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ThreadClient extends Thread {
    private ObjectInputStream input;
    private TextView chatField;

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
                chatField.append(message);
                if (message.equals("fin\n")) end = true;
            } while (!end);
        } catch (Exception e) {
            System.out.println("Se ha recibido un error: " + e.getMessage());
        }
    }
}

