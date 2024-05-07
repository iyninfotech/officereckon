package com.infozealrecon.android;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.infozealrecon.android.Constant.APIURL;
import com.infozealrecon.android.asynctasks.CustomProcessbar;
import com.infozealrecon.android.asynctasks.NetworkUtils;
import com.infozealrecon.android.asynctasks.ToastUtils;
import com.infozealrecon.android.fontStyle.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity {


    Button btnRegister,btnDelete;
    ImageView ivSearch;
    EditText edtCustomerID, edtDate;
    JSONArray jsonArrayCustomerdata;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegister);
        btnDelete = findViewById(R.id.btnDelete);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        edtCustomerID = findViewById(R.id.edtCustomerID);
        edtDate = findViewById(R.id.edtDate);


        ivSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (NetworkUtils.isInternetAvailable(RegisterActivity.this)) {

                    if(TextUtils.isEmpty(edtCustomerID.getText().toString().trim()))
                    {
                        edtCustomerID.requestFocus();
                        edtCustomerID.setError("Please Provide Customer ID...!");

                    }else
                    {
                        GETCUSTID();
                    }

                } else {

                    NetworkUtils.showNetworkAlertDialog(RegisterActivity.this);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkUtils.isInternetAvailable(RegisterActivity.this)) {


                    if(TextUtils.isEmpty(edtCustomerID.getText().toString().trim()))
                    {
                        edtCustomerID.requestFocus();
                        edtCustomerID.setError("Please Provide Customer ID...!");

                    }else if(TextUtils.isEmpty(edtDate.getText().toString()) || edtDate.getText().toString().equals("") || edtDate.getText().toString().equals("##//#/#/##"))
                    {
                        edtDate.requestFocus();
                        edtDate.setError("Please Provide lock Date...!");
                    }else
                    {
                        ADDUPDATE();
                    }

                } else {

                    NetworkUtils.showNetworkAlertDialog(RegisterActivity.this);
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkUtils.isInternetAvailable(RegisterActivity.this)) {

                    if(TextUtils.isEmpty(edtCustomerID.getText().toString().trim()))
                    {
                        edtCustomerID.requestFocus();
                        edtCustomerID.setError("Please Provide Customer ID...!");

                    }else if(TextUtils.isEmpty(edtDate.getText().toString()) || edtDate.getText().toString().equals("") || edtDate.getText().toString().equals("##//#/#/##"))
                    {
                        edtDate.requestFocus();
                        edtDate.setError("Please Provide lock Date...!");
                    }
                    else{
                        LOCKDELETE();
                    }

                } else {

                    NetworkUtils.showNetworkAlertDialog(RegisterActivity.this);
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void GETCUSTID() {
        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL_epr + APIURL.GETCUSTID;
        Map<String, String> params = new HashMap<String, String>();
        params.put("intCompanyCustomerID", edtCustomerID.getText().toString());

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {
                            if (ErrorMessage.equalsIgnoreCase("")) {


                                jsonArrayCustomerdata = new JSONArray(new ArrayList<String>());

                                jsonArrayCustomerdata = jRootObject.getJSONArray("companyCustomerData");

                                for (int i = 0; i < jsonArrayCustomerdata.length(); i++) {
                                    edtDate.setText(jsonArrayCustomerdata.getJSONObject(i).getString("strCompanyCustomerDate").toString().replace("/",""));
                                }
                            }

                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();
                            ShowAlert.ShowAlert(RegisterActivity.this, "Alert...", ErrorMessage);
                            edtDate.setText("");
                            //ToastUtils.showErrorToast(RegisterActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(RegisterActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(RegisterActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(RegisterActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void ADDUPDATE() {
        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL_epr + APIURL.ADDUPDATE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("intCompanyCustomerID", edtCustomerID.getText().toString());
        params.put("strCompanyCustomerDate", edtDate.getText().toString());

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {
                            if (ErrorMessage.equalsIgnoreCase("")) {

                                ShowAlert.ShowAlert(RegisterActivity.this, "Done...", "Add/Update Successfully.");

                                    edtCustomerID.setText("");
                                    edtDate.setText("");

                            }

                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();
                            ShowAlert.ShowAlert(RegisterActivity.this, "Alert...", ErrorMessage);
                            //ToastUtils.showErrorToast(RegisterActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(RegisterActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(RegisterActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(RegisterActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void LOCKDELETE() {
        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL_epr + APIURL.LOCKDELETE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("intCompanyCustomerID", edtCustomerID.getText().toString());

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {
                            if (ErrorMessage.equalsIgnoreCase("")) {


                                ShowAlert.ShowAlert(RegisterActivity.this, "Done...", "Deleted Successfully.");
                                edtCustomerID.setText("");
                                edtDate.setText("");

                            }

                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();
                            ShowAlert.ShowAlert(RegisterActivity.this, "Alert...", ErrorMessage);
                            //ToastUtils.showErrorToast(RegisterActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(RegisterActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(RegisterActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(RegisterActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }


}