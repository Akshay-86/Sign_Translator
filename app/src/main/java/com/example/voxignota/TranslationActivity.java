package com.example.voxignota;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class TranslationActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = "TranslationActivity";
    private PreviewView previewView;
    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector;
    private Preview preview;
    private EditText captions;
    private AppDatabase db;
    private TextToSpeech tts;
    private Button soundBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        previewView = findViewById(R.id.cameraPreview);
        Button toggleButton = findViewById(R.id.toggle);
        captions = findViewById(R.id.captions);
        Button saveHistoryBtn = findViewById(R.id.saveHistoryBtn);
        soundBtn = findViewById(R.id.soundBtn);

        db = AppDatabase.getDatabase(this);
        tts = new TextToSpeech(this, this);

        soundBtn.setOnClickListener(v -> speakOut());

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
                    }
                } catch (CameraInfoUnavailableException e) {
                    throw new RuntimeException(e);
                }
            } else {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
            }
            bindCameraUseCases();
        });

        saveHistoryBtn.setOnClickListener(v -> {
            String textToSave = captions.getText().toString();
            if (!textToSave.isEmpty()) {
                saveToHistory(textToSave);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void saveToHistory(String text) {
        HistoryItem historyItem = new HistoryItem();
        historyItem.text = text;
        historyItem.timestamp = System.currentTimeMillis();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.historyDao().insert(historyItem);
                return null;
            }
        }.execute();
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
        // Ensure camera resources are released
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "This Language is not supported");
            } else {
                soundBtn.setEnabled(true);
            }
        } else {
            Log.e(TAG, "Initialization Failed!");
        }
    }

    private void speakOut() {
        String text = captions.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "");
    }
}
