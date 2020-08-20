package com.score3s.android.asynctasks;

import android.text.Html;
import android.text.Spanned;

public class StringUtils {
    public static boolean isNotBlank(String str) {
        if (str == null || str.trim().equalsIgnoreCase(""))
            return false;
        return true;
    }

    public static boolean isBlank(String str) {
        if (str == null || str.trim().equals(""))
            return true;
        return false;
    }

    public static String getReverseString(String msg) {
        if (msg.equalsIgnoreCase("0")) {
            return "1";
        } else if (msg.equalsIgnoreCase("1")) {
            return "0";
        } else if (msg.equalsIgnoreCase("")) {
            return "1";
        } else {
            return "";
        }
    }

    public static String getColoredString(String text, int color) {
        String str = "";
        str = "<font color='" + color + "'>" + text + "</font>";
        return str;
    }

    public static Spanned getColoredSpanned(String text, int color) {
        String input = "<font color='" + color + "'>" + text + "</font>";
        Spanned spannedStrinf = Html.fromHtml(input);
        return spannedStrinf;
    }

    public static boolean isBothStringsSame(String str1, String str2) {
        if (str1.equals(str2)) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isBothStringsSameWithIgnore(String str1, String str2) {
        if (str1.equalsIgnoreCase(str2)) {
            return true;
        } else {
            return false;
        }
    }

    public static String concatTwoStrigsWithComma(String str1, String str2) {
        return str1 + ", " + str2;
    }

    public static String getStringFromBoolean(boolean what) {
        String result = "0";
        if (what) {
            result = "1";
        }
        return result;
    }

}
