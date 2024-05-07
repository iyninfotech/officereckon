package com.infozealrecon.android;

import static com.infozealrecon.android.MainActivity.AUTHKEY;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.infozealrecon.android.Constant.APIURL;
import com.infozealrecon.android.Validations.CheckValidate;
import com.infozealrecon.android.asynctasks.CustomProcessbar;
import com.infozealrecon.android.asynctasks.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OutletDetailsActivity extends Activity {

    ImageView btnBack;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;
    TextView tvOutletID, tvOutletName,tvOutletGroupCode, tvoutletContactPer,tvOutletaddress,tvOutletContactNumber,tvOutletArea,tvOutletCity,tvOutletEmailID,tvOutletState,tvParty_OtherInfo,tvPartyAMCDate,tvPANNumber,tvRemarks,tvGSTNumber,tvSpecialRemark;
    JSONArray jsonArrayDetails;
    LinearLayout layoutactoutdet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_details);

        btnBack = findViewById(R.id.btnBack);
        preferencesUserAuthKey = getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");

        tvOutletID = findViewById(R.id.tvOutletID);
        tvOutletName = findViewById(R.id.tvOutletName);
        tvOutletGroupCode = findViewById(R.id.tvOutletGroupCode);
        tvoutletContactPer = findViewById(R.id.tvoutletContactPer);
        tvOutletaddress = findViewById(R.id.tvOutletaddress);
        tvOutletContactNumber = findViewById(R.id.tvOutletContactNumber);
        tvOutletArea = findViewById(R.id.tvOutletArea);
        tvOutletCity = findViewById(R.id.tvOutletCity);
        tvOutletEmailID = findViewById(R.id.tvOutletEmailID);
        tvOutletState = findViewById(R.id.tvOutletState);
        tvParty_OtherInfo = findViewById(R.id.tvParty_OtherInfo);
        tvPartyAMCDate = findViewById(R.id.tvPartyAMCDate);
        tvPANNumber = findViewById(R.id.tvPANNumber);
        tvGSTNumber = findViewById(R.id.tvGSTNumber);
        tvRemarks = findViewById(R.id.tvRemarks);
        tvSpecialRemark = findViewById(R.id.tvSpecialRemark);
        layoutactoutdet = findViewById(R.id.layoutactoutdet);

        getClientByID();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                overridePendingTransition(android.R.animator.fade_out, android.R.animator.fade_in);
                finish();

            }
        });

    }

    private void getClientByID() {

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
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("Success")) {

                            jsonArrayDetails =  jRootObject.getJSONArray("PartyMasterList");

                            tvOutletID.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Key")));
                            tvOutletName.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Name")));
                            tvOutletGroupCode.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Head_Name")));
                            tvoutletContactPer.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Cp")));
                            tvOutletaddress.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Add")) + ","
                            + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Add1")) +  ","
                            + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Area")) +  ","
                            + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_City")) +  ","
                            + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("District")) +  ","
                            + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Stat")) +  ","
                            + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Pin"))
                            );

                            String ContactPhoneOff ="",ContactPhoneRes="",ContactMob1="",ContactMob2="";
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Phno")).equals(""))
                            {
                                ContactPhoneOff = "Phone (O): "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Phno")) + "\n";
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Phnores")).equals(""))
                            {
                                ContactPhoneRes = "Phone (R): "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Phnores")) + "\n";
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Mobno")).equals(""))
                            {
                                ContactMob1 = "Mobile(1): "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Mobno"));
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Faxno")).equals(""))
                            {
                                ContactMob2 = "\n" + "Mobile(2): "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Faxno"));
                            }
                            tvOutletContactNumber.setText(ContactPhoneOff+ContactPhoneRes+ContactMob1+ContactMob2);

                            tvOutletArea.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Area")));
                            tvOutletCity.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_City")));
                            tvOutletState.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Stat")));
                            tvOutletEmailID.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Email")));

                            String PInfo1 ="",PInfo2="";
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_OtherInfo1")).equals(""))
                            {
                                PInfo1 = CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_OtherInfo1")) + ",";
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_OtherInfo2")).equals(""))
                            {
                                PInfo2 = CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_OtherInfo2"));
                            }
                            tvParty_OtherInfo.setText(PInfo1+PInfo2);

                            String PAMCDT1 ="",PAMCDT2="",PSAMCDT1 ="",PSAMCDT2="";
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Amc")).equals(""))
                            {
                                PAMCDT1 = "AMC ::"+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Amc")) + "\n";
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_InstallDt")).equals(""))
                            {
                                PAMCDT2 = "Date ::"+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_InstallDt"));
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_ServerAmc")).equals(""))
                            {
                                PSAMCDT1 = "\nServer AMC ::" + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_ServerAmc")) + "\n";
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_ServerInstallDt")).equals(""))
                            {
                                PSAMCDT2 = "Date ::"+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_ServerInstallDt"));
                            }
                            tvPartyAMCDate.setText(PAMCDT1+PAMCDT2+PSAMCDT1+PSAMCDT2);

                            tvPANNumber.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Panno")));
                            tvGSTNumber.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Gstin")));

                            String Remark1 ="",Remark2="",Remark3="";
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Remark1")).equals(""))
                            {
                                Remark1 = "Remark(1): "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Remark1")) + "\n";
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Remark2")).equals(""))
                            {
                                Remark2 = "Remark(2): "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Remark2")) + "\n";
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Remark3")).equals(""))
                            {
                                Remark3 = "\n" + "Remark(3): "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Remark3"));
                            }
                            tvRemarks.setText(Remark1+Remark2+Remark3);

                            String StatusRemark1 ="",StatusRemark2="",StatusRemark3="";
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_StatusRemark1")).equals(""))
                            {
                                StatusRemark1 = "Status Remark: "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_StatusRemark1")) + "\n";
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_SpclRemark")).equals(""))
                            {
                                StatusRemark2 = "Special Remark: "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_SpclRemark")) + "\n";
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Status")).equals(""))
                            {
                                StatusRemark3 = "\n" + "Status:: " + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Party_Status"));
                            }
                            tvSpecialRemark.setText(StatusRemark1+StatusRemark2+StatusRemark3);

                           /* if(jsonArrayDetails.getJSONObject(0).getString("Head_Code").contains("A031")){
                                layoutactoutdet.setBackgroundColor(Color.parseColor("#FFF8E649"));

                            }else{

                                if(jsonArrayDetails.getJSONObject(0).getString("Party_Status").toUpperCase().contains("NOT USING SOFTWARE")){
                                    layoutactoutdet.setBackgroundColor(Color.parseColor("#FFFF5722"));

                                }else{
                                    layoutactoutdet.setBackgroundColor(Color.parseColor("#FF00BCD4"));
                                }
                            }*/

                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(OutletDetailsActivity.this, "Error1 " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(OutletDetailsActivity.this, "Error2 " + e.toString());
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(OutletDetailsActivity.this, "Error3 "+ e.toString());
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(OutletDetailsActivity.this, "Error4 " + status.getMessage());
                    if(status.getCode()== 401){

                        AlertDialog.Builder builder = new AlertDialog.Builder(OutletDetailsActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Alert...");
                        builder.setMessage("Please login again...");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", then he is allowed to exit from application

                                Intent intent = new Intent(OutletDetailsActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
                super.callback(url, jRootObject, status);
            }

        }.method(AQuery.METHOD_POST).header("Authorization", "Bearer " + AuthKey));

    }

}
