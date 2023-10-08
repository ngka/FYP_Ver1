package com.example.fyp_ocr_order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class RegisterActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btRegister = findViewById(R.id.bt_register);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                String confirmPsd = etConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    etUsername.setError("請輸入員工編號");
                    etUsername.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("請輸入密碼");
                    etPassword.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(confirmPsd)) {
                    etConfirmPassword.setError("請輸入密碼");
                    etConfirmPassword.requestFocus();
                    return;
                }
                if (!password.equals(confirmPsd)) {
                    etConfirmPassword.setError("密碼不一致");
                    etConfirmPassword.requestFocus();
                    return;
                }

                registerUser(username, password);
            }
        });
    }

    private void registerUser(final String username, final String password) {
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String uid = currentUser.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Appusers").child(uid);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("username", username);
                            userMap.put("password", password);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "註冊成功", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "註冊失敗", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "註冊失敗", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}