package com.example.applicationtcp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText etHost, etPort, etA, etB;
    private Spinner spShape;
    private TextView tvStatus;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etHost = findViewById(R.id.etHost);
        etPort = findViewById(R.id.etPort);
        etA    = findViewById(R.id.etA);
        etB    = findViewById(R.id.etB);
        spShape = findViewById(R.id.spShape);
        tvStatus = findViewById(R.id.tvStatus);
        Button btnSend = findViewById(R.id.btnSend);

        List<String> shapes = Arrays.asList("rectangle", "carre", "cercle");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, shapes);
        spShape.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendShape());
    }

    private void sendShape() {
        String host = etHost.getText().toString().trim();
        if (TextUtils.isEmpty(host)) {
            tvStatus.setText("Veuillez saisir l'IP/host du serveur");
            return;
        }

        int port = parseInt(etPort.getText().toString(), 1234);
        String type = spShape.getSelectedItem().toString();
        double a = parseDouble(etA.getText().toString(), 1.0);
        double b = type.equals("rectangle") ? parseDouble(etB.getText().toString(), 1.0) : 0.0;

        ShapePayload payload = new ShapePayload(type, a, b);

        TcpClient.send(host, port, payload.toLine(), new TcpClient.Callback() {
            @Override public void onResult(String message) {
                runOnUiThread(() -> tvStatus.setText(message));
            }
            @Override public void onError(Exception e) {
                runOnUiThread(() -> tvStatus.setText("Erreur: " + e.getMessage()));
            }
        });
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
    private double parseDouble(String s, double def) {
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }
}
