package com.example.oneuiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerAdapter.OnDrawerItemClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CollapsingToolbarLayout collapsingToolbar;
    private MaterialToolbar toolbar;
    private RecyclerView mainRecyclerView;
    private DrawerAdapter drawerAdapter;
    private ThemeManager themeManager;
    private LanguageManager languageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        languageManager = new LanguageManager(this);
        themeManager = new ThemeManager(this);
        
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
        navigationView = findViewById(R.id.navigation_view);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        mainRecyclerView = findViewById(R.id.main_recycler_view);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });
    }

    private void setupDrawer() {
        drawerAdapter = new DrawerAdapter(this);
        drawerAdapter.setOnItemClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupCollapsingToolbar() {
        collapsingToolbar.setTitle(getString(R.string.app_name));
        
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
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Create sample data for main screen
        List<String> mainItems = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            mainItems.add(getString(R.string.main_item) + " " + i);
        }
        
        // Note: You would need to create a simple adapter for these items
        // MainAdapter adapter = new MainAdapter(mainItems);
        // mainRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation with programmatically created menu items
        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    @Override
    public void onItemClick(DrawerAdapter.DrawerItem item, int position) {
        switch (item.getItemType()) {
            case DrawerAdapter.DrawerItem.ITEM_TYPE_HOME:
                Toast.makeText(this, R.string.home, Toast.LENGTH_SHORT).show();
                break;
            case DrawerAdapter.DrawerItem.ITEM_TYPE_SCROLL_LIST:
                Intent scrollIntent = new Intent(this, ScrollListActivity.class);
                startActivity(scrollIntent);
                break;
            case DrawerAdapter.DrawerItem.ITEM_TYPE_SETTINGS:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case DrawerAdapter.DrawerItem.ITEM_TYPE_NOTIFICATIONS:
                Toast.makeText(this, R.string.notifications, Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(navigationView);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
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
