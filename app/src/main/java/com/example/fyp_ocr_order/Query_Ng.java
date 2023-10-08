package com.example.fyp_ocr_order;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Query_Ng extends AppCompatActivity {
    TextView textView;
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_ng);
        setTitle("Query Question");

        button = findViewById(R.id.submit);
        textView = findViewById(R.id.textView);
        EditText edit_Username = findViewById(R.id.Username);
        EditText edit_Email = findViewById(R.id.Email);

        button.setOnClickListener(view -> {
            String Username = edit_Username.getText().toString();
            String Email = edit_Email.getText().toString();
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://IP/FYP/FYP_websiteData/User_Website_workVersion/get_data.php?Username="+ Username + "&Email=" + Email;
            StringRequest myReq = new StringRequest(Request.Method.GET, url,
                    response -> {
                        if (!response.equals("No data found")) {
                            String question = response.trim();
                            textView.setText(question);
                        } else {
                            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("Error", error.getLocalizedMessage());
                    });
            queue.add(myReq);
        });
    }
}

