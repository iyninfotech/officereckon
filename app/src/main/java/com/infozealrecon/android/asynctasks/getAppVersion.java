package com.infozealrecon.android.asynctasks;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class getAppVersion {
    static String versionName = "";
    static int versionCode = -1;
    static Activity NewActivity;

    public static final String getVersionInfo(Activity mctx) {

        NewActivity = mctx;
        try {
            PackageInfo packageInfo = NewActivity.getPackageManager().getPackageInfo(NewActivity.getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // tvVersionDetails.setText(String.format("Version code = %d  \nVersion name = %s", versionCode, versionName));

        return String.valueOf(versionCode);

    }


}
