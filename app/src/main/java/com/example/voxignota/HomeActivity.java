package com.example.voxignota;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
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
}
