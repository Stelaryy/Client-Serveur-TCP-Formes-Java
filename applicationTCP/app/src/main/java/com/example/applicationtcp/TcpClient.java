package com.example.applicationtcp;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TcpClient {
    public interface Callback {
        void onResult(String message);
        void onError(Exception e);
    }

    public static void send(String host, int port, String line, Callback cb) {
        new Thread(() -> {
            try (Socket s = new Socket(host, port);
                 BufferedWriter w = new BufferedWriter(
                         new OutputStreamWriter(s.getOutputStream(), "UTF-8"))) {
                w.write(line);
                w.flush();
                cb.onResult("Envoyé: " + line.trim());
            } catch (Exception e) {
                cb.onError(e);
            }
        }).start();
    }
}
