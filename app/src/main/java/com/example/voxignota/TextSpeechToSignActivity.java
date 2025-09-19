package com.example.voxignota;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;



public class TextSpeechToSignActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_speech_to_sign);
        ImageButton roundButton = findViewById(R.id.roundButton);

        roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TextSpeechToSignActivity.this, HomeActivity.class);
                // Optional: clear the back stack if you don't want to return here on back press
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // optional: closes the translation activity
            }

            private void startActivity(Intent intent) {
            }
        });
    }
}
