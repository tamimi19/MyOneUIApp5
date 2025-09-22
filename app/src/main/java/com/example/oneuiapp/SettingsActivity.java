package com.example.oneuiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private MaterialToolbar toolbar;
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
        setContentView(R.layout.activity_settings);
        
        initViews();
        setupToolbar();
        setupCollapsingToolbar();
        
        // Add settings fragment if not already added
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }
    }

    private void initViews() {
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
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
        // Set title for settings screen
        collapsingToolbar.setTitle(getString(R.string.settings));
        
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
            toolbar.setContentDescription(getString(R.string.toolbar_collapsed));
        } else {
            toolbar.setContentDescription(getString(R.string.toolbar_expanded));
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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        
        private ThemeManager themeManager;
        private LanguageManager languageManager;
        
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Initialize preferences with custom name
            getPreferenceManager().setSharedPreferencesName("app_preferences");
            
            // Initialize managers
            themeManager = new ThemeManager(requireContext());
            languageManager = new LanguageManager(requireContext());
            
            // Create preferences screen programmatically
            createPreferencesScreen();
        }
        
        private void createPreferencesScreen() {
            PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(requireContext());
            setPreferenceScreen(preferenceScreen);
            
            // Create Appearance category
            createAppearanceCategory(preferenceScreen);
            
            // Create General category
            createGeneralCategory(preferenceScreen);
        }
        
        private void createAppearanceCategory(PreferenceScreen preferenceScreen) {
            // Appearance category
            PreferenceCategory appearanceCategory = new PreferenceCategory(requireContext());
            appearanceCategory.setTitle(getString(R.string.category_appearance));
            preferenceScreen.addPreference(appearanceCategory);
            
            // Language preference
            ListPreference languagePreference = createLanguagePreference();
            appearanceCategory.addPreference(languagePreference);
            
            // Theme preference
            ListPreference themePreference = createThemePreference();
            appearanceCategory.addPreference(themePreference);
        }
        
        private void createGeneralCategory(PreferenceScreen preferenceScreen) {
            // General category
            PreferenceCategory generalCategory = new PreferenceCategory(requireContext());
            generalCategory.setTitle(getString(R.string.category_general));
            preferenceScreen.addPreference(generalCategory);
            
            // Notifications preference
            Preference notificationsPreference = createNotificationsPreference();
            generalCategory.addPreference(notificationsPreference);
        }
        
        private ListPreference createLanguagePreference() {
            ListPreference languagePreference = new ListPreference(requireContext());
            languagePreference.setKey("language");
            languagePreference.setTitle(getString(R.string.language));
            languagePreference.setSummary(getString(R.string.language_summary));
            languagePreference.setDialogTitle(getString(R.string.language));
            
            // Set entries and values
            languagePreference.setEntries(new String[]{
                getString(R.string.language_english),
                getString(R.string.language_arabic)
            });
            languagePreference.setEntryValues(new String[]{
                LanguageManager.LANGUAGE_ENGLISH,
                LanguageManager.LANGUAGE_ARABIC
            });
            
            // Set current value
            languagePreference.setValue(languageManager.getCurrentLanguage());
            
            // Set summary based on current selection
            updateLanguageSummary(languagePreference);
            
            // Set change listener
            languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String selectedLanguage = (String) newValue;
                    
                    // Apply language change
                    languageManager.setLanguage(selectedLanguage);
                    
                    // Update summary
                    updateLanguageSummary((ListPreference) preference);
                    
                    // Recreate activity to apply changes
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                    
                    return true;
                }
            });
            
            return languagePreference;
        }
        
        private ListPreference createThemePreference() {
            ListPreference themePreference = new ListPreference(requireContext());
            themePreference.setKey("theme");
            themePreference.setTitle(getString(R.string.theme));
            themePreference.setSummary(getString(R.string.theme_summary));
            themePreference.setDialogTitle(getString(R.string.theme));
            
            // Set entries and values
            themePreference.setEntries(new String[]{
                getString(R.string.theme_light),
                getString(R.string.theme_dark),
                getString(R.string.theme_system)
            });
            themePreference.setEntryValues(new String[]{
                ThemeManager.THEME_LIGHT,
                ThemeManager.THEME_DARK,
                ThemeManager.THEME_SYSTEM
            });
            
            // Set current value
            themePreference.setValue(themeManager.getCurrentTheme());
            
            // Set summary based on current selection
            updateThemeSummary(themePreference);
            
            // Set change listener
            themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String selectedTheme = (String) newValue;
                    
                    // Apply theme change
                    themeManager.setTheme(selectedTheme);
                    
                    // Update summary
                    updateThemeSummary((ListPreference) preference);
                    
                    // Recreate activity to apply changes
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                    
                    return true;
                }
            });
            
            return themePreference;
        }
        
        private Preference createNotificationsPreference() {
            Preference notificationsPreference = new Preference(requireContext());
            notificationsPreference.setKey("notifications");
            notificationsPreference.setTitle(getString(R.string.notifications));
            notificationsPreference.setSummary(getString(R.string.notifications_summary));
            
            // Set icon if available
            notificationsPreference.setIcon(R.drawable.ic_oui_notification_outline);
            
            // Set click listener
            notificationsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    handleNotificationsClick();
                    return true;
                }
            });
            
            return notificationsPreference;
        }
        
        private void updateLanguageSummary(ListPreference languagePreference) {
            String currentLanguage = languageManager.getCurrentLanguage();
            String displayName = languageManager.getLanguageDisplayName(currentLanguage);
            languagePreference.setSummary(getString(R.string.current_selection, displayName));
        }
        
        private void updateThemeSummary(ListPreference themePreference) {
            String currentTheme = themeManager.getCurrentTheme();
            String displayName = themeManager.getThemeDisplayName(currentTheme);
            themePreference.setSummary(getString(R.string.current_selection, displayName));
        }
        
        private void handleNotificationsClick() {
            // Show message about notifications feature
            Toast.makeText(getContext(), R.string.notifications_feature_coming_soon, Toast.LENGTH_LONG).show();
            
            // TODO: Open notification settings when feature is implemented
            // Intent intent = new Intent(getContext(), NotificationsActivity.class);
            // startActivity(intent);
        }
    }
}
