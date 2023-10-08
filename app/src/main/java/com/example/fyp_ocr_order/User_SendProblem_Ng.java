package com.example.fyp_ocr_order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fyp_ocr_order.ml.Model400;
import com.example.fyp_ocr_order.ml.ModelMLP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class User_SendProblem_Ng extends AppCompatActivity {
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_add_ng);
        setTitle("User Input data");

        button = findViewById(R.id.submit);
        EditText editText1 = findViewById(R.id.value1);
        EditText editText2 = findViewById(R.id.value2);
        EditText editText3 = findViewById(R.id.value3);
        EditText editText4 = findViewById(R.id.value4); //added_txt
        EditText editText5 = findViewById(R.id.value5);
        EditText editText6 = findViewById(R.id.value6);
        EditText editText7 = findViewById(R.id.value7);
        EditText editText8 = findViewById(R.id.value8);

        button.setOnClickListener(view -> {
            String Title = editText1.getText().toString();
            String description_txt = editText2.getText().toString();
            String day = editText3.getText().toString();
            String month = editText4.getText().toString();
            String added_txt = editText5.getText().toString();
            String year = editText6.getText().toString();
            String company = editText7.getText().toString();
            String fullname = editText8.getText().toString();
            // Create JSON object
            JSONObject jsonObject = new JSONObject();
            try {
                // Convert description_txt to TensorBuffer
                TensorBuffer inputFeature0 = convertStringToTensorBuffer(description_txt);

                // Run model inference
                Model400.Outputs outputs;
                try {
                    Model400 model = Model400.newInstance(getApplicationContext());
                    outputs = model.process(inputFeature0);
                    model.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error running model inference!", Toast.LENGTH_LONG).show();
                    return;  // Exit the method
                }

                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                String classification;
                float[] outputArr = outputFeature0.getFloatArray();
                if (outputArr[0] > 0.5) {
                    classification = "NonUrgent";
                } else {
                    classification = "Urgent";
                }

                // Use classification to determine filename
                String filename;
                if (classification.equals("Urgent")) {
                    filename = "Urgent.json";
                } else {
                    filename = "NonUrgent.json";
                }

                jsonObject.put("Title", Title);
                jsonObject.put("description_txt", description_txt);
                jsonObject.put("day", day);
                jsonObject.put("month", month);
                jsonObject.put("year", year);
                jsonObject.put("added_txt", added_txt);
                jsonObject.put("company", company);
                jsonObject.put("fullname", fullname);

                File file = new File(getFilesDir(), filename);
                JSONArray jsonArray;
                if (file.exists()) {
                    // If file exists, read the existing JSON array
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader bufferedReader = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        jsonArray = new JSONArray(sb.toString());
                        bufferedReader.close();
                        isr.close();
                        fis.close();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        jsonArray = new JSONArray();
                    }
                } else {
                    // If file does not exist, create a new JSON array
                    jsonArray = new JSONArray();
                }

                // Add the new JSON object to the JSON array
                jsonArray.put(jsonObject);

                // Convert JSON array to string
                String jsonString = jsonArray.toString();

                // Save JSON string to file
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(jsonString);
                    fileWriter.flush();
                    fileWriter.close();

                    // Show a Toast message to notify the user that the data has been saved
                    String message = String.format("Data saved in %s. The AI model predicted: %s.", filename, classification);
                    Toast.makeText(User_SendProblem_Ng.this, message, Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
    private TensorBuffer convertStringToTensorBuffer(String text) {
        // TODO: Replace this with your actual implementation
        // Convert the text to a one-hot encoding
        float[] oneHot = new float[2];
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 2) {
                oneHot[c] = 1.0f;
            }
        }

        // Put the one-hot encoding into a TensorBuffer
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 2}, DataType.FLOAT32);
        inputFeature0.loadArray(oneHot);

        return inputFeature0;
    }
}