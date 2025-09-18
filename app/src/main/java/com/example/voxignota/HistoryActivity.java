package com.example.voxignota;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity {

    private AppDatabase db;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = AppDatabase.getDatabase(this);

        RecyclerView recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(new HashMap<>());
        recyclerView.setAdapter(adapter);

        loadHistory();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadHistory() {
        new AsyncTask<Void, Void, List<HistoryItem>>() {
            @Override
            protected List<HistoryItem> doInBackground(Void... voids) {
                return db.historyDao().getAll();
            }

            @Override
            protected void onPostExecute(List<HistoryItem> historyItems) {
                if (historyItems != null) {
                    Map<String, List<HistoryItem>> groupedByDate = groupHistoryByDate(historyItems);
                    adapter.setItems(groupedByDate);
                }
            }
        }.execute();
    }

    private Map<String, List<HistoryItem>> groupHistoryByDate(List<HistoryItem> historyItems) {
        Map<String, List<HistoryItem>> grouped = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (HistoryItem item : historyItems) {
            String date = sdf.format(new Date(item.timestamp));
            if (!grouped.containsKey(date)) {
                grouped.put(date, new ArrayList<>());
            }
            Objects.requireNonNull(grouped.get(date)).add(item);
        }
        return grouped;
    }
}
