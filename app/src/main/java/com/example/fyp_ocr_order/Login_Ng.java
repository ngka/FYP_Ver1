package com.example.fyp_ocr_order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Login_Ng extends AppCompatActivity {
    TextView output;
    TextView btnteacher;
    private FirebaseAuth mAuth;
    private BiometricPrompt biometricPrompt;
    private DatabaseReference mDatabase;

    private Executor executor;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Login Account");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_ng);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        output=(TextView)findViewById( R.id._out_encryp );
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final Button loginBtn = findViewById(R.id.loginBtn);
        final TextView registerNowBtn = findViewById(R.id.registerNowBtn);
        Button button = findViewById(R.id.btn_authenticate);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new androidx.biometric.BiometricPrompt(Login_Ng.this,
                executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                encryption(String.valueOf(output)); // 加密敏感数据
                startActivity(new Intent(Login_Ng.this, PublicuserPage_Ng.class));
                Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        Button biometricLoginButton = findViewById(R.id.btn_authenticate);
        biometricLoginButton.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);

        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailTXT = email.getText().toString();
                final String passwordTxT = password.getText().toString();

                if(TextUtils.isEmpty(emailTXT) && TextUtils.isEmpty(passwordTxT)) {
                    Toast.makeText(Login_Ng.this, "Please Enter Email Address and Password", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(emailTXT)) {
                    Toast.makeText(Login_Ng.this, "Please Enter Email Address", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(passwordTxT)) {
                    Toast.makeText(Login_Ng.this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
                } else if(!TextUtils.isEmpty(emailTXT) || !TextUtils.isEmpty(passwordTxT)) {
                    loginUser(emailTXT, passwordTxT);
                }
            }
        });

        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login_Ng.this, Register_Ng.class));
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    // 加密您需要存儲的敏感資料
                    try {
                        // 登入成功後跳轉到 OCR_Ng 介面
                        Intent intent = new Intent(Login_Ng.this, PublicuserPage_Ng.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(Login_Ng.this, "Login Success", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login_Ng.this, "Login failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public String encryption(String input){
        try {
            // Do Encryption
            // output.setText(x); // Show the result her

            //Generate Key
            KeyGenerator keyGen=KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();

            // Generate Matrix
            byte[] x =new byte[64];
            SecureRandom random = new SecureRandom();
            random.nextBytes(x);
            //Set EncrpyMode
            Cipher cipher = Cipher.getInstance("AES");

            //Apply Tools
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(x);

            //Set Mode to Encryption
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            //Start Encrypt
            byte[] cipherText = cipher.doFinal(input.trim().getBytes());
            String conVal = Base64.encodeToString(cipherText,Base64.DEFAULT);
            //Shwo
            output.setText(output.getText()+"Decrypted Message:"+cipherText);
            decryption(conVal,secretKey,x);
            // decryption(x,x,x);
        }catch (Exception e){
        }
        return input;
    }
    public void decryption(String input, SecretKey key, byte[] IV){

        try{
            byte[] encText = Base64.decode(input,Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptedText = cipher.doFinal(encText);  // decrypt it and put the result to decryptedText
            String d = new String(decryptedText, StandardCharsets.UTF_8);
            //output.setText(output.getText()+"Decrypted Message:"+d);
            // Do Decryption
        }catch (Exception e){
            e.printStackTrace();
        }

        // output.setText(x); // Show the result here
    }
    public static String encoderfun(byte[] decval){
        String conval = Base64.encodeToString(decval,Base64.DEFAULT);
        return conval;
    }
    public static byte[] decoderfun(String enval){
        byte[] conVal= Base64.decode(enval,Base64.DEFAULT);
        return conVal;
    }
}