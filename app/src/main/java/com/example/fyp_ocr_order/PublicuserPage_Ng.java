package com.example.fyp_ocr_order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PublicuserPage_Ng extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userhome_ng);
        setTitle("Home Page");


        Button ocr = findViewById(R.id.ocr);
        Button cnn = findViewById(R.id.cnn);
        Button feedback = findViewById(R.id.feedback);
        Button add_data = findViewById(R.id.add_data);

        add_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PublicuserPage_Ng.this, User_SendProblem_Ng.class);
                startActivity(intent);
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PublicuserPage_Ng.this, Feedback_Ng.class);
                startActivity(intent);
            }
        });
        ocr.setOnClickListener(view -> {
            Intent intent = new Intent(PublicuserPage_Ng.this, OCR_Ng.class);
            startActivity(intent);
        });
        cnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PublicuserPage_Ng.this, CNN_Ng.class);
                startActivity(intent);
            }
        });


    }

}
