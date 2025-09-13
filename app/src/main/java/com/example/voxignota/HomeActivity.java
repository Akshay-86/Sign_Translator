package com.example.voxignota;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class HomeActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA);
        }


        findViewById(R.id.signToTextBtn).setOnClickListener(v ->
            startActivity(new Intent(HomeActivity.this, TranslationActivity.class)));

        findViewById(R.id.signToSpeechBtn).setOnClickListener(v ->
            startActivity(new Intent(HomeActivity.this, TranslationActivity.class)));

        findViewById(R.id.textToSignBtn).setOnClickListener(v ->
            startActivity(new Intent(HomeActivity.this, TextSpeechToSignActivity.class)));

        findViewById(R.id.speechToSignBtn).setOnClickListener(v ->
            startActivity(new Intent(HomeActivity.this, TextSpeechToSignActivity.class)));

        findViewById(R.id.learnBtn).setOnClickListener(v ->
            startActivity(new Intent(HomeActivity.this, LearnActivity.class)));

        findViewById(R.id.historyBtn).setOnClickListener(v ->
            startActivity(new Intent(HomeActivity.this, HistoryActivity.class)));
    }
}
