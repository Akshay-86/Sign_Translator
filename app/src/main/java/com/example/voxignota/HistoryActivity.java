package com.example.voxignota;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<String> items = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private static final String TAG = "HistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.historyList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadHistory();
    }

    private void loadHistory() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login or continue", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("history")
          .whereEqualTo("uid", uid)
          .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
          .get()
          .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                  items.clear();
                  for (QueryDocumentSnapshot doc : task.getResult()) {
                      long ts = doc.getLong("timestamp") != null ? doc.getLong("timestamp") : 0L;
                      String type = doc.getString("type");
                      String result = doc.getString("result");
                      String display = result + "  (" + type + ")";
                      items.add(display);
                  }
                  adapter.notifyDataSetChanged();
              } else {
                  Log.w(TAG, "Error getting documents.", task.getException());
                  Toast.makeText(HistoryActivity.this, "Failed to load history", Toast.LENGTH_SHORT).show();
              }
          });
    }
}
