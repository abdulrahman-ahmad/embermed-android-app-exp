package com.biz4solutions.application;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.biz4solutions.utilities.Constants;

import java.io.File;

/*
 * Created by ketan on 11/30/2017.
 */
public class Application extends android.app.Application {
    private String APP_DATA_DIRECTORY;

    @Override
    public void onCreate() {
        super.onCreate();
        APP_DATA_DIRECTORY = "Android/data/" + getApplicationContext().getPackageName() + "/";
        if (ContextCompat.checkSelfPermission(Application.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(Application.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                createLoggerFile();
            } /*else {
                //isDatabaseOpen = false;
            }*/
        } else {
            createLoggerFile();
        }
    }

    public void createLoggerFile() {
        try {
            File mydir = new File(getSupportDirectoryPath());
            String supportFilePath = getSupportDirectoryPath() + Constants.SUPPORT_FILE_NAME;
            boolean isMyDirCreated = true;
            if (!mydir.exists()) {
                isMyDirCreated = mydir.mkdirs();
            } else {
                File mFile = new File(supportFilePath);
                if (mFile.exists() && mFile.length() > 20000000 && mFile.delete()) { // delete file if file size is greater than 20MB
                    isMyDirCreated = mydir.mkdirs();
                }
            }

            if (isMyDirCreated) {
                Runtime.getRuntime().exec(new String[]{"logcat", "-v", "time", "-f", getSupportDirectoryPath() + Constants.SUPPORT_FILE_NAME, "", ""});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public String getSupportDirectoryPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_DATA_DIRECTORY + File.separator + Constants.SUPPORT_DIRECTORY + File.separator;
    }
}
