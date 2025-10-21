
package com.example.voxignota;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.flexbox.FlexboxLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class TextSpeechToSignActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private EditText inputText;
    private Button micBtn, translateBtn;
    private SpeechRecognizer speechRecognizer;
    private FlexboxLayout signsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_speech_to_sign);

        inputText = findViewById(R.id.inputText);
        micBtn = findViewById(R.id.micBtn);
        translateBtn = findViewById(R.id.translateBtn);
        signsLayout = findViewById(R.id.signsLayout);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {}

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null) {
                    inputText.setText(data.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(TextSpeechToSignActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TextSpeechToSignActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                } else {
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }
        });

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputText.getText().toString().trim().toLowerCase();
                if (text.isEmpty()) {
                    Toast.makeText(TextSpeechToSignActivity.this, "Please enter text or speak.", Toast.LENGTH_SHORT).show();
                    return;
                }

                signsLayout.removeAllViews();
                String[] words = text.split("\\s+");

                for (String word : words) {
                    // --- START OF THE CHANGE ---
                    HorizontalScrollView horizontalScrollView = new HorizontalScrollView(TextSpeechToSignActivity.this);
                    LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    horizontalScrollView.setLayoutParams(scrollParams);

                    LinearLayout wordLayout = new LinearLayout(TextSpeechToSignActivity.this);
                    wordLayout.setOrientation(LinearLayout.HORIZONTAL);
                    wordLayout.setPadding(0, 0, 10, 10);
                    // --- END OF THE CHANGE ---

                    for (char character : word.toCharArray()) {
                        ImageView imageView = new ImageView(TextSpeechToSignActivity.this);

                        float density = getResources().getDisplayMetrics().density;
                        int sizeInDp = 60;
                        int sizeInPx = (int) (sizeInDp * density);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
                        imageView.setLayoutParams(layoutParams);

                        try {
                            InputStream is = getAssets().open("signs/" + character + ".jpeg");
                            Drawable d = Drawable.createFromStream(is, null);
                            imageView.setImageDrawable(d);
                            imageView.setPadding(5,5,5,5);
                            wordLayout.addView(imageView);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(TextSpeechToSignActivity.this, "Error loading image for: " + character, Toast.LENGTH_SHORT).show();
                        }
                    }
                    // --- START OF THE CHANGE ---
                    horizontalScrollView.addView(wordLayout);
                    signsLayout.addView(horizontalScrollView);
                    // --- END OF THE CHANGE ---
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                micBtn.performClick();
            } else {
                Toast.makeText(this, "Permission to record audio is required for this feature.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goHome(View v){
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }
}
