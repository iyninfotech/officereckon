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

public class MRPDetailsActivity extends Activity {

    ImageView btnBack;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;
    TextView tvItemId,tvMRPId,tvItemCode,tvItemname,tvBrand,tvCategory,tvmainGroup,tvDivision,tvMRP,tvSalesRate,tvSalesRateWithtax,tvbottomSalesRate,tvStock,tvCGSTP,tvSGSTP,tvIGSTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mrp_details);

        btnBack = findViewById(R.id.btnBack);
        preferencesUserAuthKey = getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");

        tvItemId = findViewById(R.id.tvItemId);
        tvMRPId = findViewById(R.id.tvMRPId);
        tvItemCode = findViewById(R.id.tvItemCode);
        tvItemname = findViewById(R.id.tvItemName);
        tvBrand = findViewById(R.id.tvBrand);
        tvCategory = findViewById(R.id.tvCategory);
        tvmainGroup = findViewById(R.id.tvMainGroup);
        tvDivision = findViewById(R.id.tvDivision);
        tvMRP = findViewById(R.id.tvMRP);
        tvSalesRate = findViewById(R.id.tvSalesRate);
        tvSalesRateWithtax = findViewById(R.id.tvSalesRateWithTax);
        tvbottomSalesRate = findViewById(R.id.tvBottoSalesRate);
        tvStock = findViewById(R.id.tvStock);
        tvCGSTP = findViewById(R.id.tvCGSTP);
        tvSGSTP = findViewById(R.id.tvSGSTP);
        tvIGSTP = findViewById(R.id.tvIGSTP);



        getMRPDetails();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                overridePendingTransition(android.R.animator.fade_out, android.R.animator.fade_in);
                finish();

            }
        });

    }

    private void getMRPDetails() {

        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.MRP_DETAILS;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);
        params.put("ItemId", getIntent().getStringExtra("id"));
        params.put("MRPId", getIntent().getStringExtra("MRPId"));


        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {

                            JSONObject jsonObjectOutletDetails = jRootObject.getJSONObject("MRPDetail");
                            tvItemId.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("ItemId")));
                            tvMRPId.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("MRPId")));
                            tvItemCode.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("ItemCode")));
                            tvItemname.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("ItemName")));
                            tvBrand.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("Brand")));
                            tvCategory.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("Category")));
                            tvmainGroup.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("MainGroup")));
                            tvDivision.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("Division")));
                            tvMRP.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("MRP")));
                            tvSalesRate.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("SalesRate")));
                            tvSalesRateWithtax.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("SalesRateWithTax")));
                            tvbottomSalesRate.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("BottomSalesRate")));
                            tvStock.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("Stock")));
                            tvCGSTP.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("CGSTPerc")));
                            tvSGSTP.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("SGSTPerc")));
                            tvIGSTP.setText(CheckValidate.checkemptyTV(jsonObjectOutletDetails.getString("IGSTPerc")));
                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(MRPDetailsActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(MRPDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(MRPDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(MRPDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }


}
