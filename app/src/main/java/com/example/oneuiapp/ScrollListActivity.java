package com.example.oneuiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import android.os.Bundle;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class ScrollListActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private ScrollListAdapter adapter;
    private ThemeManager themeManager;
    private LanguageManager languageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        languageManager = new LanguageManager(this);
        themeManager = new ThemeManager(this);
        
        languageManager.applyLanguage();
        themeManager.applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_list);
        
        initViews();
        setupToolbar();
        setupCollapsingToolbar();
        setupRecyclerView();
        generateListItems();
    }

    private void initViews() {
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.scroll_recycler_view);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupCollapsingToolbar() {
        collapsingToolbar.setTitle(getString(R.string.scroll_screen));
        
        // Enable OneUI specific features using available SESL methods
        collapsingToolbar.seslEnableFadeToolbarTitle(true);
        
        // Monitor collapse state for any additional UI adjustments
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // Optional: Add any custom behavior based on collapse state
                // The title animation is handled automatically by CollapsingToolbarLayout
            }
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        
        adapter = new ScrollListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void generateListItems() {
        List<ScrollListItem> items = new ArrayList<>();
        
        // Create 200 list items as required
        for (int i = 1; i <= 200; i++) {
            ScrollListItem item = new ScrollListItem();
            item.setTitle(getString(R.string.list_item_title) + " " + i);
            item.setDescription(getString(R.string.list_item_description) + " " + i);
            item.setIconResource(getIconForItem(i));
            item.setShowChevron(true);
            item.setEnabled(true);
            items.add(item);
        }
        
        adapter.setItems(items);
    }

    private int getIconForItem(int position) {
        // Use a simple pattern for icons since we don't have the specific icon resources
        // This method can be updated when the actual icons are available
        return android.R.drawable.ic_menu_info_details; // Default Android icon as placeholder
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recreate activity if theme or language changed
        if (themeManager.hasThemeChanged() || languageManager.hasLanguageChanged()) {
            recreate();
        }
    }
}
