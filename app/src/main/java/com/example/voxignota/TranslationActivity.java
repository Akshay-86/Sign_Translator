package com.example.voxignota;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;

public class TranslationActivity extends AppCompatActivity {

    private static final String TAG = "TranslationActivity";
    private PreviewView previewView;
    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector;
    private Preview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        previewView = findViewById(R.id.cameraPreview);
        Button toggleButton = findViewById(R.id.toggle);

        // Initialize to default back camera
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        // Initialize the camera preview
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                if (cameraProvider == null) {
                    Log.e(TAG, "Camera provider is null");
                    return;
                }

                preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                bindCameraUseCases();
                //updateToggleButtonText();

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error initializing camera provider", e);
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));

        // Handle toggle button click
        toggleButton.setOnClickListener(v -> {
            if (cameraProvider == null) {
                Log.e(TAG, "Camera provider not available for toggle.");
                return;
            }

            // Toggle camera selector
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                // Check if front camera is available
                try {
                    if (!cameraProvider.hasCamera(cameraSelector)) {
                        Log.w(TAG, "Front camera is not available.");
                        // Revert to back camera if front is not available
                        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                         // Optionally, inform the user e.g. via a Toast
                    }
                } catch (CameraInfoUnavailableException e) {
                    throw new RuntimeException(e);
                }
            } else {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
            }
            bindCameraUseCases();
            //updateToggleButtonText();
        });
    }

    private void bindCameraUseCases() {
        if (cameraProvider == null) {
            Log.e(TAG, "Camera provider is null, cannot bind use cases.");
            return;
        }
        try {
            cameraProvider.unbindAll(); // Unbind existing use cases
            cameraProvider.bindToLifecycle(
                    TranslationActivity.this,
                    cameraSelector,
                    preview
            );
        } catch (Exception e) {
            Log.e(TAG, "Error binding camera use cases", e);
        }
    }

/*    private void updateToggleButtonText() {
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            toggleButton.setText("Switch to Front");
        } else {
            toggleButton.setText("Switch to Back");
        }
    }*/

    public void goHome(View v){
        // Unbind camera before leaving activity to release resources
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure camera resources are released
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }
}
