package com.example.voxignota;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Map<String, List<HistoryItem>> groupedItems;
    private final List<Object> items = new ArrayList<>();
    private final Map<String, Boolean> expandedState;

    public HistoryAdapter(Map<String, Boolean> expandedState) {
        this.expandedState = expandedState;
    }

    public void setItems(Map<String, List<HistoryItem>> groupedItems) {
        this.groupedItems = groupedItems;
        rebuildItems();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void rebuildItems() {
        items.clear();
        for (String date : groupedItems.keySet()) {
            items.add(date);
            if (Boolean.TRUE.equals(expandedState.getOrDefault(date, false))) {
                items.addAll(Objects.requireNonNull(groupedItems.get(date)));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_header_item, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            String date = (String) items.get(position);
            headerViewHolder.headerTextView.setText(date);
            boolean isExpanded = Boolean.TRUE.equals(expandedState.getOrDefault(date, false));
            headerViewHolder.arrow.setImageResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);

            holder.itemView.setOnClickListener(v -> {
                expandedState.put(date, !isExpanded);
                rebuildItems();
            });
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            HistoryItem historyItem = (HistoryItem) items.get(position);
            itemViewHolder.textTextView.setText(historyItem.text);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            itemViewHolder.timestampTextView.setText(sdf.format(new Date(historyItem.timestamp)));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTextView;
        ImageView arrow;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.headerTextView);
            arrow = itemView.findViewById(R.id.arrow);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textTextView;
        TextView timestampTextView;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textTextView = itemView.findViewById(R.id.textTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}
