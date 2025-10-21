package com.example.voxignota;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private int reportClickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

    public void goReport(View v) {
        reportClickCount++;
        if (reportClickCount >= 5) {
            final String url = "https://github.com/Akshay-86/Sign_Translator/issues/new";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            // Check if there's an app that can handle this intent
            if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                v.getContext().startActivity(intent);
            } else {
                Toast.makeText(v.getContext(), "No browser found to open the link", Toast.LENGTH_SHORT).show();
            }
            reportClickCount = 0; // Reset counter
        } else {
            int remainingClicks = 5 - reportClickCount;
            String message = "You are " + remainingClicks + " clicks away from reporting an issue.";
            Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

}
