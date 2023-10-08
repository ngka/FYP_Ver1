package com.example.fyp_ocr_order;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EmployeePage_Ng extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employeehome_ng);
        setTitle("Employee");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        FloatingActionButton fab = findViewById(R.id.fab);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // handle home button click
                        Intent homeIntent = new Intent(EmployeePage_Ng.this, UserChoice_Ng.class);
                        startActivity(homeIntent);
                        break;
                    case R.id.nav_back:
                        // handle back button click
                        finish();
                        break;
                    case R.id.nav_logout:
                        // handle logout button click
                        // This depends on how you manage user logins.
                        // Here is a simple way to clear user session and go back to login page.
                        SharedPreferences preferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent logoutIntent = new Intent(EmployeePage_Ng.this, Employee_Login_Ng.class);
                        startActivity(logoutIntent);
                        finish(); // This closes the current activity, so the user can't go back to it with the back button.
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        ImageView Data1 = (ImageView) findViewById(R.id.Data1);
        ImageView Data = (ImageView) findViewById(R.id.Data);
        ImageView ocr = (ImageView) findViewById(R.id.ocr);
        ImageView Query = (ImageView) findViewById(R.id.Query);
        ImageView Update = (ImageView) findViewById(R.id.Update);
        ImageView Chat = (ImageView) findViewById(R.id.Chat);
        ImageView Search = (ImageView) findViewById(R.id.Search);

        ocr.setOnClickListener(view -> {
            Intent intent = new Intent(EmployeePage_Ng.this, OCR_Ng.class);
            startActivity(intent);
        });



        Query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeePage_Ng.this, Query_Ng.class);
                startActivity(intent);
            }
        });

        Data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeePage_Ng.this, Employee_Query_Ng.class);
                startActivity(intent);
            }
        });
        Data1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeePage_Ng.this, Employee_Urgent_Ng2.class);
                startActivity(intent);
            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeePage_Ng.this, EmployeeUpdate_Ng.class);
                startActivity(intent);
            }
        });

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeePage_Ng.this, SerchLocation_Ng.class);
                startActivity(intent);
            }
        });
        Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeePage_Ng.this, gptActivity.class);
                startActivity(intent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeePage_Ng.this, gptActivity.class);
                startActivity(intent);
            }
        });





    }

}
