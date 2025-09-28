package com.example.oneuiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class ScrollListActivity extends AppCompatActivity implements ScrollListAdapter.OnItemClickListener {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ScrollListAdapter adapter;
    private ThemeManager themeManager;
    private LanguageManager languageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize managers before calling super.onCreate()
        languageManager = new LanguageManager(this);
        themeManager = new ThemeManager(this);
        
        // Apply language and theme settings
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
        recyclerView = findViewById(R.id.recycler_view_list);
        
        // Check if required views are found
        if (toolbar == null) {
            throw new RuntimeException("Toolbar not found in layout. Make sure R.id.toolbar exists in activity_scroll_list.xml");
        }
        if (recyclerView == null) {
            throw new RuntimeException("RecyclerView not found in layout. Make sure R.id.recycler_view_list exists in activity_scroll_list.xml");
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Let CollapsingToolbarLayout handle title
        }
    }

    private void setupCollapsingToolbar() {
        // Only proceed if CollapsingToolbarLayout is available
        if (collapsingToolbar == null) {
            // Fallback: Use regular toolbar title if CollapsingToolbarLayout is not available
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getString(R.string.scroll_screen));
            }
            return;
        }
        
        // Set title for the scroll list screen
        collapsingToolbar.setTitle(getString(R.string.scroll_screen));
        
        // Enable OneUI specific features using available SESL methods
        collapsingToolbar.seslEnableFadeToolbarTitle(true);
        
        // Monitor collapse state for additional UI adjustments if needed
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                private boolean isCollapsed = false;
                
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    // Calculate collapse ratio
                    int maxScroll = appBarLayout.getTotalScrollRange();
                    if (maxScroll == 0) return;
                    
                    float percentage = Math.abs(verticalOffset) / (float) maxScroll;
                    
                    // Determine if toolbar is collapsed
                    boolean nowCollapsed = percentage >= 0.9f;
                    if (isCollapsed != nowCollapsed) {
                        isCollapsed = nowCollapsed;
                        updateToolbarState(isCollapsed);
                    }
                }
            });
        }
    }

    private void updateToolbarState(boolean isCollapsed) {
        // Optional: Add custom behavior when toolbar collapses/expands
        // The title animation is handled automatically by SESL CollapsingToolbarLayout
        if (isCollapsed) {
            // Toolbar is collapsed - title is small and positioned at top
            toolbar.setContentDescription(getString(R.string.toolbar_collapsed));
        } else {
            // Toolbar is expanded - title is large and centered
            toolbar.setContentDescription(getString(R.string.toolbar_expanded));
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        
        // Initialize adapter
        adapter = new ScrollListAdapter(this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        
        // Add scroll listener for performance optimization
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // Optimize performance during scrolling if needed
            }
        });
    }

    private void generateListItems() {
        List<ScrollListItem> items = new ArrayList<>();
        
        // Create 200 list items as required
        for (int i = 1; i <= 200; i++) {
            ScrollListItem item = new ScrollListItem();
            item.setTitle(getString(R.string.scroll_item_title, i));
            item.setDescription(getString(R.string.scroll_item_description, i));
            item.setIconResource(getIconForItem(i));
            item.setShowChevron(true);
            item.setEnabled(true);
            items.add(item);
        }
        
        adapter.setItems(items);
    }

    private int getIconForItem(int position) {
        // Use a varied pattern for icons to make the list more interesting
        switch (position % 8) {
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
            case 6:
                return android.R.drawable.ic_menu_edit;
            case 7:
                return android.R.drawable.ic_menu_search;
            default:
                return android.R.drawable.ic_menu_info_details;
        }
    }

    @Override
    public void onItemClick(ScrollListItem item, int position) {
        if (item != null) {
            String message = getString(R.string.scroll_item_clicked, item.getTitle(), position + 1);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
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
        
        // Handle configuration changes
        handleConfigurationChanges();
    }

    private void handleConfigurationChanges() {
        if (themeManager.hasThemeChanged() || languageManager.hasLanguageChanged()) {
            // Recreate activity to apply changes
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        // Clean up resources
        if (adapter != null) {
            adapter.setOnItemClickListener(null);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // Provide smooth back navigation
        super.onBackPressed();
        // Optional: Add custom back animation if needed
    }
}
