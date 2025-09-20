package com.example.oneuiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LanguageManager {
    
    private static final String PREF_NAME = "language_preferences";
    private static final String KEY_LANGUAGE = "selected_language";
    private static final String KEY_LANGUAGE_CHANGED = "language_changed";
    
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_ARABIC = "ar";
    
    private Context context;
    private SharedPreferences preferences;
    
    public LanguageManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void setLanguage(String languageCode) {
        if (languageCode == null || languageCode.isEmpty()) {
            languageCode = LANGUAGE_ENGLISH;
        }
        
        String currentLanguage = getCurrentLanguage();
        if (!currentLanguage.equals(languageCode)) {
            preferences.edit()
                    .putString(KEY_LANGUAGE, languageCode)
                    .putBoolean(KEY_LANGUAGE_CHANGED, true)
                    .apply();
            
            updateLocale(languageCode);
        }
    }
    
    public String getCurrentLanguage() {
        return preferences.getString(KEY_LANGUAGE, getSystemDefaultLanguage());
    }
    
    public void applyLanguage() {
        String savedLanguage = getCurrentLanguage();
        updateLocale(savedLanguage);
        markLanguageAsApplied();
    }
    
    private void updateLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }
        
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
    
    public boolean isRtlLanguage() {
        String currentLanguage = getCurrentLanguage();
        return LANGUAGE_ARABIC.equals(currentLanguage);
    }
    
    public boolean hasLanguageChanged() {
        return preferences.getBoolean(KEY_LANGUAGE_CHANGED, false);
    }
    
    private void markLanguageAsApplied() {
        preferences.edit()
                .putBoolean(KEY_LANGUAGE_CHANGED, false)
                .apply();
    }
    
    private String getSystemDefaultLanguage() {
        String systemLanguage = Locale.getDefault().getLanguage();
        if (LANGUAGE_ARABIC.equals(systemLanguage)) {
            return LANGUAGE_ARABIC;
        }
        return LANGUAGE_ENGLISH;
    }
    
    public String getLanguageDisplayName(String languageCode) {
        if (LANGUAGE_ARABIC.equals(languageCode)) {
            return context.getString(R.string.language_arabic);
        }
        return context.getString(R.string.language_english);
    }
    
    public boolean isLanguageSupported(String languageCode) {
        return LANGUAGE_ENGLISH.equals(languageCode) || LANGUAGE_ARABIC.equals(languageCode);
    }
    
    public void resetToSystemDefault() {
        String systemLanguage = getSystemDefaultLanguage();
        setLanguage(systemLanguage);
    }
    
    public Context createLanguageContext(Context baseContext, String languageCode) {
        if (languageCode == null || languageCode.isEmpty()) {
            return baseContext;
        }
        
        Locale locale = new Locale(languageCode);
        Configuration configuration = new Configuration(baseContext.getResources().getConfiguration());
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }
        
        return baseContext.createConfigurationContext(configuration);
    }
}
