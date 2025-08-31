package com.example.voxignota;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TranslationActivity extends AppCompatActivity {
    private TextView captions;
    private Button backBtn, saveBtn;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        captions = findViewById(R.id.captions);
        backBtn = findViewById(R.id.backBtn);
        saveBtn = findViewById(R.id.saveHistoryBtn);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(v -> finish());

        // Example: when user clicks Save, store the current caption as history
        saveBtn.setOnClickListener(v -> {
            String resultText = captions.getText().toString();
            if (resultText == null || resultText.trim().isEmpty()) {
                Toast.makeText(this, "No translation to save.", Toast.LENGTH_SHORT).show();
                return;
            }
            saveHistory("SignToText", resultText);
        });
    }

    private void saveHistory(String type, String result) {
        String uid = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "User not signed in.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("type", type);
        data.put("result", result);
        data.put("timestamp", System.currentTimeMillis());

        db.collection("history")
          .add(data)
          .addOnSuccessListener(docRef -> Toast.makeText(TranslationActivity.this, "Saved to history", Toast.LENGTH_SHORT).show())
          .addOnFailureListener(e -> Toast.makeText(TranslationActivity.this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
