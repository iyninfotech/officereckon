package com.score3s.android;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.score3s.android.Constant.APIURL;
import com.score3s.android.asynctasks.CustomProcessbar;
import com.score3s.android.asynctasks.NetworkUtils;
import com.score3s.android.asynctasks.getAppVersion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.score3s.android.MainActivity.AUTHKEY;

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

            CheckAuth();

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

    private void CheckAuth() {

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.LOGIN_AUTH;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey",AuthKey);
        params.put("AppVersion", AppVersion);

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                CustomProcessbar.hideProcessBar();
                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {
                            if(jRootObject.getString("LoginStatus").equals("Success")){

                                if(jRootObject.getString("ServerAppVersion").equals(AppVersion))
                                {

                                    scheduleVideoPause(2000);
                                }
                            }
                        } else {
                            if (ErrorMessage.equalsIgnoreCase("Invalid App Version"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                                builder.setCancelable(false);
                                builder.setTitle("Alert..");
                                builder.setMessage(String.format("Please, Update your Score3S App!"));
                                builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //if user pressed "yes", then he is allowed to exit from application
                                        finish();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                            else if(ErrorMessage.equalsIgnoreCase("Invalid App Authentication"))
                            {
                                AuthKey = "";
                                scheduleVideoPause(2000);
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                                builder.setCancelable(false);
                                builder.setTitle("Alert..");
                                builder.setMessage(String.format("Please, contact \n 'Infozeal eSolutions Private Limited'"));
                                builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //if user pressed "yes", then he is allowed to exit from application
                                        finish();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        }
                    } catch (JSONException e) {
                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();

                    } catch (Exception e) {
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();

                    }
                } else {

                }
                super.callback(url, jRootObject, status);
            }

        });

    }
    private void scheduleVideoPause(int msec) {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(AuthKey.length() == 0) {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                    editorUserAuthKey.putString("SELECTVALUE","0");
                    editorUserAuthKey.apply();
                }else{
                    Intent mainIntent = new Intent(SplashActivity.this, NavigationDrawerActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                    editorUserAuthKey.putString("SELECTVALUE","0");
                    editorUserAuthKey.apply();

                }

            }
        }, msec);
    }

}
