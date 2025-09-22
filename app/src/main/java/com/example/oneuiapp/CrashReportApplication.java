package com.example.oneuiapp;

import android.app.Application;
import android.content.Context;

public class CrashReportApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        
        // تشغيل نظام تسجيل الأخطاء
        CrashLogger.initialize(this);
    }

    public static Context getAppContext() {
        return context;
    }
}
