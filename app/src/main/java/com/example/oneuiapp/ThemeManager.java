package com.example.oneuiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    
    private static final String PREF_NAME = "theme_preferences";
    private static final String KEY_THEME = "selected_theme";
    private static final String KEY_THEME_CHANGED = "theme_changed";
    
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_SYSTEM = "system";
    
    private Context context;
    private SharedPreferences preferences;
    
    public ThemeManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void setTheme(String themeMode) {
        if (themeMode == null || themeMode.isEmpty()) {
            themeMode = THEME_SYSTEM;
        }
        
        String currentTheme = getCurrentTheme();
        if (!currentTheme.equals(themeMode)) {
            preferences.edit()
                    .putString(KEY_THEME, themeMode)
                    .putBoolean(KEY_THEME_CHANGED, true)
                    .apply();
            
            applyThemeMode(themeMode);
        }
    }
    
    public String getCurrentTheme() {
        return preferences.getString(KEY_THEME, THEME_SYSTEM);
    }
    
    public void applyTheme() {
        String savedTheme = getCurrentTheme();
        applyThemeMode(savedTheme);
        markThemeAsApplied();
    }
    
    private void applyThemeMode(String themeMode) {
        // OneUI Design library automatically handles theme switching
        // We only need to set the AppCompatDelegate mode
        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
    
    public boolean isDarkModeEnabled() {
        String currentTheme = getCurrentTheme();
        if (THEME_DARK.equals(currentTheme)) {
            return true;
        } else if (THEME_LIGHT.equals(currentTheme)) {
            return false;
        } else {
            // THEME_SYSTEM - check system setting
            int currentNightMode = context.getResources().getConfiguration().uiMode 
                    & Configuration.UI_MODE_NIGHT_MASK;
            return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
        }
    }
    
    public boolean hasThemeChanged() {
        return preferences.getBoolean(KEY_THEME_CHANGED, false);
    }
    
    private void markThemeAsApplied() {
        preferences.edit()
                .putBoolean(KEY_THEME_CHANGED, false)
                .apply();
    }
    
    public String getThemeDisplayName(String themeMode) {
        switch (themeMode) {
            case THEME_LIGHT:
                return context.getString(R.string.theme_light);
            case THEME_DARK:
                return context.getString(R.string.theme_dark);
            case THEME_SYSTEM:
            default:
                return context.getString(R.string.theme_system);
        }
    }
    
    public boolean isThemeSupported(String themeMode) {
        return THEME_LIGHT.equals(themeMode) || 
               THEME_DARK.equals(themeMode) || 
               THEME_SYSTEM.equals(themeMode);
    }
    
    public void resetToSystemDefault() {
        setTheme(THEME_SYSTEM);
    }
    
    /**
     * Get the next theme in rotation order (for testing purposes)
     */
    public String getNextTheme() {
        String currentTheme = getCurrentTheme();
        switch (currentTheme) {
            case THEME_LIGHT:
                return THEME_DARK;
            case THEME_DARK:
                return THEME_SYSTEM;
            case THEME_SYSTEM:
            default:
                return THEME_LIGHT;
        }
    }
    
    /**
     * Check if dark mode is currently active based on system state
     */
    public boolean isCurrentlyDarkMode() {
        int currentNightMode = context.getResources().getConfiguration().uiMode 
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }
    
    /**
     * Get theme mode ID for programmatic use
     */
    public int getThemeModeId(String themeMode) {
        switch (themeMode) {
            case THEME_LIGHT:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case THEME_DARK:
                return AppCompatDelegate.MODE_NIGHT_YES;
            case THEME_SYSTEM:
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }
}
