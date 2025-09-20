package com.example.oneuiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    private final Context context;
    private final List<DrawerItem> drawerItems;
    private OnDrawerItemClickListener clickListener;

    public interface OnDrawerItemClickListener {
        void onDrawerItemClick(DrawerItem item, int position);
    }

    public DrawerAdapter(Context context, List<DrawerItem> drawerItems) {
        this.context = context;
        this.drawerItems = drawerItems;
    }

    public void setOnDrawerItemClickListener(OnDrawerItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public DrawerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drawer_item, parent, false);
        return new DrawerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerViewHolder holder, int position) {
        DrawerItem item = drawerItems.get(position);
        
        holder.iconImageView.setImageResource(item.getIconResource());
        holder.textView.setText(item.getTitle());
        
        if (item.hasArrow()) {
            holder.arrowImageView.setVisibility(View.VISIBLE);
        } else {
            holder.arrowImageView.setVisibility(View.GONE);
        }
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        clickListener.onDrawerItemClick(item, adapterPosition);
                    }
                }
            }
        });

        holder.itemView.setSelected(item.isSelected());
    }

    @Override
    public int getItemCount() {
        return drawerItems != null ? drawerItems.size() : 0;
    }

    public void updateSelectedItem(int newSelectedPosition) {
        for (int i = 0; i < drawerItems.size(); i++) {
            drawerItems.get(i).setSelected(i == newSelectedPosition);
        }
        notifyDataSetChanged();
    }

    public static class DrawerViewHolder extends RecyclerView.ViewHolder {
        
        final ImageView iconImageView;
        final TextView textView;
        final ImageView arrowImageView;

        public DrawerViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.drawer_item_icon);
            textView = itemView.findViewById(R.id.drawer_item_text);
            arrowImageView = itemView.findViewById(R.id.drawer_item_arrow);
        }
    }

    public static class DrawerItem {
        private final String title;
        private final int iconResource;
        private final int id;
        private boolean selected;
        private boolean hasArrow;

        public DrawerItem(int id, String title, int iconResource) {
            this.id = id;
            this.title = title;
            this.iconResource = iconResource;
            this.selected = false;
            this.hasArrow = false;
        }

        public DrawerItem(int id, String title, int iconResource, boolean hasArrow) {
            this.id = id;
            this.title = title;
            this.iconResource = iconResource;
            this.selected = false;
            this.hasArrow = hasArrow;
        }

        public String getTitle() {
            return title;
        }

        public int getIconResource() {
            return iconResource;
        }

        public int getId() {
            return id;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean hasArrow() {
            return hasArrow;
        }

        public void setHasArrow(boolean hasArrow) {
            this.hasArrow = hasArrow;
        }
    }
      }
