package com.example.oneuiapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * فئة لإدارة ملفات سجل الأخطاء
 * تقدم إمكانيات عرض وحذف وتصدير ملفات السجل
 */
public class CrashLogManager {
    private static final String TAG = "CrashLogManager";
    private static final String CRASH_LOG_FOLDER = "OneUiApp_CrashLogs";
    private static final long MAX_LOG_FILES = 50; // الحد الأقصى لعدد ملفات السجل
    private static final long MAX_FILE_AGE_DAYS = 30; // الحد الأقصى لعمر الملف بالأيام
    
    private Context context;
    
    public CrashLogManager(Context context) {
        this.context = context;
    }
    
    /**
     * الحصول على مسار مجلد سجلات الأخطاء
     */
    public File getCrashLogDirectory() {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return new File(downloadDir, CRASH_LOG_FOLDER);
    }
    
    /**
     * الحصول على قائمة بجميع ملفات السجل
     */
    public List<File> getAllLogFiles() {
        File logDir = getCrashLogDirectory();
        if (!logDir.exists()) {
            return new ArrayList<>();
        }
        
        File[] files = logDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && file.getName().endsWith(".txt");
            }
        });
        
        if (files == null) {
            return new ArrayList<>();
        }
        
        List<File> logFiles = Arrays.asList(files);
        // ترتيب الملفات حسب التاريخ (الأحدث أولاً)
        logFiles.sort(new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });
        
        return logFiles;
    }
    
    /**
     * حذف ملف سجل محدد
     */
    public boolean deleteLogFile(File logFile) {
        try {
            if (logFile.exists()) {
                boolean deleted = logFile.delete();
                if (deleted) {
                    Log.d(TAG, "تم حذف ملف السجل: " + logFile.getName());
                }
                return deleted;
            }
        } catch (Exception e) {
            Log.e(TAG, "فشل في حذف ملف السجل", e);
        }
        return false;
    }
    
    /**
     * حذف جميع ملفات السجل
     */
    public int deleteAllLogFiles() {
        List<File> logFiles = getAllLogFiles();
        int deletedCount = 0;
        
        for (File file : logFiles) {
            if (deleteLogFile(file)) {
                deletedCount++;
            }
        }
        
        Log.d(TAG, "تم حذف " + deletedCount + " ملف سجل");
        return deletedCount;
    }
    
    /**
     * حذف ملفات السجل القديمة تلقائياً
     */
    public void cleanupOldLogFiles() {
        List<File> logFiles = getAllLogFiles();
        long currentTime = System.currentTimeMillis();
        long maxAge = MAX_FILE_AGE_DAYS * 24 * 60 * 60 * 1000L; // تحويل الأيام إلى ملي ثانية
        
        int deletedCount = 0;
        
        // حذف الملفات القديمة
        for (File file : logFiles) {
            long fileAge = currentTime - file.lastModified();
            if (fileAge > maxAge) {
                if (deleteLogFile(file)) {
                    deletedCount++;
                }
            }
        }
        
        // حذف الملفات الزائدة إذا تجاوز العدد الحد الأقصى
        List<File> remainingFiles = getAllLogFiles();
        if (remainingFiles.size() > MAX_LOG_FILES) {
            for (int i = (int) MAX_LOG_FILES; i < remainingFiles.size(); i++) {
                if (deleteLogFile(remainingFiles.get(i))) {
                    deletedCount++;
                }
            }
        }
        
        if (deletedCount > 0) {
            Log.d(TAG, "تم حذف " + deletedCount + " ملف سجل قديم");
        }
    }
    
    /**
     * الحصول على معلومات حول ملفات السجل
     */
    public LogFileInfo getLogFileInfo() {
        List<File> logFiles = getAllLogFiles();
        
        LogFileInfo info = new LogFileInfo();
        info.totalFiles = logFiles.size();
        
        long totalSize = 0;
        for (File file : logFiles) {
            totalSize += file.length();
        }
        info.totalSizeBytes = totalSize;
        
        if (!logFiles.isEmpty()) {
            info.oldestFile = logFiles.get(logFiles.size() - 1);
            info.newestFile = logFiles.get(0);
        }
        
        return info;
    }
    
    /**
     * فئة لتخزين معلومات ملفات السجل
     */
    public static class LogFileInfo {
        public int totalFiles = 0;
        public long totalSizeBytes = 0;
        public File oldestFile = null;
        public File newestFile = null;
        
        public String getTotalSizeFormatted() {
            if (totalSizeBytes < 1024) {
                return totalSizeBytes + " بايت";
            } else if (totalSizeBytes < 1024 * 1024) {
                return String.format(Locale.getDefault(), "%.1f كيلوبايت", totalSizeBytes / 1024.0);
            } else {
                return String.format(Locale.getDefault(), "%.1f ميجابايت", totalSizeBytes / (1024.0 * 1024.0));
            }
        }
        
        public String getOldestFileDate() {
            if (oldestFile != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                return dateFormat.format(new Date(oldestFile.lastModified()));
            }
            return "غير محدد";
        }
        
        public String getNewestFileDate() {
            if (newestFile != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                return dateFormat.format(new Date(newestFile.lastModified()));
            }
            return "غير محدد";
        }
    }
    
    /**
     * إنشاء تقرير موجز عن حالة التطبيق (للمطورين)
     */
    public String generateStatusReport() {
        StringBuilder report = new StringBuilder();
        LogFileInfo info = getLogFileInfo();
        
        report.append("======== تقرير حالة التطبيق ========\n");
        report.append("التاريخ والوقت: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n");
        report.append("عدد ملفات السجل: ").append(info.totalFiles).append("\n");
        report.append("الحجم الإجمالي: ").append(info.getTotalSizeFormatted()).append("\n");
        report.append("أقدم ملف: ").append(info.getOldestFileDate()).append("\n");
        report.append("أحدث ملف: ").append(info.getNewestFileDate()).append("\n");
        report.append("مسار مجلد السجلات: ").append(getCrashLogDirectory().getAbsolutePath()).append("\n");
        report.append("================================\n");
        
        return report.toString();
    }
  }
