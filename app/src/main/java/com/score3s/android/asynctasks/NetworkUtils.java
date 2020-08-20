package com.score3s.android.asynctasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.score3s.android.R;


public class NetworkUtils {

    public static boolean isInternetAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static void showNetworkAlertDialog(Context mCtx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx, R.style.CustomDialogTheme);
        builder.setCancelable(false);
        builder.setTitle(mCtx.getString(R.string.connectivity_error));
        builder.setMessage(mCtx.getString(R.string.no_internet_connection_available_right_now));
        builder.setPositiveButton(mCtx.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

}
