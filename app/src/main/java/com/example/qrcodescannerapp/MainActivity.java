package com.example.qrcodescannerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView;
    BarcodeDetector barcodeDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.cameraPreview);
        textView = findViewById(R.id.textView);
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector)
                .setRequestedPreviewSize(640,480).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
// here, Permission is not granted
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {android.Manifest.permission.CAMERA}, 50);
                }
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();

            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray qrCodes = detections.getDetectedItems();
                if(qrCodes.size() != 0){
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator)getApplicationContext()
                                    .getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                            Barcode code = (Barcode) qrCodes.valueAt(0);
                            textView.setText(code.displayValue);
                            return;
                        }
                    });
                }
            }
        });
    }
}
