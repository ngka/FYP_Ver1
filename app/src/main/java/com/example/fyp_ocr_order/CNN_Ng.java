package com.example.fyp_ocr_order;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp_ocr_order.ml.MyModel100;
import com.example.fyp_ocr_order.ml.MyModel500;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CNN_Ng extends AppCompatActivity {
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1f;
    private float lastTouchX = 0f;
    private float lastTouchY = 0f;
    private float lastFocusX = 0f;
    private float lastFocusY = 0f;

    ImageButton camera,gallery;
    ImageView imageView;
    TextView result;

    int imageSize = 256;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnn_ng);
        setTitle("Classify image");


        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());


        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);
        Button feeback = findViewById(R.id.toFee);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);

        camera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,3);
                }else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA},100);
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent,3);
            }
        });
        feeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CNN_Ng.this, Feedback_Ng.class);
                startActivity(intent);
            }
        });
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
    public void classifyImage(Bitmap image) {
        try {
            MyModel100 model = MyModel100.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; //RGB
                    byteBuffer.putFloat(((val >>16) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat(((val >>8) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            MyModel100.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidence.length; i++) {
                if (confidence[i] > maxConfidence) {
                    maxConfidence = confidence[i];
                    maxPos = i;
                }
            }

            String[] classes = {"DisneyLand", "GovernmentSecretariat", "HKWetlandPark", "Ocean Park", "Tian Tan Buddha", "WongTaiSin"};
            String detectedClass = classes[maxPos];

            result.setText(detectedClass);

            // If the detected object is GovernmentSecretariat, show it on Google Maps
            if (detectedClass.equals("GovernmentSecretariat")) {
                String geoUri = "geo:0,0?q=Hong+Kong+Government+Headquarters";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
            if (detectedClass.equals("DisneyLand")) {
                String geoUri = "geo:0,0?q=Hong+Kong+DisneyLand";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
            if (detectedClass.equals("HKWetlandPark")) {
                String geoUri = "geo:0,0?q=Hong+Kong+HKWetlandPark";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
            if (detectedClass.equals("Ocean Park")) {
                String geoUri = "geo:0,0?q=Hong+Kong+Ocean Park";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
            if (detectedClass.equals("WongTaiSin")) {
                String geoUri = "geo:0,0?q=Hong+Kong+WongTaiSin";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
            if (detectedClass.equals("Tian Tan Buddha")) {
                String geoUri = "geo:0,0?q=Hong+Kong+Tian Tan Buddha";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }



            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode ==3){
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image,dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize,imageSize,false);
            classifyImage(image);
        }else{
            Uri dat = data.getData();
            Bitmap image=null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize,imageSize,false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

}
