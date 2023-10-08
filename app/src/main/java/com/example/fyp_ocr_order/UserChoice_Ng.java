package com.example.fyp_ocr_order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class UserChoice_Ng extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userchoice_ng);

        Button user = findViewById(R.id.user);
        Button tv_employee = findViewById(R.id.employee);


        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserChoice_Ng.this, Login_Ng.class);
                startActivity(intent);
            }
        });
        tv_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserChoice_Ng.this, Employee_Login_Ng.class);
                startActivity(intent);
            }
        });

    }
}
