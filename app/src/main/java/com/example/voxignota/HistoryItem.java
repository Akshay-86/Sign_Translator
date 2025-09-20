package com.example.voxignota;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history")
public class HistoryItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String text;

    public long timestamp;
}
