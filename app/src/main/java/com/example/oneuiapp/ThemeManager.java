package com.example.oneuiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "selected_theme";
    private static final String THEME_LIGHT = "light";
    private static final String THEME_DARK = "dark";
    private static final String THEME_SYSTEM = "system";

    private final Context context;
    private final SharedPreferences preferences;
    private boolean themeChanged = false;

    public ThemeManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Sets the application theme and applies it immediately
     * @param theme Theme identifier (light, dark, system)
     */
    public void setTheme(String theme) {
        String currentTheme = getCurrentTheme();
        if (!currentTheme.equals(theme)) {
            preferences.edit().putString(KEY_THEME, theme).apply();
            applyTheme();
            themeChanged = true;
        }
    }

    /**
     * Gets the currently selected theme
     * @return Current theme identifier
     */
    public String getCurrentTheme() {
        return preferences.getString(KEY_THEME, THEME_LIGHT);
    }

    /**
     * Applies the current theme setting to the application
     */
    public void applyTheme() {
        String theme = getCurrentTheme();
        
        switch (theme) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

    /**
     * Checks if the theme has been changed recently
     * @return True if theme was changed since last check
     */
    public boolean hasThemeChanged() {
        boolean changed = themeChanged;
        themeChanged = false;
        return changed;
    }

    /**
     * Checks if the current theme is dark mode
     * @return True if dark theme is active
     */
    public boolean isDarkTheme() {
        String currentTheme = getCurrentTheme();
        
        if (THEME_DARK.equals(currentTheme)) {
            return true;
        } else if (THEME_SYSTEM.equals(currentTheme)) {
            Configuration config = context.getResources().getConfiguration();
            return (config.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        }
        
        return false;
    }

    /**
     * Gets the theme resource ID for the current theme
     * @return Resource ID for the current theme
     */
    public int getThemeResourceId() {
        if (isDarkTheme()) {
            return R.style.OneUITheme_Dark;
        } else {
            return R.style.OneUITheme_Light;
        }
    }

    /**
     * Gets display name for the current theme
     * @return Localized display name for the theme
     */
    public String getThemeDisplayName() {
        String theme = getCurrentTheme();
        
        switch (theme) {
            case THEME_LIGHT:
                return context.getString(R.string.theme_light);
            case THEME_DARK:
                return context.getString(R.string.theme_dark);
            case THEME_SYSTEM:
                return context.getString(R.string.theme_system);
            default:
                return context.getString(R.string.theme_light);
        }
    }

    /**
     * Gets all available theme options
     * @return Array of theme identifiers
     */
    public String[] getAvailableThemes() {
        return new String[]{THEME_LIGHT, THEME_DARK, THEME_SYSTEM};
    }

    /**
     * Gets display names for all available themes
     * @return Array of localized theme names
     */
    public String[] getThemeDisplayNames() {
        return new String[]{
            context.getString(R.string.theme_light),
            context.getString(R.string.theme_dark),
            context.getString(R.string.theme_system)
        };
    }

    /**
     * Resets theme to default (light theme)
     */
    public void resetToDefault() {
        setTheme(THEME_LIGHT);
    }

    /**
     * Checks if theme follows system setting
     * @return True if theme follows system
     */
    public boolean isSystemTheme() {
        return THEME_SYSTEM.equals(getCurrentTheme());
    }

    /**
     * Static method to initialize theme on app start
     * @param context Application context
     */
    public static void initializeTheme(Context context) {
        ThemeManager themeManager = new ThemeManager(context);
        themeManager.applyTheme();
    }
}
