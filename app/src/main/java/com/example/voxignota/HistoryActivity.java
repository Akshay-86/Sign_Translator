package com.example.voxignota;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView historyList = findViewById(R.id.historyList);

        SharedPreferences prefs = getSharedPreferences("history", MODE_PRIVATE);
        Set<String> historySet = prefs.getStringSet("history_set", new HashSet<>());

        ArrayList<String> historyArrayList = new ArrayList<>(historySet);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, historyArrayList);

        historyList.setAdapter(adapter);
    }

}
