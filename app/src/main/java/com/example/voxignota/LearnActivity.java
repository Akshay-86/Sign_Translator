
package com.example.voxignota;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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
    }
}
