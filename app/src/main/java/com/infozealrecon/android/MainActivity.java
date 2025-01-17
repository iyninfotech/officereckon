package com.infozealrecon.android;

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
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.infozealrecon.android.Constant.APIURL;
import com.infozealrecon.android.asynctasks.CustomProcessbar;
import com.infozealrecon.android.asynctasks.NetworkUtils;
import com.infozealrecon.android.asynctasks.StringUtils;
import com.infozealrecon.android.asynctasks.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private static final String TODO = "";
    public static final String AUTHKEY = "Authi";
    Button btnLogin;
    EditText edtClientID, edtMobileNo, edtUserID, edtPassword;
    TextView tvDemoRequest;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;

    String UserID, Password;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    String IMEINumber = "";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);

        edtUserID = findViewById(R.id.edtUserID);
        edtPassword = findViewById(R.id.edtPassword);
        tvDemoRequest = findViewById(R.id.tvDemoRequest);

        preferencesUserAuthKey = getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });


        String text = "If your are not using our software then please click on <br /> <b><i>Customer Lock</b></i>";
        SpannableString spannableString = new SpannableString(Html.fromHtml(text));
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                 Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                            startActivity(intent);
                            finish();
            }
        };

        spannableString.setSpan(clickableSpan1, 56,69, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDemoRequest.setText(spannableString);
        tvDemoRequest.setMovementMethod(LinkMovementMethod.getInstance());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void attemptLogin() {

        // Store values at the time of the login attempt.
        UserID = edtUserID.getText().toString();
        Password = edtPassword.getText().toString();


        if (StringUtils.isBlank(UserID)) {
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
        params.put("User_Id", edtUserID.getText().toString().trim().toUpperCase());
        params.put("User_Pass", edtPassword.getText().toString().trim().toUpperCase());
        editorUserAuthKey.putString("username", edtUserID.getText().toString().trim());
        editorUserAuthKey.apply();


        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                CustomProcessbar.hideProcessBar();
                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("Success")) {

                            editorUserAuthKey.putString("auth", jRootObject.getString("AuthToken"));
                            editorUserAuthKey.apply();

                            Intent intent = new Intent(MainActivity.this, NavigationDrawerActivity.class);
                            startActivity(intent);
                            finish();

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