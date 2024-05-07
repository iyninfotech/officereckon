package com.infozealrecon.android.Validations;

import android.text.TextUtils;

public class CheckValidate {


    public static final double checkemptyDouble(String string) {

        if (string.trim().equals(".")) {
            string = "";
        }
        if (!TextUtils.isEmpty(string)) {
            return Double.parseDouble(string);
        } else {
            return 0;
        }
    }


    public static final int checkemptyintger(String string) {

        if (string.trim().equals(".")) {
            string = "";
        }
        if (!TextUtils.isEmpty(string)) {
            return Integer.parseInt(string);
        } else {
            return 0;
        }
    }



    public static final String checkemptyTV(String string) {
        if (string.equals("0") || string.equals("0.0") || string.equals("0.00") || string.equals("null") || string.equals("NULL")) {
            string = "";
        }
        if (!TextUtils.isEmpty(string.trim())) {
            return string;
        } else {
            return "";
        }
    }

    public static final String checkemptystring(String string) {
        if (string.trim().equals(".") || string.trim().equals("null") || string.trim().equals("NULL")) {
            string = "";
        }
        if (!TextUtils.isEmpty(string.trim())) {
            return string;
        } else {
            return "";
        }
    }

    public static final String checkempty(String string) {

        if (!TextUtils.isEmpty(string)) {
            return string;
        } else {
            return "";
        }
    }



}
