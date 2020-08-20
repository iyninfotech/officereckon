package com.score3s.android.fontStyle;

//import android.app.AlertDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.score3s.android.NavigationDrawerActivity;
import com.score3s.android.R;


public class ShowAlert {
    public static String Title = "Warning...";
    public static String Msg;
    public static Activity CurrentActivity;

    public static void ShowWarning(Context mCtx, String messgae) {
        Msg = messgae;
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
        builder.setCancelable(false);
        builder.setTitle(Title);
        builder.setMessage(Msg);
        builder.setPositiveButton(mCtx.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public static void ShowAlert(Context mCtx, String title, String messgae) {
        Title = title;
        Msg = messgae;

        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
        builder.setCancelable(false);
        builder.setTitle(Title);
        builder.setMessage(Msg);
        builder.setPositiveButton(mCtx.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public static void ShowAlertOkCancle(Activity mCtx, String title, String messgae) {

        CurrentActivity = mCtx;
        Title = title;
        Msg = messgae;
        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentActivity);
        builder.setCancelable(false);
        builder.setTitle(Title);
        builder.setMessage(Msg);
        builder.setPositiveButton(mCtx.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CurrentActivity, NavigationDrawerActivity.class);
                        CurrentActivity.startActivity(intent);
                        CurrentActivity.finish();
                        dialog.dismiss();

                    }
                });
        builder.setNegativeButton(mCtx.getString(R.string.cancle),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
