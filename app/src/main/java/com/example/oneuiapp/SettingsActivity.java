package com.example.oneuiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

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
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    private void setupCollapsingToolbar() {
        collapsingToolbar.setTitle(getString(R.string.settings));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        
        private ThemeManager themeManager;
        private LanguageManager languageManager;
        
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            
            themeManager = new ThemeManager(requireContext());
            languageManager = new LanguageManager(requireContext());
            
            setupLanguagePreference();
            setupThemePreference();
            setupNotificationsPreference();
        }
        
        private void setupLanguagePreference() {
            ListPreference languagePreference = findPreference("language");
            if (languagePreference != null) {
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
                
                String currentLanguage = languageManager.getCurrentLanguage();
                languagePreference.setValue(currentLanguage);
                updateLanguageSummary(languagePreference, currentLanguage);
            }
        }
        
        private void setupThemePreference() {
            ListPreference themePreference = findPreference("theme");
            if (themePreference != null) {
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
                
                String currentTheme = themeManager.getCurrentTheme();
                themePreference.setValue(currentTheme);
                updateThemeSummary(themePreference, currentTheme);
            }
        }
        
        private void setupNotificationsPreference() {
            Preference notificationsPreference = findPreference("notifications");
            if (notificationsPreference != null) {
                notificationsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Toast.makeText(getContext(), R.string.notifications_settings, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        }
        
        private void updateLanguageSummary(ListPreference preference, String value) {
            if ("ar".equals(value)) {
                preference.setSummary(getString(R.string.language_arabic));
            } else {
                preference.setSummary(getString(R.string.language_english));
            }
        }
        
        private void updateThemeSummary(ListPreference preference, String value) {
            if ("dark".equals(value)) {
                preference.setSummary(getString(R.string.theme_dark));
            } else {
                preference.setSummary(getString(R.string.theme_light));
            }
        }
    }
}
