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

public class ScrollListAdapter extends RecyclerView.Adapter<ScrollListAdapter.ScrollListViewHolder> {

    private final Context context;
    private List<ScrollListItem> items;
    private OnScrollListItemClickListener clickListener;

    public interface OnScrollListItemClickListener {
        void onScrollListItemClick(ScrollListItem item, int position);
    }

    public ScrollListAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public void setOnScrollListItemClickListener(OnScrollListItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setItems(List<ScrollListItem> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addItem(ScrollListItem item) {
        if (item != null) {
            items.add(item);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearItems() {
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public ScrollListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scroll_list_item, parent, false);
        return new ScrollListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScrollListViewHolder holder, int position) {
        ScrollListItem item = items.get(position);
        
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());
        
        if (item.getIconResource() != 0) {
            holder.iconImageView.setImageResource(item.getIconResource());
            holder.iconImageView.setVisibility(View.VISIBLE);
        } else {
            holder.iconImageView.setVisibility(View.GONE);
        }
        
        if (item.showChevron()) {
            holder.chevronImageView.setVisibility(View.VISIBLE);
        } else {
            holder.chevronImageView.setVisibility(View.GONE);
        }
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        clickListener.onScrollListItemClick(item, adapterPosition);
                    }
                }
            }
        });

        holder.itemView.setSelected(item.isSelected());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ScrollListItem getItem(int position) {
        if (position >= 0 && position < items.size()) {
            return items.get(position);
        }
        return null;
    }

    public List<ScrollListItem> getAllItems() {
        return new ArrayList<>(items);
    }

    public static class ScrollListViewHolder extends RecyclerView.ViewHolder {
        
        final ImageView iconImageView;
        final TextView titleTextView;
        final TextView descriptionTextView;
        final ImageView chevronImageView;

        public ScrollListViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.scroll_item_icon);
            titleTextView = itemView.findViewById(R.id.scroll_item_title);
            descriptionTextView = itemView.findViewById(R.id.scroll_item_description);
            chevronImageView = itemView.findViewById(R.id.scroll_item_chevron);
        }
    }
}

class ScrollListItem {
    private String title;
    private String description;
    private int iconResource;
    private boolean selected;
    private boolean showChevron;

    public ScrollListItem() {
        this.selected = false;
        this.showChevron = true;
    }

    public ScrollListItem(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    public ScrollListItem(String title, String description, int iconResource) {
        this(title, description);
        this.iconResource = iconResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean showChevron() {
        return showChevron;
    }

    public void setShowChevron(boolean showChevron) {
        this.showChevron = showChevron;
    }
          }
