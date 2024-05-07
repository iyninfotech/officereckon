package com.infozealrecon.android;

import static com.infozealrecon.android.MainActivity.AUTHKEY;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.infozealrecon.android.Constant.APIURL;
import com.infozealrecon.android.asynctasks.CustomProcessbar;
import com.infozealrecon.android.asynctasks.NetworkUtils;
import com.infozealrecon.android.asynctasks.ToastUtils;
import com.infozealrecon.android.asynctasks.getAppVersion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends Activity {
    //Duration of Splash
    private Handler handler;
    String isLogin;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey,AppVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();

        preferencesUserAuthKey = getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");

        AppVersion = getAppVersion.getVersionInfo(SplashActivity.this);


        final Handler temp = new Handler();

        if (NetworkUtils.isInternetAvailable(SplashActivity.this)) {

            CheckAuthKey();


        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
            builder.setCancelable(false);
            builder.setTitle(R.string.connectivity_error);
            builder.setMessage(R.string.no_internet_connection_available_right_now);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

      //  scheduleVideoPause(2000);

    }

    private void CheckAuthKey() {

        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.CLIENTBYID;

        Map<String, String> params = new HashMap<String, String>();
        params.put("Party_Key", getIntent().getStringExtra("Party_Key"));

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        scheduleVideoPause(2000);
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                       /* if (ErrorMessage.equalsIgnoreCase("Success")) {


                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(SplashActivity.this, "Error1 " + ErrorMessage);
                        }*/
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(SplashActivity.this, "Error2 " + e.toString());
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(SplashActivity.this, "Error3 "+ e.toString());
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(SplashActivity.this, "Error4 " + status.getMessage());
                    if(status.getCode()== 401){
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
                super.callback(url, jRootObject, status);
            }

        }.method(AQuery.METHOD_POST).header("Authorization", "Bearer " + AuthKey));

    }
    private void scheduleVideoPause(int msec) {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                    Intent mainIntent = new Intent(SplashActivity.this, NavigationDrawerActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();


            }
        }, msec);
    }

}
