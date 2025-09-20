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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CollapsingToolbarLayout collapsingToolbar;
    private MaterialToolbar toolbar;
    private RecyclerView mainRecyclerView;
    private ThemeManager themeManager;
    private LanguageManager languageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // تطبيق إعدادات اللغة والثيم قبل إنشاء النشاط
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
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_oui_drawer);
        }
        
        // إعداد معالج الضغط على زر القائمة
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
        navigationView.setNavigationItemSelectedListener(this);
        
        // إعداد الدرج مع الأيقونات المناسبة
        navigationView.getMenu().clear();
        navigationView.getMenu().add(0, R.id.nav_home, 0, R.string.home)
                .setIcon(R.drawable.ic_home);
        navigationView.getMenu().add(0, R.id.nav_scroll_list, 0, R.string.scroll_screen)
                .setIcon(R.drawable.ic_list);
        navigationView.getMenu().add(0, R.id.nav_settings, 0, R.string.settings)
                .setIcon(R.drawable.ic_settings);
        navigationView.getMenu().add(0, R.id.nav_notifications, 0, R.string.notifications)
                .setIcon(R.drawable.ic_notifications);
    }

    private void setupCollapsingToolbar() {
        collapsingToolbar.setTitle(getString(R.string.app_name));
        
        // تمكين الميزات الخاصة بـ OneUI
        collapsingToolbar.setExtendedTitleEnabled(true);
        collapsingToolbar.seslEnableFadeToolbarTitle(true);
        
        // إعداد التمرير التفاعلي للعنوان
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
                
                // تحديث موضع ومقياس العنوان تدريجياً
                updateTitlePosition(percentage);
            }
        });
    }

    private void updateTitlePosition(float percentage) {
        // تطبيق تأثير التكبير والتصغير التدريجي للعنوان كما في One UI
        float expandedTitleSize = getResources().getDimension(R.dimen.expanded_title_size);
        float collapsedTitleSize = getResources().getDimension(R.dimen.collapsed_title_size);
        
        float currentSize = expandedTitleSize - ((expandedTitleSize - collapsedTitleSize) * percentage);
        collapsingToolbar.setExpandedTitleTextSize(currentSize);
        
        // تحديث موضع العنوان بناءً على نسبة التمرير
        if (percentage < 0.5f) {
            // في الوضع الموسع - العنوان في المنتصف
            collapsingToolbar.setExpandedTitleGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);
        } else {
            // في الوضع المنهار - العنوان في الجانب
            if (languageManager.isRtlLanguage()) {
                collapsingToolbar.setCollapsedTitleGravity(android.view.Gravity.TOP | android.view.Gravity.END);
            } else {
                collapsingToolbar.setCollapsedTitleGravity(android.view.Gravity.TOP | android.view.Gravity.START);
            }
        }
    }

    private void setupRecyclerView() {
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // إنشاء قائمة تجريبية للواجهة الرئيسية
        List<String> mainItems = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            mainItems.add(getString(R.string.main_item) + " " + i);
        }
        
        // يمكن استخدام محول بسيط هنا أو إنشاء محول مخصص
        // MainAdapter adapter = new MainAdapter(mainItems);
        // mainRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_home) {
            // البقاء في الشاشة الرئيسية
            Toast.makeText(this, R.string.home, Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_scroll_list) {
            // الانتقال إلى شاشة التمرير
            Intent intent = new Intent(this, ScrollListActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_settings) {
            // الانتقال إلى شاشة الإعدادات
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_notifications) {
            // عرض رسالة للإشعارات
            Toast.makeText(this, R.string.notifications, Toast.LENGTH_SHORT).show();
        }
        
        drawerLayout.closeDrawer(navigationView);
        return true;
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
        // إعادة تطبيق الإعدادات عند العودة للنشاط
        if (themeManager.hasThemeChanged() || languageManager.hasLanguageChanged()) {
            recreate();
        }
    }
                      }
