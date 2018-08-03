package com.biz4solutions.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.biz4solutions.R;
import com.biz4solutions.utilities.CommonFunctions;
import com.biz4solutions.utilities.Constants;

import java.io.File;

/*
 * Created by ketan on 11/30/2017.
 */
public class CrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        CommonFunctions.getInstance().hideSoftKeyBoard(this);
    }

    public void launchMainActivity(View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                /*Intent intent = new Intent(CrashActivity.this, MainActivity.class);
                startActivity(intent);*/
                finish();
            }
        }, 200);
    }

    public void emailLogFile(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.SEND_LOG_EMAIL_ADDRESS1/*, Constants.SEND_LOG_EMAIL_ADDRESS2*/});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, Constants.SUPPORT_LOG_EXTRA_SUBJECT);
        emailIntent.putExtra(Intent.EXTRA_TEXT, Constants.SUPPORT_LOG_EXTRA_TEXT);
        File file = new File(CommonFunctions.getSupportDirectoryPath(), Constants.SUPPORT_FILE_NAME);
        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.setData(Uri.parse("mailto:"));
        startActivityForResult(emailIntent, Constants.SEND_LOG_EMAIL);
    }
}
