package com.example.fyp_ocr_order;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPW_Ng extends AppCompatActivity {
    private EditText emailInput;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpw_ng);

        mAuth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.email);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = emailInput.getText().toString();

                if (!emailAddress.isEmpty()) {
                    mAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetPW_Ng.this, "Reset instructions sent to your email.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ResetPW_Ng.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ResetPW_Ng.this, "Please enter your email address.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
