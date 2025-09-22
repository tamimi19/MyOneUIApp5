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
import android.util.Log;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnDrawerItemClickListener {

    private static final String TAG = "MainActivity";
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
        try {
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
            
            Log.d(TAG, "MainActivity تم إنشاؤها بنجاح");
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في onCreate", e);
            CrashLogger.logError(TAG, "خطأ في إنشاء MainActivity", e);
            throw e; // إعادة إرسال الخطأ لتسجيله في نظام الأخطاء العام
        }
    }

    private void initViews() {
        try {
            drawerLayout = findViewById(R.id.drawer_layout);
            drawerContainer = findViewById(R.id.drawer_container);
            drawerRecyclerView = findViewById(R.id.drawer_recycler_view);
            collapsingToolbar = findViewById(R.id.collapsing_toolbar);
            toolbar = findViewById(R.id.toolbar);
            mainRecyclerView = findViewById(R.id.main_recycler_view);
            
            Log.d(TAG, "Views تم تهيئتها بنجاح");
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في تهيئة Views", e);
            CrashLogger.logError(TAG, "خطأ في تهيئة Views", e);
            throw e;
        }
    }

    private void setupToolbar() {
        try {
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
                    try {
                        toggleDrawer();
                    } catch (Exception e) {
                        Log.e(TAG, "خطأ في toggleDrawer", e);
                        CrashLogger.logError(TAG, "خطأ في تبديل Drawer", e);
                    }
                }
            });
            
            Log.d(TAG, "Toolbar تم إعداده بنجاح");
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إعداد Toolbar", e);
            CrashLogger.logError(TAG, "خطأ في إعداد Toolbar", e);
            throw e;
        }
    }

    private void setupDrawer() {
        try {
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
            
            Log.d(TAG, "Drawer تم إعداده بنجاح");
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إعداد Drawer", e);
            CrashLogger.logError(TAG, "خطأ في إعداد Drawer", e);
            throw e;
        }
    }

    private void setupCollapsingToolbar() {
        try {
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
                        try {
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
                        } catch (Exception e) {
                            Log.e(TAG, "خطأ في onOffsetChanged", e);
                            CrashLogger.logError(TAG, "خطأ في تغيير حالة AppBar", e);
                        }
                    }
                });
            }
            
            Log.d(TAG, "CollapsingToolbar تم إعداده بنجاح");
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إعداد CollapsingToolbar", e);
            CrashLogger.logError(TAG, "خطأ في إعداد CollapsingToolbar", e);
            throw e;
        }
    }

    private void updateToolbarState(boolean isCollapsed) {
        try {
            // Optional: Add custom behavior when toolbar collapses/expands
            // The title animation is handled automatically by SESL CollapsingToolbarLayout
            if (isCollapsed) {
                // Toolbar is collapsed - title is small and positioned at top
                Log.d(TAG, "Toolbar مطوي");
            } else {
                // Toolbar is expanded - title is large and centered
                Log.d(TAG, "Toolbar موسع");
            }
        } catch (Exception e) {
            Log.e(TAG, "خطأ في updateToolbarState", e);
            CrashLogger.logError(TAG, "خطأ في تحديث حالة Toolbar", e);
        }
    }

    private void setupRecyclerView() {
        try {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mainRecyclerView.setLayoutManager(layoutManager);
            mainRecyclerView.setHasFixedSize(true);
            
            // Initialize main adapter
            mainAdapter = new MainAdapter(this);
            mainAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(String item, int position) {
                    try {
                        showItemClickedMessage(item, position);
                    } catch (Exception e) {
                        Log.e(TAG, "خطأ في onItemClick", e);
                        CrashLogger.logError(TAG, "خطأ في النقر على العنصر", e);
                    }
                }
            });
            
            // Generate sample data for main screen
            mainAdapter.generateSampleData(20);
            mainRecyclerView.setAdapter(mainAdapter);
            
            Log.d(TAG, "RecyclerView تم إعداده بنجاح");
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إعداد RecyclerView", e);
            CrashLogger.logError(TAG, "خطأ في إعداد RecyclerView", e);
            throw e;
        }
    }

    private void showItemClickedMessage(String item, int position) {
        try {
            String message = getString(R.string.item_clicked_message, item, position + 1);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "خطأ في عرض رسالة النقر", e);
            CrashLogger.logError(TAG, "خطأ في عرض رسالة النقر على العنصر", e);
        }
    }

    private void toggleDrawer() {
        try {
            if (drawerLayout.isDrawerOpen(drawerContainer)) {
                drawerLayout.closeDrawer(drawerContainer);
            } else {
                drawerLayout.openDrawer(drawerContainer);
            }
        } catch (Exception e) {
            Log.e(TAG, "خطأ في تبديل Drawer", e);
            CrashLogger.logError(TAG, "خطأ في فتح/إغلاق Drawer", e);
        }
    }

    @Override
    public void onItemClick(DrawerAdapter.DrawerItem item, int position) {
        try {
            handleDrawerItemClick(item);
            drawerLayout.closeDrawer(drawerContainer);
        } catch (Exception e) {
            Log.e(TAG, "خطأ في النقر على عنصر Drawer", e);
            CrashLogger.logError(TAG, "خطأ في معالجة النقر على عنصر Drawer", e);
        }
    }

    private void handleDrawerItemClick(DrawerAdapter.DrawerItem item) {
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "خطأ في معالجة النقر على عنصر Drawer", e);
            CrashLogger.logError(TAG, "خطأ في تنفيذ إجراء عنصر Drawer", e);
            Toast.makeText(this, "حدث خطأ أثناء تنفيذ العملية", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotificationsMessage() {
        try {
            Toast.makeText(this, R.string.notifications_feature_coming_soon, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "خطأ في عرض رسالة الإشعارات", e);
            CrashLogger.logError(TAG, "خطأ في عرض رسالة الإشعارات", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawerLayout.isDrawerOpen(drawerContainer)) {
                drawerLayout.closeDrawer(drawerContainer);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            Log.e(TAG, "خطأ في onBackPressed", e);
            CrashLogger.logError(TAG, "خطأ في التعامل مع زر الرجوع", e);
            super.onBackPressed(); // تنفيذ الإجراء الافتراضي
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            
            // Update drawer content when returning to activity
            updateDrawerContent();
            
            // Handle theme or language changes
            handleConfigurationChanges();
            
            Log.d(TAG, "MainActivity تم استئنافها بنجاح");
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في onResume", e);
            CrashLogger.logError(TAG, "خطأ في استئناف MainActivity", e);
        }
    }

    private void updateDrawerContent() {
        try {
            if (drawerAdapter != null) {
                drawerAdapter.updateLanguage();
            }
        } catch (Exception e) {
            Log.e(TAG, "خطأ في تحديث محتوى Drawer", e);
            CrashLogger.logError(TAG, "خطأ في تحديث محتوى Drawer", e);
        }
    }

    private void handleConfigurationChanges() {
        try {
            if (themeManager.hasThemeChanged() || languageManager.hasLanguageChanged()) {
                // Recreate activity to apply changes
                recreate();
            }
        } catch (Exception e) {
            Log.e(TAG, "خطأ في معالجة تغييرات الإعدادات", e);
            CrashLogger.logError(TAG, "خطأ في معالجة تغييرات الإعدادات", e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == android.R.id.home) {
                toggleDrawer();
                return true;
            }
            return super.onOptionsItemSelected(item);
        } catch (Exception e) {
            Log.e(TAG, "خطأ في onOptionsItemSelected", e);
            CrashLogger.logError(TAG, "خطأ في معالجة اختيار العنصر", e);
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            // Clean up resources
            if (drawerAdapter != null) {
                drawerAdapter.setOnItemClickListener(null);
            }
            if (mainAdapter != null) {
                mainAdapter.setOnItemClickListener(null);
            }
            
            Log.d(TAG, "MainActivity تم تدميرها بنجاح");
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في onDestroy", e);
            CrashLogger.logError(TAG, "خطأ في تدمير MainActivity", e);
        } finally {
            super.onDestroy();
        }
    }
}
