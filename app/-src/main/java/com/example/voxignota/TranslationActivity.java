package com.example.voxignota;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.os.SystemClock;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Locale;
import java.io.FileInputStream;
import android.content.res.AssetFileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import org.tensorflow.lite.Interpreter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranslationActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = "TranslationActivity";
    private PreviewView previewView;
    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector;
    private Preview preview;
    private ImageAnalysis imageAnalysis;
    private TextView captions;
    private AppDatabase db;
    private TextToSpeech tts;
    private Button soundBtn;
    private Interpreter tflite;
    private String[] labels;
    private final AtomicLong lastInference = new AtomicLong(0);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ProgressBar loadingIndicator;
    private TextView fpsCounter;
    private long lastFrameTime = 0;

    private ByteBuffer inputBuffer;
    private ByteBuffer outputBuffer;


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Start the camera.
                    startCamera();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied.
                    Toast.makeText(this, "Camera permission is required for this feature.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        previewView = findViewById(R.id.cameraPreview);
        Button toggleButton = findViewById(R.id.toggle);
        captions = findViewById(R.id.captions);
        Button saveHistoryBtn = findViewById(R.id.saveHistoryBtn);
        soundBtn = findViewById(R.id.soundBtn);
        loadingIndicator = findViewById(R.id.loading);
        fpsCounter = findViewById(R.id.fpsCounter);

        db = AppDatabase.getDatabase(this);
        tts = new TextToSpeech(this, this);

        soundBtn.setOnClickListener(v -> speakOut());

        // Check for permission and start camera if granted, or request otherwise
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

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
                    Log.e(TAG, "Failed to check for camera availability.", e);
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

    private void startCamera() {
        loadingIndicator.setVisibility(View.VISIBLE);
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
                    loadingIndicator.setVisibility(View.GONE);
                    return;
                }

                preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // ImageAnalysis for in-frame inference
                imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(TranslationActivity.this), this::analyzeImage);

                bindCameraUseCases();

                // Load the TFLite model from assets (model.tflite)
                try {
                    tflite = new Interpreter(loadModelFile("model.tflite"));
                    tflite.allocateTensors(); // Add this line
                    // Allocate buffers based on model input/output tensor shapes
                    int[] inputShape = tflite.getInputTensor(0).shape(); // {1, 64, 64, 3}
                    inputBuffer = ByteBuffer.allocateDirect(inputShape[0] * inputShape[1] * inputShape[2] * inputShape[3] * 4).order(ByteOrder.nativeOrder());

                    int[] outputShape = tflite.getOutputTensor(0).shape(); // {1, 59}
                    outputBuffer = ByteBuffer.allocateDirect(outputShape[0] * outputShape[1] * 4).order(ByteOrder.nativeOrder());

                } catch (Exception e) {
                    Log.e(TAG, "Error loading tflite model or allocating buffers", e);
                }

                // Load labels - if you have a labels file in assets, load it here. Otherwise use fallback.
                labels = loadFallbackLabels();
                loadingIndicator.setVisibility(View.GONE);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error initializing camera provider", e);
                loadingIndicator.setVisibility(View.GONE);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void saveToHistory(String text) {
        HistoryItem historyItem = new HistoryItem();
        historyItem.text = text;
        historyItem.timestamp = System.currentTimeMillis();

        executorService.execute(() -> db.historyDao().insert(historyItem));
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
                    preview,
                    imageAnalysis
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
        if (tflite != null) {
            tflite.close();
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
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

    // Load model file from assets into a MappedByteBuffer
    private MappedByteBuffer loadModelFile(String filename) throws IOException {
        try (AssetFileDescriptor fileDescriptor = getAssets().openFd(filename);
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    // Very small fallback labels - replace with actual mapping from the model author if available
    private String[] loadFallbackLabels() {
        // The model outputs 59 classes. This fallback maps 0-25 to A-Z, 26-35 to 0-9, and the rest to placeholders.
        String[] fallback = new String[59];
        for (int i = 0; i < 26; i++) fallback[i] = String.valueOf((char) ('A' + i));
        int idx = 26;
        for (int d = 0; d < 10; d++) fallback[idx++] = String.valueOf(d);
        while (idx < fallback.length) fallback[idx++] = "?";
        return fallback;
    }

    // Preprocess a Bitmap to the model input: resize to 64x64 and normalize to [0,1]
    private void preprocessBitmap(Bitmap bmp, ByteBuffer buffer) {
        final int targetW = 64;
        final int targetH = 64;
        Bitmap resized = Bitmap.createScaledBitmap(bmp, targetW, targetH, true);
        buffer.rewind();
        int[] pixels = new int[targetW * targetH];
        resized.getPixels(pixels, 0, targetW, 0, 0, targetW, targetH);
        for (int color : pixels) {
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;
            buffer.putFloat(r);
            buffer.putFloat(g);
            buffer.putFloat(b);
        }
    }


    // Run inference and return predicted class index
    private int runInference(Bitmap bmp) {
        if (tflite == null || inputBuffer == null || outputBuffer == null) {
            Log.e(TAG, "TFLite Interpreter or buffers not initialized.");
            return -1;
        }
        preprocessBitmap(bmp, inputBuffer);

        try {
            outputBuffer.rewind();
            tflite.run(inputBuffer, outputBuffer);
        } catch (Exception e) {
            Log.e(TAG, "TFLite run error", e);
            return -1;
        }
        // argmax
        outputBuffer.rewind();
        int argmax = 0;
        float max = outputBuffer.getFloat();
        for (int i = 1; i < labels.length; i++) {
            float current = outputBuffer.getFloat();
            if (current > max) {
                max = current;
                argmax = i;
            }
        }
        return argmax;
    }

    // Throttled analyzer: runs inference at most ~6 times per second and appends stable predictions
    private void analyzeImage(ImageProxy image) {
        long now = SystemClock.elapsedRealtime();
        if (now - lastInference.get() < 166) { // ~6 FPS
            image.close();
            return;
        }
        lastInference.set(now);

        // Calculate and display FPS
        if (lastFrameTime != 0) {
            long deltaTime = now - lastFrameTime;
            float fps = 1000.0f / deltaTime;
            runOnUiThread(() -> fpsCounter.setText(String.format(Locale.US, "FPS: %.1f", fps)));
        }
        lastFrameTime = now;

        // Try to get a bitmap from previewView if available (easier) otherwise convert ImageProxy
        Bitmap bmp = previewView.getBitmap();
        if (bmp == null) {
            image.close();
            return;
        }

        // Run inference on a cropped center square to focus on hand area
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size = Math.min(w, h);
        int left = (w - size) / 2;
        int top = (h - size) / 2;
        Bitmap crop = Bitmap.createBitmap(bmp, left, top, size, size);

        int pred = runInference(crop);
        if (pred >= 0 && pred < labels.length) {
            String ch = labels[pred];
            runOnUiThread(() -> {
                String cur = captions.getText().toString();
                // Simple append behavior - avoid adding placeholders
                if (!"?".equals(ch)) {
                    captions.setText(new StringBuilder(cur).append(ch).toString());
                }
            });
        }

        image.close();
    }
}
