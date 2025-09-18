package com.example.voxignota;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    List<HistoryItem> getAll();

    @Insert
    void insert(HistoryItem historyItem);
}
