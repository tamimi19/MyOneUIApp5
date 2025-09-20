package com.example.oneuiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LanguageManager {

    private static final String PREFS_NAME = "language_prefs";
    private static final String KEY_LANGUAGE = "selected_language";
    private static final String LANGUAGE_ENGLISH = "en";
    private static final String LANGUAGE_ARABIC = "ar";

    private final Context context;
    private final SharedPreferences preferences;
    private boolean languageChanged = false;

    public LanguageManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Sets the application language and applies it immediately
     * @param language Language code (en, ar)
     */
    public void setLanguage(String language) {
        String currentLanguage = getCurrentLanguage();
        if (!currentLanguage.equals(language)) {
            preferences.edit().putString(KEY_LANGUAGE, language).apply();
            applyLanguage();
            languageChanged = true;
        }
    }

    /**
     * Gets the currently selected language
     * @return Current language code
     */
    public String getCurrentLanguage() {
        return preferences.getString(KEY_LANGUAGE, getSystemLanguage());
    }

    /**
     * Gets the system default language
     * @return System language code
     */
    private String getSystemLanguage() {
        Locale systemLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            systemLocale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            systemLocale = Resources.getSystem().getConfiguration().locale;
        }
        
        String language = systemLocale.getLanguage();
        return isLanguageSupported(language) ? language : LANGUAGE_ENGLISH;
    }

    /**
     * Applies the current language setting to the application
     */
    public void applyLanguage() {
        String language = getCurrentLanguage();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    /**
     * Checks if the language has been changed recently
     * @return True if language was changed since last check
     */
    public boolean hasLanguageChanged() {
        boolean changed = languageChanged;
        languageChanged = false;
        return changed;
    }

    /**
     * Checks if current language is RTL (Right-to-Left)
     * @return True if current language is RTL
     */
    public boolean isRtlLanguage() {
        String language = getCurrentLanguage();
        return LANGUAGE_ARABIC.equals(language);
    }

    /**
     * Checks if current language is Arabic
     * @return True if current language is Arabic
     */
    public boolean isArabicLanguage() {
        return LANGUAGE_ARABIC.equals(getCurrentLanguage());
    }

    /**
     * Checks if current language is English
     * @return True if current language is English
     */
    public boolean isEnglishLanguage() {
        return LANGUAGE_ENGLISH.equals(getCurrentLanguage());
    }

    /**
     * Checks if a language is supported by the application
     * @param language Language code to check
     * @return True if language is supported
     */
    public boolean isLanguageSupported(String language) {
        return LANGUAGE_ENGLISH.equals(language) || LANGUAGE_ARABIC.equals(language);
    }

    /**
     * Gets display name for the current language
     * @return Localized display name for the language
     */
    public String getCurrentLanguageDisplayName() {
        String language = getCurrentLanguage();
        
        if (LANGUAGE_ARABIC.equals(language)) {
            return context.getString(R.string.language_arabic);
        } else {
            return context.getString(R.string.language_english);
        }
    }

    /**
     * Gets all available language codes
     * @return Array of language codes
     */
    public String[] getAvailableLanguages() {
        return new String[]{LANGUAGE_ENGLISH, LANGUAGE_ARABIC};
    }

    /**
     * Gets display names for all available languages
     * @return Array of localized language names
     */
    public String[] getLanguageDisplayNames() {
        return new String[]{
            context.getString(R.string.language_english),
            context.getString(R.string.language_arabic)
        };
    }

    /**
     * Gets the current locale object
     * @return Current locale
     */
    public Locale getCurrentLocale() {
        return new Locale(getCurrentLanguage());
    }

    /**
     * Resets language to system default
     */
    public void resetToSystemDefault() {
        setLanguage(getSystemLanguage());
    }

    /**
     * Gets the layout direction for current language
     * @return View.LAYOUT_DIRECTION_RTL or View.LAYOUT_DIRECTION_LTR
     */
    public int getLayoutDirection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isRtlLanguage()) {
                return android.view.View.LAYOUT_DIRECTION_RTL;
            } else {
                return android.view.View.LAYOUT_DIRECTION_LTR;
            }
        }
        return android.view.View.LAYOUT_DIRECTION_LTR;
    }

    /**
     * Creates a context with the specified language
     * @param baseContext Base context to wrap
     * @param language Language code
     * @return Context with applied language
     */
    public static Context createLanguageContext(Context baseContext, String language) {
        Locale locale = new Locale(language);
        Configuration configuration = new Configuration(baseContext.getResources().getConfiguration());
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        return baseContext.createConfigurationContext(configuration);
    }

    /**
     * Static method to initialize language on app start
     * @param context Application context
     */
    public static void initializeLanguage(Context context) {
        LanguageManager languageManager = new LanguageManager(context);
        languageManager.applyLanguage();
    }
}
