package com.example.oneuiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private Context context;
    private List<String> items;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String item, int position);
    }

    public MainAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public MainAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public void setItems(List<String> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void addItem(String item) {
        if (item != null) {
            this.items.add(item);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void addItems(List<String> newItems) {
        if (newItems != null && !newItems.isEmpty()) {
            int startPosition = this.items.size();
            this.items.addAll(newItems);
            notifyItemRangeInserted(startPosition, newItems.size());
        }
    }

    public void clearItems() {
        int itemCount = this.items.size();
        this.items.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public String getItem(int position) {
        if (position >= 0 && position < items.size()) {
            return items.get(position);
        }
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.main_item, parent, false);
        return new MainViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        String item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        private ImageView iconImageView;
        private TextView titleTextView;
        private TextView descriptionTextView;
        private ImageView chevronImageView;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.main_item_icon);
            titleTextView = itemView.findViewById(R.id.main_item_title);
            descriptionTextView = itemView.findViewById(R.id.main_item_description);
            chevronImageView = itemView.findViewById(R.id.main_item_chevron);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                        itemClickListener.onItemClick(items.get(position), position);
                    }
                }
            });
        }

        public void bind(String item, int position) {
            if (item == null) {
                return;
            }

            titleTextView.setText(item);

            // Set description based on position
            String description = context.getString(R.string.main_item_description) + " " + (position + 1);
            descriptionTextView.setText(description);
            descriptionTextView.setVisibility(View.VISIBLE);

            // Set icon based on position pattern
            int iconResource = getIconForPosition(position);
            if (iconResource != 0) {
                iconImageView.setImageResource(iconResource);
                iconImageView.setVisibility(View.VISIBLE);
            } else {
                iconImageView.setVisibility(View.GONE);
            }

            // Always show chevron for main items
            chevronImageView.setVisibility(View.VISIBLE);

            // Set content description for accessibility
            itemView.setContentDescription(
                context.getString(R.string.main_item) + ": " + item
            );

            // Set click ripple effect
            itemView.setEnabled(true);
            itemView.setAlpha(1.0f);
        }

        private int getIconForPosition(int position) {
            // Use different icons based on position pattern
            // These can be updated when actual OneUI icons are available
            switch (position % 6) {
                case 0:
                    return android.R.drawable.ic_menu_info_details;
                case 1:
                    return android.R.drawable.ic_menu_agenda;
                case 2:
                    return android.R.drawable.ic_menu_call;
                case 3:
                    return android.R.drawable.ic_menu_camera;
                case 4:
                    return android.R.drawable.ic_menu_gallery;
                case 5:
                    return android.R.drawable.ic_menu_manage;
                default:
                    return android.R.drawable.ic_menu_info_details;
            }
        }
    }

    // Helper methods for data management
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getSize() {
        return items.size();
    }

    public List<String> getAllItems() {
        return new ArrayList<>(items);
    }

    public void updateItem(int position, String newItem) {
        if (position >= 0 && position < items.size() && newItem != null) {
            items.set(position, newItem);
            notifyItemChanged(position);
        }
    }

    public boolean contains(String item) {
        return items.contains(item);
    }

    public int indexOf(String item) {
        return items.indexOf(item);
    }

    // Method to generate sample data
    public void generateSampleData(int count) {
        List<String> sampleItems = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            sampleItems.add(context.getString(R.string.main_item) + " " + i);
        }
        setItems(sampleItems);
    }

    // Method to filter items (for search functionality if needed later)
    public void filterItems(String query) {
        // This method can be implemented later for search functionality
        // For now, it's a placeholder for future enhancements
    }
}
