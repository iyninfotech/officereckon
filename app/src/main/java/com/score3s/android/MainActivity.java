package com.score3s.android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.score3s.android.Constant.APIURL;
import com.score3s.android.asynctasks.CustomProcessbar;
import com.score3s.android.asynctasks.NetworkUtils;
import com.score3s.android.asynctasks.StringUtils;
import com.score3s.android.asynctasks.ToastUtils;
import com.score3s.android.asynctasks.getAppVersion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private static final String TODO = "";
    public static final String AUTHKEY = "Authi";
    Button btnLogin;
    EditText edtClientID, edtMobileNo, edtUserID, edtPassword;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String UserID, ClientID, Password, MobileNo;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    String IMEINumber = "";
    String AuthKey,AppVersion;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        edtClientID = findViewById(R.id.edtClientID);
        edtMobileNo = findViewById(R.id.edtMobileNo);
        edtUserID = findViewById(R.id.edtUserID);
        edtPassword = findViewById(R.id.edtPassword);

        preferencesUserAuthKey = getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AppVersion = getAppVersion.getVersionInfo(MainActivity.this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void attemptLogin() {

        // Store values at the time of the login attempt.
        UserID = edtUserID.getText().toString();
        ClientID = edtClientID.getText().toString();
        Password = edtPassword.getText().toString();
        MobileNo = edtMobileNo.getText().toString();




        if (StringUtils.isBlank(ClientID)) {
            edtClientID.requestFocus();
            ToastUtils.showErrorToast(this, "Please enter ClientID");
        } else if (StringUtils.isBlank(MobileNo)) {
            edtMobileNo.requestFocus();
            ToastUtils.showErrorToast(this, "Please enter Mobile Number");
        } else if (StringUtils.isBlank(UserID)) {
            edtUserID.requestFocus();
            ToastUtils.showErrorToast(this, "Please enter UserID");
        } else if (StringUtils.isBlank(Password)) {
            edtPassword.requestFocus();
            ToastUtils.showErrorToast(this, "Please enter Password");
        } else {
            if (NetworkUtils.isInternetAvailable(this)) {
                loadIMEI();
//                new LoginTask().execute();
            } else {
                NetworkUtils.showNetworkAlertDialog(this);
            }
        }
    }

    private void login() {

        try {
            CustomProcessbar.showProcessBar(MainActivity.this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.PERFORM_LOGIN;
        Map<String, String> params = new HashMap<String, String>();
        params.put("UserName", edtUserID.getText().toString().trim());
        params.put("Password", edtPassword.getText().toString().trim());
        params.put("ClientId", edtClientID.getText().toString().trim());
        params.put("MobileNo", edtMobileNo.getText().toString().trim());
        params.put("NotificationToken", "");
        if(IMEINumber != null) {
            params.put("IMEINo", IMEINumber);
        }else{
            params.put("IMEINo", "9999");
        }

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

                            editorUserAuthKey.putString("auth", jRootObject.getString("AuthKey"));
                            editorUserAuthKey.putString("CompanyName", jRootObject.getString("CompanyName"));
                            editorUserAuthKey.putString("UserName",jRootObject.getString("UserName"));
                            editorUserAuthKey.apply();

                            AuthKey = preferencesUserAuthKey.getString("auth", "");

                            CheckAuth();


                            /*Intent intent = new Intent(MainActivity.this, NavigationDrawerActivity.class);
                            startActivity(intent);
                            finish();*/

                        } else {
                            ToastUtils.showErrorToast(MainActivity.this, "Login Failed " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(MainActivity.this, "Login Failed");
                    } catch (Exception e) {
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(MainActivity.this, "Login Failed");
                    }
                } else {
                    ToastUtils.showErrorToast(MainActivity.this, "Login Failed");
                }
                super.callback(url, jRootObject, status);
            }

        });

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
                                    Intent intent = new Intent(MainActivity.this, NavigationDrawerActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        } else {
                            if (ErrorMessage.equalsIgnoreCase("Invalid App Version"))
                            {
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
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
                                androidx.appcompat.app.AlertDialog alert = builder.create();
                                alert.show();
                            }
                            else if(ErrorMessage.equalsIgnoreCase("Invalid App Authentication"))
                            {
                                AuthKey = "";

                            }
                            else
                            {
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
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
                                androidx.appcompat.app.AlertDialog alert = builder.create();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.
            requestReadPhoneStatePermission();
        } else {
            // READ_PHONE_STATE permission is already been granted.
            doPermissionGrantedStuffs();
        }
    }


    /**
     * Requests the READ_PHONE_STATE permission.
     * If the permission has been denied previously, a dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission Request")
                    .setMessage("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    })
                    .setIcon(R.drawable.logo)
                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                doPermissionGrantedStuffs();
            } else {
                alertAlert("Permisssion Required");
            }
        }
    }

    private void alertAlert(String msg) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do somthing here
                    }
                })
                .setIcon(R.drawable.logo)
                .show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void doPermissionGrantedStuffs() {
        //Have an  object of TelephonyManager
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Get IMEI Number of Phone  //////////////// for this example i only need the IMEI
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
       // IMEINumber = tm.getImei(0);  // running code old
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        IMEINumber = androidId.trim();
        /*String tmpMobile = tm.getLine1Number().trim();
        if(tmpMobile.contains("+"))
        {
            IMEINumber = tm.getLine1Number();
        }else
        {
            IMEINumber = "+" + tm.getLine1Number();
        }*/

        login();


    }
}