package com.example.fyp_ocr_order;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SerchLocation_Ng extends AppCompatActivity {
    TextView textView;
    ImageButton button;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch_ng);
        setTitle("Search Location");

        button = findViewById(R.id.submit);
        textView = findViewById(R.id.textView);
        EditText edit_location = findViewById(R.id.edit_location);
        spinner = findViewById(R.id.spinner_status);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        button.setOnClickListener(view -> {
            String location = edit_location.getText().toString();
            String status = spinner.getSelectedItem().toString();
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://192.168.56.49/FYP/FYP_websiteData/User_Website_workVersion/serch_location.php?Location=" + location + "&STATUS=" + status;
            StringRequest myReq = new StringRequest(Request.Method.GET, url,
                    response -> {
                        if (!response.equals("No data found")) {
                            String data = response.trim();
                            textView.setText(data);
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
