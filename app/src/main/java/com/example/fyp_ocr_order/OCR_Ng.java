package com.example.fyp_ocr_order;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class OCR_Ng extends AppCompatActivity {
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1f;
    private float lastTouchX = 0f;
    private float lastTouchY = 0f;
    private float lastFocusX = 0f;
    private float lastFocusY = 0f;
    private TextView ttt;


    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private ImageView  imageView;
    private TextView recognizedTextView;
    ImageButton btnToCNN;
    private TextView translatedTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_ng);
        setTitle("Optical Character Recognition");



        // 初始化scaleGestureDetector
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        recognizedTextView = findViewById(R.id.recognized_text_view);
        translatedTextView = findViewById(R.id.translated_text_view);
        btnToCNN = findViewById(R.id.TCNN);
        Button btnImage = findViewById(R.id.btnImage);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });
        btnToCNN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OCR_Ng.this, PublicuserPage_Ng.class);
                startActivity(intent);
            }
        });
        imageView = findViewById(R.id.image);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // 处理缩放手势
        scaleGestureDetector.onTouchEvent(motionEvent);

        // 处理移动手势
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = motionEvent.getX();
                lastTouchY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = motionEvent.getX() - lastTouchX;
                float dy = motionEvent.getY() - lastTouchY;
                imageView.setTranslationX(imageView.getTranslationX() + dx);
                imageView.setTranslationY(imageView.getTranslationY() + dy);
                lastTouchX = motionEvent.getX();
                lastTouchY = motionEvent.getY();
                break;
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            // 获取缩放焦点的坐标
            lastFocusX = detector.getFocusX();
            lastFocusY = detector.getFocusY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            // 计算缩放比例
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            scaleFactor = Math.max(1f, Math.min(scaleFactor, 10.0f));

            // 计算ImageView的边界
            float viewWidth = imageView.getWidth() * scaleFactor;
            float viewHeight = imageView.getHeight() * scaleFactor;
            float maxWidth = imageView.getMaxWidth();
            float maxHeight = imageView.getMaxHeight();

            // 根据缩放比例调整ImageView的大小和位置
            if (viewWidth > maxWidth) {
                viewWidth = maxWidth;
                scaleFactor = viewWidth / imageView.getWidth();
            }
            if (viewHeight > maxHeight) {
                viewHeight = maxHeight;
                scaleFactor = viewHeight / imageView.getHeight();
            }
            float translationX = (maxWidth - viewWidth) / 2;
            float translationY = (maxHeight - viewHeight) / 2;
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);

            // 计算缩放焦点的偏移量，并将其应用到ImageView的位置上
            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();
            float focusShiftX = (focusX - lastFocusX) * scaleFactor;
            float focusShiftY = (focusY - lastFocusY) * scaleFactor;
            imageView.setTranslationX(imageView.getTranslationX() + focusShiftX);
            imageView.setTranslationY(imageView.getTranslationY() + focusShiftY);

            lastFocusX = focusX;
            lastFocusY = focusY;

            return true;
        }
    }

    public void translateText(String sourceText) {
        // 創建一個翻譯選項，其中源語言為英文，目標語言為中文
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.CHINESE)
                        .build();

        // 創建一個翻譯器
        Translator translator = Translation.getClient(options);

        // 下載翻譯模型（如果尚未下載）
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()  // 這一行表示只在 WiFi 條件下下載模型
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                // 模型下載成功，可以進行翻譯
                                translator.translate(sourceText)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(String translatedText) {
                                                        // 翻譯成功，更新 UI 以顯示翻譯結果
                                                        translatedTextView.setText(translatedText);
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // 處理錯誤
                                                        e.printStackTrace();
                                                    }
                                                });
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 處理錯誤
                                e.printStackTrace();
                            }
                        });
    }

    private void showImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                pickImageFromGallery();
                                break;
                            case 1:
                                pickImageFromCamera();
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void pickImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);
            recognizeTextFromImageAndUpload(imageUri);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            recognizeTextFromBitmapAndUpload(imageBitmap);
        }
    }

    private void recognizeTextFromImageAndUpload(Uri imageUri) {
        try {
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(this, imageUri);

            FirebaseVisionDocumentTextRecognizer recognizer = FirebaseVision.getInstance()
                    .getCloudDocumentTextRecognizer();

            recognizer.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                        @Override
                        public void onSuccess(FirebaseVisionDocumentText firebaseVisionDocumentText) {
                            String recognizedText = firebaseVisionDocumentText.getText();
                            recognizedTextView.setText(recognizedText);
                            translateText(recognizedText);
                            uploadRecognizedText(recognizedText, imageUri);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("OCR", "Error recognizing text", e);
                        }
                    });

        } catch (IOException e) {
            Log.e("OCR", "Error reading image file", e);
        }
    }

    private void recognizeTextFromBitmapAndUpload(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        recognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        String recognizedText = firebaseVisionText.getText();
                        recognizedTextView.setText(recognizedText);
                        translateText(recognizedText);
                        uploadRecognizedText(recognizedText, null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("OCR", "Error recognizing text", e);
                    }
                });
    }

    private void uploadRecognizedText(String recognizedText, Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String imageName = System.currentTimeMillis() + ".jpg";
        String textName = System.currentTimeMillis() + ".txt";

        StorageReference imageRef = storageRef.child("CameraPhoto/" + imageName);
        StorageReference textRef = storageRef.child("Cameratexts/" + textName);

        UploadTask imageUpload = null; // declare the variable here

        if (imageUri != null) {
            imageUpload = imageRef.putFile(imageUri); // assign the value here
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            imageUpload = imageRef.putBytes(data); // assign the value here
        }

        byte[] textBytes = recognizedText.getBytes();
        UploadTask textUpload = textRef.putBytes(textBytes);

        imageUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        Log.d("OCR", "Image uploaded: " + imageUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("OCR", "Error uploading image", e);
            }
        });

        textUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                textRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String textUrl = uri.toString();
                        Log.d("OCR", "Text uploaded: " + textUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("OCR", "Error uploading text", e);
            }
        });
    }
}