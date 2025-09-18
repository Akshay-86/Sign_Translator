package com.example.voxignota;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        String[] itemslist = {"ITEM 1","ITEM 2","ITEM 3","ITEM 4","ITEM 5","ITEM 6","ITEM 7","ITEM 8","ITEM 9","ITEM 10","ITEM 11","ITEM 12","ITEM 13","ITEM 14","ITEM 15","ITEM 16","ITEM 17","ITEM 18","ITEM 19","ITEM 20"};
        ListView listView = findViewById(R.id.historyList);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemslist));

        ImageButton roundButton = findViewById(R.id.roundButton);

        roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, HomeActivity.class);
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
