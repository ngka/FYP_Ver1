package com.example.fyp_ocr_order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Employee_Query_Ng extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_query_ng);
        setTitle("Urgent page");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // 讀取 JSON 文件
        try {
            File file = new File(getFilesDir(), "Urgent.json");
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            // 將 JSON 字符串轉換為 JSONArray
            JSONArray jsonArray = new JSONArray(sb.toString());

            // Set adapter
            MyAdapter adapter = new MyAdapter(jsonArray);
            recyclerView.setAdapter(adapter);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}

