package com.example.voxignota;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class LearnActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        String[] topics = {"Basics of Sign Language", "Common Phrases", "Finger Spelling", "Numbers"};
        ListView listView = findViewById(R.id.learnList);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topics));

        ImageButton roundButton = findViewById(R.id.roundButton);

        roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LearnActivity.this, HomeActivity.class);
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
