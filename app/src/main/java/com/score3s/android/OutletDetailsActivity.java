package com.score3s.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.score3s.android.Constant.APIURL;
import com.score3s.android.Validations.CheckValidate;
import com.score3s.android.asynctasks.CustomProcessbar;
import com.score3s.android.asynctasks.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.score3s.android.MainActivity.AUTHKEY;

public class OutletDetailsActivity extends Activity {

    ImageView btnBack;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;
    TextView tvOutletID, tvOutName, tvoutletAddress, tvOutletContact, tvOutletEmail, tvOutletContactPerson, tvGSTNo, tvSalesMan, tvRoute, tvOutstanding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_details);

        btnBack = findViewById(R.id.btnBack);
        preferencesUserAuthKey = getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");

        tvOutletID = findViewById(R.id.tvOutletID);
        tvOutName = findViewById(R.id.tvOutletName);
        tvoutletAddress = findViewById(R.id.tvOutletaddress);
        tvOutletContact = findViewById(R.id.tvoutletContact);
        tvOutletEmail = findViewById(R.id.tvOutletEmail);
        tvOutletContactPerson = findViewById(R.id.tvContactPerson);
        tvGSTNo = findViewById(R.id.tvGSTNumber);
        tvSalesMan = findViewById(R.id.tvSalesMan);
        tvRoute = findViewById(R.id.tvRoute);
        tvOutstanding = findViewById(R.id.tvOutstanding);

        getOutletDetails();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                overridePendingTransition(android.R.animator.fade_out, android.R.animator.fade_in);
                finish();

            }
        });

    }

    private void getOutletDetails() {

        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.OUTLET_DETAILS;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);
        params.put("OutletId", getIntent().getStringExtra("id"));

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {

                            JSONObject jsonObjectOutletDetails = jRootObject.getJSONObject("outletDetail");
                            tvOutletID.setText(jsonObjectOutletDetails.getString("OutletId"));
                            tvOutName.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("OutletName")));
                            tvoutletAddress.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("OutletAddress")));
                            tvOutletContact.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("Contact")));
                            tvOutletEmail.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("Email")));
                            tvOutletContactPerson.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("ContactPerson")));
                            tvGSTNo.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("GstNo")));
                            tvSalesMan.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("SalesMan")));
                            tvRoute.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("RouteName")));
                            tvOutstanding.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("Outstanding")));
                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(OutletDetailsActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(OutletDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(OutletDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(OutletDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

}
