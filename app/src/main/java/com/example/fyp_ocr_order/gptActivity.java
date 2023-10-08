package com.example.fyp_ocr_order;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class gptActivity extends AppCompatActivity {
    private Button btnSend;
    private EditText etMessage;
    private TextView tvResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gpt_activity_ng);

        btnSend = findViewById(R.id.btnSend);
        etMessage = findViewById(R.id.etMessage);
        tvResponse = findViewById(R.id.tvResponse);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String response = OpenAiChatService.chat(message);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResponse.setText(response);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
