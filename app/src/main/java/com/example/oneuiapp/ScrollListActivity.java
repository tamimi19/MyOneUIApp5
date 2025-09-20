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
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    private void setupCollapsingToolbar() {
        collapsingToolbar.setTitle(getString(R.string.scroll_screen));
        collapsingToolbar.setExtendedTitleEnabled(true);
        collapsingToolbar.seslEnableFadeToolbarTitle(true);
        
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
                updateTitlePosition(percentage);
            }
        });
    }

    private void updateTitlePosition(float percentage) {
        float expandedTitleSize = getResources().getDimension(R.dimen.expanded_title_size);
        float collapsedTitleSize = getResources().getDimension(R.dimen.collapsed_title_size);
        
        float currentSize = expandedTitleSize - ((expandedTitleSize - collapsedTitleSize) * percentage);
        collapsingToolbar.setExpandedTitleTextSize(currentSize);
        
        if (percentage < 0.5f) {
            collapsingToolbar.setExpandedTitleGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);
        } else {
            if (languageManager.isRtlLanguage()) {
                collapsingToolbar.setCollapsedTitleGravity(android.view.Gravity.TOP | android.view.Gravity.END);
            } else {
                collapsingToolbar.setCollapsedTitleGravity(android.view.Gravity.TOP | android.view.Gravity.START);
            }
        }
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
        
        for (int i = 1; i <= 200; i++) {
            ScrollListItem item = new ScrollListItem();
            item.setTitle(getString(R.string.list_item_title) + " " + i);
            item.setDescription(getString(R.string.list_item_description) + " " + i);
            item.setIconResource(getIconForItem(i));
            items.add(item);
        }
        
        adapter.setItems(items);
    }

    private int getIconForItem(int position) {
        int[] icons = {
            R.drawable.ic_item_1,
            R.drawable.ic_item_2,
            R.drawable.ic_item_3,
            R.drawable.ic_item_4,
            R.drawable.ic_item_5
        };
        return icons[position % icons.length];
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
        if (themeManager.hasThemeChanged() || languageManager.hasLanguageChanged()) {
            recreate();
        }
    }
        }
