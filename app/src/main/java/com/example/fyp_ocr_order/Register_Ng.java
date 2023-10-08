package com.example.fyp_ocr_order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register_Ng extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    TextView btnteacher;

    // create object of DatabaseReference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ng);
        setTitle("User Register Page");

        mAuth = FirebaseAuth.getInstance();

        final EditText fullname = findViewById(R.id.name);
        final EditText email = findViewById(R.id.email);
        final EditText school = findViewById(R.id.school);
        final EditText password = findViewById(R.id.password);
        final EditText conPassword = findViewById(R.id.conPassword);

        final Button registerBtn = findViewById(R.id.registerBtn);
        final TextView loginNowBtn = findViewById(R.id.loginNow);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fullnameTxt = fullname.getText().toString();
                final String emailTxt = email.getText().toString();
                final String schoolTxt = school.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String conPasswordTxt = conPassword.getText().toString();

                if (TextUtils.isEmpty(emailTxt) && TextUtils.isEmpty(passwordTxt)) {
                    Toast.makeText(Register_Ng.this, "Please Enter Data", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(emailTxt)) {
                    Toast.makeText(Register_Ng.this, "Please Enter Email Address", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(passwordTxt)) {
                    Toast.makeText(Register_Ng.this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(schoolTxt)) {
                    Toast.makeText(Register_Ng.this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(fullnameTxt)) {
                    Toast.makeText(Register_Ng.this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(conPasswordTxt)) {
                    Toast.makeText(Register_Ng.this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(fullnameTxt)
                        || !TextUtils.isEmpty(emailTxt)
                        || !TextUtils.isEmpty(schoolTxt)
                        || !TextUtils.isEmpty(passwordTxt)
                        || !TextUtils.isEmpty(conPasswordTxt)) {

                    register_user(fullnameTxt, emailTxt, schoolTxt, passwordTxt);
                }
            }
        });

        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register_Ng.this, Login_Ng.class));

            }
        });


    }

    private void register_user(final String fullnameTxt, final String emailTxt, final String schoolTxt, final String passwordTxt) {
        mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser current_user = mAuth.getCurrentUser();
                            String uid = current_user.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", fullnameTxt);
                            userMap.put("email", emailTxt);
                            userMap.put("school", schoolTxt);
                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(Register_Ng.this, Login_Ng.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(Register_Ng.this, "Registration failed. Please try again later.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Register_Ng.this, "Registration failed. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}