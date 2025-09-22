package com.example.oneuiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private MaterialToolbar toolbar;
    private ThemeManager themeManager;
    private LanguageManager languageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        languageManager = new LanguageManager(this);
        themeManager = new ThemeManager(this);
        
        languageManager.applyLanguage();
        themeManager.applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initViews();
        setupToolbar();
        setupCollapsingToolbar();
        
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
        }
    }

    private void setupCollapsingToolbar() {
        collapsingToolbar.setTitle(getString(R.string.settings));
        
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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        
        private ThemeManager themeManager;
        private LanguageManager languageManager;
        
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Create preferences programmatically since XML file may not exist
            getPreferenceManager().setSharedPreferencesName("app_preferences");
            
            themeManager = new ThemeManager(requireContext());
            languageManager = new LanguageManager(requireContext());
            
            createPreferencesScreen();
        }
        
        private void createPreferencesScreen() {
            // Create language preference
            ListPreference languagePreference = new ListPreference(requireContext());
            languagePreference.setKey("language");
            languagePreference.setTitle(getString(R.string.language));
            languagePreference.setSummary(getString(R.string.language_summary));
            languagePreference.setEntries(new String[]{
                getString(R.string.language_english),
                getString(R.string.language_arabic)
            });
            languagePreference.setEntryValues(new String[]{"en", "ar"});
            languagePreference.setValue(languageManager.getCurrentLanguage());
            languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String selectedLanguage = (String) newValue;
                    languageManager.setLanguage(selectedLanguage);
                    
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                    return true;
                }
            });
            getPreferenceScreen().addPreference(languagePreference);
            
            // Create theme preference
            ListPreference themePreference = new ListPreference(requireContext());
            themePreference.setKey("theme");
            themePreference.setTitle(getString(R.string.theme));
            themePreference.setSummary(getString(R.string.theme_summary));
            themePreference.setEntries(new String[]{
                getString(R.string.theme_light),
                getString(R.string.theme_dark)
            });
            themePreference.setEntryValues(new String[]{"light", "dark"});
            themePreference.setValue(themeManager.getCurrentTheme());
            themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String selectedTheme = (String) newValue;
                    themeManager.setTheme(selectedTheme);
                    
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                    return true;
                }
            });
            getPreferenceScreen().addPreference(themePreference);
            
            // Create notifications preference
            Preference notificationsPreference = new Preference(requireContext());
            notificationsPreference.setKey("notifications");
            notificationsPreference.setTitle(getString(R.string.notifications));
            notificationsPreference.setSummary(getString(R.string.notifications_summary));
            notificationsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getContext(), R.string.notifications_settings, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            getPreferenceScreen().addPreference(notificationsPreference);
        }
    }
}
