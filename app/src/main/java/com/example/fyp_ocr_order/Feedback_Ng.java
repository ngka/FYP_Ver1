package com.example.fyp_ocr_order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Feedback_Ng extends AppCompatActivity {
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_ng);
        setTitle("Feedback to us");

        button = findViewById(R.id.submit);
        EditText edit_Username = findViewById(R.id.Username);
        EditText edit_Email = findViewById(R.id.Email);
        EditText edit_Question = findViewById(R.id.Question);


        button.setOnClickListener(view -> {
            String Username = edit_Username.getText().toString();
            String Email = edit_Email.getText().toString();
            String Question = edit_Question.getText().toString();

            RequestQueue queue = Volley.newRequestQueue(Feedback_Ng.this);
            String url = "http://192.168.56.49/FYP/FYP_websiteData/User_Website_workVersion/create.php?Username="+ Username + "&Email=" + Email + "&Question=" + Question;

            StringRequest myReq = new StringRequest(Request.Method.GET, url,
                    response -> {
                        if (response.equals("Fail")){
                            Toast.makeText(Feedback_Ng.this, "FAIL", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(Feedback_Ng.this, "Data added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Feedback_Ng.this, PublicuserPage_Ng.class);
                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        Log.e("Error", error.getLocalizedMessage());
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Username", Username);
                    params.put("Email", Email);
                    params.put("Question", Question);
                    return params;
                }
            };
            queue.add(myReq);
        });

    }

}
