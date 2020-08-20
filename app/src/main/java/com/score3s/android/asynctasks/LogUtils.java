package com.score3s.android.asynctasks;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class LogUtils {

    private static boolean isLogEnable = false;

    public static void Log_e(String tag, String msg) {
        if (isLogEnable) {
            Log.e(tag, msg);
        }
    }

    public static void Log_e(String tag, long msg) {
        if (isLogEnable) {
            Log.e(tag, "" + msg);
        }
    }

    public static void Log_e(String tag, CharSequence msg) {
        if (isLogEnable) {
            Log.e(tag, msg.toString());
        }
    }

    public static void Log_e(String tag, int msg) {
        if (isLogEnable) {
            Log.e(tag, "" + msg);
        }
    }

    public static void Log_e(String tag, boolean msg) {
        if (isLogEnable) {
            Log.e(tag, "" + msg);
        }
    }

    public static void Log_e(String tag, JSONObject msg) {
        if (isLogEnable) {
            Log.e(tag, msg.toString());
        }
    }

    public static void Log_e(String tag, JSONArray msg) {
        if (isLogEnable) {
            Log.e(tag, msg.toString());
        }
    }

}
