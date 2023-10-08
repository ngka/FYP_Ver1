package com.example.fyp_ocr_order;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EmployeeUpdate_Ng extends AppCompatActivity {
    Button dateButton,statusButton ;
    String date, STATUS;  // Declare STATUS here
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_IMAGE_PICK = 1;
    EditText edit_EmployeeName;
    EditText orderIdEditText;
    EditText Location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ng);
        setTitle("Update information");

        edit_EmployeeName = findViewById(R.id.EmployeeName);
        orderIdEditText = findViewById(R.id.OrderID);
        Location = findViewById(R.id.Location);
        dateButton = findViewById(R.id.dateButton);  // Assumes you have a Button with this id in your layout
        statusButton = findViewById(R.id.status_button);
        ImageButton button = findViewById(R.id.submit);
        ImageButton cameraButton = findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeUpdate_Ng.this);
                builder.setTitle("Choose Image Source");
                builder.setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: // Choose to take photo
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                        }
                                        break;

                                    case 1: // Choose from gallery
                                        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
                                        break;
                                }
                            }
                        });
                builder.show();
            }
        });

        dateButton.setOnClickListener(view -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EmployeeUpdate_Ng.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        date = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        dateButton.setText(date);
                        dateButton.setBackgroundColor(Color.TRANSPARENT);
                    },
                    year,
                    month,
                    day);
            datePickerDialog.show();
        });

        statusButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeUpdate_Ng.this);
            builder.setTitle("Choose a status")
                    .setItems(new String[]{"open", "closed"},
                            (dialog, which) -> {
                                switch (which) {
                                    case 0:
                                        STATUS = "open";
                                        break;
                                    case 1:
                                        STATUS = "closed";
                                        break;
                                }
                                statusButton.setText(STATUS);
                                statusButton.setBackgroundColor(Color.TRANSPARENT);
                            });
            builder.create().show();
        });

        button.setOnClickListener(view -> {
            String EmployeeName = edit_EmployeeName.getText().toString();
            String OrderID = orderIdEditText.getText().toString();
            String location = Location.getText().toString();

            RequestQueue queue = Volley.newRequestQueue(EmployeeUpdate_Ng.this);
            String url = "http://192.168.56.49/FYP/FYP_websiteData/User_Website_workVersion/update_employee.php?Employee_Name="+ EmployeeName + "&OrderID=" + OrderID + "&Date=" + date +"&STATUS=" + STATUS + "&Location=" + location;

            StringRequest myReq = new StringRequest(Request.Method.GET, url,
                    response -> {
                        if (response.equals("Fail")){
                            Toast.makeText(EmployeeUpdate_Ng.this, "FAIL", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(EmployeeUpdate_Ng.this, "Data added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EmployeeUpdate_Ng.this, EmployeeUpdate_Ng.class);
                        startActivity(intent);
                        finish();
                    },
                    error -> Log.e("Error", error.getLocalizedMessage())) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Employee_Name", EmployeeName);
                    params.put("OrderID", OrderID);
                    params.put("Date", date);
                    params.put("STATUS", STATUS);
                    params.put("Location", location);

                    return params;
                }
            };
            queue.add(myReq);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_IMAGE_PICK) && resultCode == RESULT_OK) {
            Bitmap imageBitmap;
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
            } else {
                Uri imageUri = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            InputImage image = InputImage.fromBitmap(imageBitmap, 0);
            TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            textRecognizer.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text result) {
                            for (Text.TextBlock block : result.getTextBlocks()) {
                                for (Text.Line line : block.getLines()) {
                                    String lineText = line.getText();
                                    if (lineText.contains("EmployeeName:")) {
                                        int index = lineText.indexOf("EmployeeName:");
                                        String employeeName = lineText.substring(index + "EmployeeName:".length()).trim();
                                        edit_EmployeeName.setText(employeeName);
                                    } else if (lineText.contains("OrderID:")) {
                                        int index = lineText.indexOf("OrderID:");
                                        String orderId = lineText.substring(index + "OrderID:".length()).trim();
                                        orderIdEditText.setText(orderId);
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                        }
                    });
        }
    }

}
