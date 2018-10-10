package com.biz4solutions.provider.application;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;

import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;

import java.io.File;

/*
 * Created by ketan on 11/30/2017.
 */
public class Application extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (ContextCompat.checkSelfPermission(Application.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(Application.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                createLoggerFile();
            }
        } else {
            createLoggerFile();
        }

        //for ignoring uri exposure
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public void createLoggerFile() {
        try {
            File mydir = new File(CommonFunctions.getSupportDirectoryPath());
            String supportFilePath = CommonFunctions.getSupportDirectoryPath() + Constants.SUPPORT_FILE_NAME;
            boolean isMyDirCreated = true;
            if (!mydir.exists()) {
                isMyDirCreated = mydir.mkdirs();
            } else {
                File mFile = new File(supportFilePath);
                if (mFile.exists() && mFile.length() > 20000000 && mFile.delete()) { // delete file if file size is greater than 20MB
                    isMyDirCreated = mydir.mkdirs();
                }
                if (!mydir.exists()) {
                    isMyDirCreated = mydir.mkdirs();
                }
            }

            if (isMyDirCreated) {
                Runtime.getRuntime().exec(new String[]{"logcat", "-v", "time", "-f", CommonFunctions.getSupportDirectoryPath() + Constants.SUPPORT_FILE_NAME, "", ""});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}