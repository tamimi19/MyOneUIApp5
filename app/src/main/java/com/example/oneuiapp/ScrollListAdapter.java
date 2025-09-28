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

    private Context context;
    private List<ScrollListItem> items;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ScrollListItem item, int position);
    }

    public ScrollListAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public void setItems(List<ScrollListItem> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void addItem(ScrollListItem item) {
        if (item != null) {
            this.items.add(item);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void addItems(List<ScrollListItem> newItems) {
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

    public ScrollListItem getItem(int position) {
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
    public ScrollListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ScrollListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScrollListViewHolder holder, int position) {
        ScrollListItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ScrollListViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView iconImageView;
        private TextView titleTextView;
        private TextView descriptionTextView;
        private ImageView chevronImageView;

        public ScrollListViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.list_item_icon);
            titleTextView = itemView.findViewById(R.id.list_item_title);
            descriptionTextView = itemView.findViewById(R.id.list_item_description);
            chevronImageView = itemView.findViewById(R.id.list_item_chevron);

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

        public void bind(ScrollListItem item, int position) {
            if (item == null) {
                return;
            }

            titleTextView.setText(item.getTitle());
            
            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                descriptionTextView.setText(item.getDescription());
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }

            if (item.getIconResource() != 0) {
                iconImageView.setImageResource(item.getIconResource());
                iconImageView.setVisibility(View.VISIBLE);
            } else {
                iconImageView.setVisibility(View.GONE);
            }

            if (item.isShowChevron()) {
                chevronImageView.setVisibility(View.VISIBLE);
            } else {
                chevronImageView.setVisibility(View.GONE);
            }

            // تحديث الوصف البديل للعنصر بالكامل إلى عنوان العنصر:
            itemView.setContentDescription(item.getTitle());

            itemView.setEnabled(item.isEnabled());
            itemView.setAlpha(item.isEnabled() ? 1.0f : 0.6f);
        }
    }
}
