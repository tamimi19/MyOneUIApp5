package com.example.oneuiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnDrawerItemClickListener {

    private DrawerLayout drawerLayout;
    private LinearLayout drawerContainer;
    private RecyclerView drawerRecyclerView;
    private CollapsingToolbarLayout collapsingToolbar;
    private MaterialToolbar toolbar;
    private RecyclerView mainRecyclerView;
    private DrawerAdapter drawerAdapter;
    private MainAdapter mainAdapter;
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
        setContentView(R.layout.activity_main);
        
        initViews();
        setupToolbar();
        setupDrawer();
        setupRecyclerView();
        setupCollapsingToolbar();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerContainer = findViewById(R.id.drawer_container);
        drawerRecyclerView = findViewById(R.id.drawer_recycler_view);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        mainRecyclerView = findViewById(R.id.main_recycler_view);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Let CollapsingToolbarLayout handle title
        }
        
        // Set navigation click listener for drawer toggle
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });
    }

    private void setupDrawer() {
        // Configure drawer layout
        drawerLayout.setDrawerElevation(getResources().getDimension(R.dimen.drawer_elevation));
        
        // Setup RecyclerView for drawer
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        drawerRecyclerView.setLayoutManager(layoutManager);
        drawerRecyclerView.setHasFixedSize(true);
        
        // Initialize drawer adapter
        drawerAdapter = new DrawerAdapter(this);
        drawerAdapter.setOnItemClickListener(this);
        drawerRecyclerView.setAdapter(drawerAdapter);
    }

    private void setupCollapsingToolbar() {
        // Set title for collapsing toolbar
        collapsingToolbar.setTitle(getString(R.string.app_name));
        
        // Enable OneUI specific features
        collapsingToolbar.seslEnableFadeToolbarTitle(true);
        
        // Configure extended title behavior (OneUI feature)
        // The extendedTitleEnabled attribute in XML handles the automatic behavior
        
        // Monitor collapse state for additional UI adjustments if needed
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                private boolean isCollapsed = false;
                
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    // Calculate collapse ratio
                    int maxScroll = appBarLayout.getTotalScrollRange();
                    float percentage = Math.abs(verticalOffset) / (float) maxScroll;
                    
                    // Determine if toolbar is collapsed
                    boolean nowCollapsed = percentage >= 0.9f;
                    if (isCollapsed != nowCollapsed) {
                        isCollapsed = nowCollapsed;
                        // Optional: Perform additional UI updates based on collapse state
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
        } else {
            // Toolbar is expanded - title is large and centered
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mainRecyclerView.setLayoutManager(layoutManager);
        mainRecyclerView.setHasFixedSize(true);
        
        // Initialize main adapter
        mainAdapter = new MainAdapter(this);
        mainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item, int position) {
                showItemClickedMessage(item, position);
            }
        });
        
        // Generate sample data for main screen
        mainAdapter.generateSampleData(20);
        mainRecyclerView.setAdapter(mainAdapter);
    }

    private void showItemClickedMessage(String item, int position) {
        String message = getString(R.string.item_clicked_message, item, position + 1);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(drawerContainer)) {
            drawerLayout.closeDrawer(drawerContainer);
        } else {
            drawerLayout.openDrawer(drawerContainer);
        }
    }

    @Override
    public void onItemClick(DrawerAdapter.DrawerItem item, int position) {
        handleDrawerItemClick(item);
        drawerLayout.closeDrawer(drawerContainer);
    }

    private void handleDrawerItemClick(DrawerAdapter.DrawerItem item) {
        switch (item.getItemType()) {
            case DrawerAdapter.DrawerItem.ITEM_TYPE_HOME:
                // Already on home screen
                Toast.makeText(this, R.string.already_on_home, Toast.LENGTH_SHORT).show();
                break;
                
            case DrawerAdapter.DrawerItem.ITEM_TYPE_SCROLL_LIST:
                startActivity(new Intent(this, ScrollListActivity.class));
                break;
                
            case DrawerAdapter.DrawerItem.ITEM_TYPE_SETTINGS:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
                
            case DrawerAdapter.DrawerItem.ITEM_TYPE_NOTIFICATIONS:
                showNotificationsMessage();
                break;
                
            default:
                Toast.makeText(this, R.string.unknown_option, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showNotificationsMessage() {
        Toast.makeText(this, R.string.notifications_feature_coming_soon, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerContainer)) {
            drawerLayout.closeDrawer(drawerContainer);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Update drawer content when returning to activity
        updateDrawerContent();
        
        // Handle theme or language changes
        handleConfigurationChanges();
    }

    private void updateDrawerContent() {
        if (drawerAdapter != null) {
            drawerAdapter.updateLanguage();
        }
    }

    private void handleConfigurationChanges() {
        if (themeManager.hasThemeChanged() || languageManager.hasLanguageChanged()) {
            // Recreate activity to apply changes
            recreate();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            toggleDrawer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        // Clean up resources
        if (drawerAdapter != null) {
            drawerAdapter.setOnItemClickListener(null);
        }
        if (mainAdapter != null) {
            mainAdapter.setOnItemClickListener(null);
        }
        super.onDestroy();
    }
    }
