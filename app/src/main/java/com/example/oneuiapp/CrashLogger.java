package com.example.oneuiapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashLogger implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashLogger";
    private static final String CRASH_LOG_FOLDER = "OneUiApp_CrashLogs";
    
    private Context context;
    private Thread.UncaughtExceptionHandler defaultHandler;
    
    public static void initialize(Context context) {
        new CrashLogger(context);
    }
    
    private CrashLogger(Context context) {
        this.context = context;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        
        // إنشاء مجلد الأخطاء إذا لم يكن موجوداً
        createCrashLogDirectory();
    }
    
    private void createCrashLogDirectory() {
        try {
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File crashLogDir = new File(downloadDir, CRASH_LOG_FOLDER);
            
            if (!crashLogDir.exists()) {
                if (crashLogDir.mkdirs()) {
                    Log.d(TAG, "مجلد سجل الأخطاء تم إنشاؤه: " + crashLogDir.getAbsolutePath());
                } else {
                    Log.e(TAG, "فشل في إنشاء مجلد سجل الأخطاء");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "خطأ في إنشاء مجلد سجل الأخطاء", e);
        }
    }
    
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            // تسجيل الخطأ في ملف
            logCrashToFile(thread, ex);
            
            // تسجيل الخطأ في Logcat
            Log.e(TAG, "خطأ غير متوقع في التطبيق", ex);
            
        } catch (Exception e) {
            Log.e(TAG, "فشل في تسجيل الخطأ", e);
        } finally {
            // استدعاء معالج الأخطاء الافتراضي لإغلاق التطبيق
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, ex);
            } else {
                System.exit(1);
            }
        }
    }
    
    private void logCrashToFile(Thread thread, Throwable ex) {
        try {
            // إنشاء اسم الملف مع الطابع الزمني
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
            String timestamp = dateFormat.format(new Date());
            String fileName = "crash_log_" + timestamp + ".txt";
            
            // مسار ملف السجل
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File crashLogDir = new File(downloadDir, CRASH_LOG_FOLDER);
            File logFile = new File(crashLogDir, fileName);
            
            // كتابة معلومات الخطأ
            FileWriter writer = new FileWriter(logFile);
            PrintWriter printWriter = new PrintWriter(writer);
            
            // كتابة معلومات عامة
            printWriter.println("======== تقرير خطأ التطبيق ========");
            printWriter.println("التاريخ والوقت: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            printWriter.println("اسم التطبيق: " + context.getPackageName());
            printWriter.println("إصدار التطبيق: " + getAppVersion());
            printWriter.println("اسم الخيط: " + thread.getName());
            printWriter.println("معرف الخيط: " + thread.getId());
            printWriter.println("============================");
            printWriter.println();
            
            // كتابة تفاصيل الخطأ
            printWriter.println("======== تفاصيل الخطأ ========");
            printWriter.println("نوع الاستثناء: " + ex.getClass().getSimpleName());
            printWriter.println("رسالة الخطأ: " + ex.getMessage());
            printWriter.println();
            
            // كتابة Stack Trace
            printWriter.println("======== تتبع المكدس ========");
            StringWriter stringWriter = new StringWriter();
            ex.printStackTrace(new PrintWriter(stringWriter));
            printWriter.println(stringWriter.toString());
            
            // كتابة معلومات الجهاز
            printWriter.println();
            printWriter.println("======== معلومات الجهاز ========");
            printWriter.println("العلامة التجارية: " + android.os.Build.BRAND);
            printWriter.println("الطراز: " + android.os.Build.MODEL);
            printWriter.println("المصنع: " + android.os.Build.MANUFACTURER);
            printWriter.println("إصدار Android: " + android.os.Build.VERSION.RELEASE);
            printWriter.println("مستوى API: " + android.os.Build.VERSION.SDK_INT);
            printWriter.println("المعالج: " + android.os.Build.HARDWARE);
            
            printWriter.close();
            writer.close();
            
            Log.i(TAG, "تم حفظ تقرير الخطأ في: " + logFile.getAbsolutePath());
            
        } catch (IOException e) {
            Log.e(TAG, "فشل في كتابة ملف سجل الخطأ", e);
        }
    }
    
    private String getAppVersion() {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "غير معروف";
        }
    }
    
    // طريقة لتسجيل الأخطاء يدوياً
    public static void logError(String tag, String message, Throwable throwable) {
        try {
            Log.e(tag, message, throwable);
            
            // حفظ الخطأ في ملف منفصل
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
            String timestamp = dateFormat.format(new Date());
            String fileName = "error_log_" + timestamp + ".txt";
            
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File crashLogDir = new File(downloadDir, CRASH_LOG_FOLDER);
            File logFile = new File(crashLogDir, fileName);
            
            FileWriter writer = new FileWriter(logFile);
            PrintWriter printWriter = new PrintWriter(writer);
            
            printWriter.println("======== تقرير خطأ يدوي ========");
            printWriter.println("التاريخ والوقت: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            printWriter.println("Tag: " + tag);
            printWriter.println("الرسالة: " + message);
            printWriter.println();
            
            if (throwable != null) {
                printWriter.println("======== تفاصيل الخطأ ========");
                StringWriter stringWriter = new StringWriter();
                throwable.printStackTrace(new PrintWriter(stringWriter));
                printWriter.println(stringWriter.toString());
            }
            
            printWriter.close();
            writer.close();
            
        } catch (Exception e) {
            Log.e(TAG, "فشل في تسجيل الخطأ اليدوي", e);
        }
    }
            }
