package com.example.fyp_ocr_order;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class loadpageActivity_Ng extends AppCompatActivity {
    ProgressBar progressBar;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laod_activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);



        progressBar = findViewById(R.id.progress_bar);
        textView = findViewById(R.id.textView);


        progressBar.setMax(100);
        progressBar.setScaleY(3f);

        progressAnimation();

    }

    public void progressAnimation(){
        ProgressBarAnimation_Ng anim = new ProgressBarAnimation_Ng(this, progressBar,textView,0, 100f);
        anim .setDuration(3000);
        progressBar.setAnimation(anim);
    }

}

