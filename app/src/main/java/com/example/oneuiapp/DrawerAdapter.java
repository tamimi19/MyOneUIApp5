
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

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    private Context context;
    private List<DrawerItem> drawerItems;
    private OnDrawerItemClickListener itemClickListener;

    public interface OnDrawerItemClickListener {
        void onItemClick(DrawerItem item, int position);
    }

    public DrawerAdapter(Context context) {
        this.context = context;
        this.drawerItems = new ArrayList<>();
        initializeDrawerItems();
    }

    private void initializeDrawerItems() {
        drawerItems.clear();
        
        drawerItems.add(new DrawerItem(
            R.drawable.ic_oui_drawer,
            context.getString(R.string.home),
            DrawerItem.ITEM_TYPE_HOME
        ));
        
        drawerItems.add(new DrawerItem(
            R.drawable.ic_oui_list,
            context.getString(R.string.scroll_screen),
            DrawerItem.ITEM_TYPE_SCROLL_LIST
        ));
        
        drawerItems.add(new DrawerItem(
            R.drawable.ic_oui_settings_outline,
            context.getString(R.string.settings),
            DrawerItem.ITEM_TYPE_SETTINGS
        ));
        
        drawerItems.add(new DrawerItem(
            R.drawable.ic_oui_notification_outline,
            context.getString(R.string.notifications),
            DrawerItem.ITEM_TYPE_NOTIFICATIONS
        ));
    }

    public void setOnItemClickListener(OnDrawerItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void updateLanguage() {
        initializeDrawerItems();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DrawerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.drawer_item, parent, false);
        return new DrawerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerViewHolder holder, int position) {
        DrawerItem item = drawerItems.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return drawerItems.size();
    }

    public class DrawerViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView iconImageView;
        private TextView textView;
        private ImageView arrowImageView;

        public DrawerViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.drawer_item_icon);
            textView = itemView.findViewById(R.id.drawer_item_text);
            arrowImageView = itemView.findViewById(R.id.drawer_item_arrow);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                        itemClickListener.onItemClick(drawerItems.get(position), position);
                    }
                }
            });
        }

        public void bind(DrawerItem item, int position) {
            iconImageView.setImageResource(item.getIconResource());
            textView.setText(item.getTitle());
            
            if (item.hasArrow()) {
                arrowImageView.setVisibility(View.VISIBLE);
            } else {
                arrowImageView.setVisibility(View.GONE);
            }

            itemView.setContentDescription(
                context.getString(R.string.drawer_item_icon_description) + ": " + item.getTitle()
            );
        }
    }

    public static class DrawerItem {
        public static final int ITEM_TYPE_HOME = 1;
        public static final int ITEM_TYPE_SCROLL_LIST = 2;
        public static final int ITEM_TYPE_SETTINGS = 3;
        public static final int ITEM_TYPE_NOTIFICATIONS = 4;

        private int iconResource;
        private String title;
        private int itemType;
        private boolean showArrow;

        public DrawerItem(int iconResource, String title, int itemType) {
            this.iconResource = iconResource;
            this.title = title;
            this.itemType = itemType;
            this.showArrow = (itemType == ITEM_TYPE_SCROLL_LIST || itemType == ITEM_TYPE_SETTINGS);
        }

        public int getIconResource() {
            return iconResource;
        }

        public String getTitle() {
            return title;
        }

        public int getItemType() {
            return itemType;
        }

        public boolean hasArrow() {
            return showArrow;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
