package com.infozealrecon.android;

import static com.infozealrecon.android.MainActivity.AUTHKEY;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.infozealrecon.android.Adapter.InquiryFollowupAdapter;
import com.infozealrecon.android.Constant.APIURL;
import com.infozealrecon.android.Validations.CheckValidate;
import com.infozealrecon.android.asynctasks.CustomProcessbar;
import com.infozealrecon.android.asynctasks.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InquiryDetailsActivity extends Activity {

    ImageView btnBack;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;
    TextView tvInquiryID,tvInquiryDate, tvInquiryName, tvInquiryContactPer,tvInquiryaddress,tvInquiryContactNumber,tvInquiryArea,tvInquiryCity,tvInquiryEmailID,tvInquiryState,tvInq_Reference,tvInq_Passon,tvRemarks,tvSpecialRemark;
    JSONArray jsonArrayDetails,jsonArrayInquiryForList,jsonArrayInquiryStageList,jsonArrayInquiryTranList;
    LinearLayout ll;
    LinearLayout ll2;
    CheckBox cb;
    CheckBox cb2;

    RecyclerView recyclerView;
    InquiryFollowupAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_details_new);

        btnBack = findViewById(R.id.btnBack);
        preferencesUserAuthKey = getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");

        tvInquiryID = findViewById(R.id.tvInquiryID);
        tvInquiryDate = findViewById(R.id.tvInquiryDate);
        tvInquiryName = findViewById(R.id.tvInquiryName);
        tvInquiryContactPer = findViewById(R.id.tvInquiryContactPer);
        tvInquiryaddress = findViewById(R.id.tvInquiryaddress);
        tvInquiryContactNumber = findViewById(R.id.tvInquiryContactNumber);
        tvInquiryArea = findViewById(R.id.tvInquiryArea);
        tvInquiryCity = findViewById(R.id.tvInquiryCity);
        tvInquiryEmailID = findViewById(R.id.tvInquiryEmailID);
        tvInquiryState = findViewById(R.id.tvInquiryState);
        tvInq_Reference = findViewById(R.id.tvInq_Reference);
        tvInq_Passon = findViewById(R.id.tvInq_Passon);
        tvRemarks = findViewById(R.id.tvRemarks);
        tvSpecialRemark = findViewById(R.id.tvSpecialRemark);

        getInquiryByID();

        ll = (LinearLayout)findViewById(R.id.rootInquiryForchBox);
        ll2 = (LinearLayout)findViewById(R.id.rootInquiryStageBox);

        recyclerView = findViewById(R.id.listViewFollowup);
        recyclerView.setLayoutManager(new LinearLayoutManager(InquiryDetailsActivity.this));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                overridePendingTransition(android.R.animator.fade_out, android.R.animator.fade_in);
                finish();

            }
        });

    }

    private void getInquiryByID() {

        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.GETInquiryHeadBYID;

        Map<String, String> params = new HashMap<String, String>();
        params.put("Inq_Code", getIntent().getStringExtra("Inq_Code"));

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("Success")) {

                            jsonArrayDetails =  jRootObject.getJSONArray("InquiryHeadList");
                            jsonArrayInquiryForList =  jRootObject.getJSONArray("InquiryForList");
                            jsonArrayInquiryStageList =  jRootObject.getJSONArray("InquiryStageList");
                            jsonArrayInquiryTranList =  jRootObject.getJSONArray("InquiryTranList");
                            tvInquiryID.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_No")));
                            tvInquiryDate.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Date")));
                            tvInquiryName.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Name")));

                            tvInquiryContactPer.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Contactperson")));
                            tvInquiryaddress.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Add1")) + ","
                            + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Add2")) +  ", "
                            + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Area")) +  ", "
                            + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_City")) +  ", "
                            + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_State")) +  ", "
                            + CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Pin"))
                            );

                            String ContactPhoneOff ="",ContactMob1="",ContactMob2="",ContactMob3="";
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Mobile")).equals(""))
                            {
                                ContactMob1 = "Mobile No.1: "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Mobile"));
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Mobile2")).equals(""))
                            {
                                ContactMob2 = "\nMobile No.2: "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Mobile2"));
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Mobile3")).equals(""))
                            {
                                ContactMob3 = "\nMobile No.3: "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Mobile3"));
                            }
                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Phone")).equals(""))
                            {
                                ContactPhoneOff = "\n" + "Phone No.: "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Phone"));
                            }
                            tvInquiryContactNumber.setText(ContactMob1+ContactMob2+ContactMob3 + ContactPhoneOff);

                            tvInquiryArea.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Area")));
                            tvInquiryCity.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_City")));
                            tvInquiryState.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_State")));
                            tvInquiryEmailID.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Email")));

                            tvInq_Reference.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Reference")));

                            tvInq_Passon.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Passon")));


                            tvRemarks.setText(CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Remark")));

                            if(!CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_LostReason")).equals(""))
                            {
                                tvSpecialRemark.setText("Status:: "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Status"))
                                                       + "\nReason:: "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_LostReason"))
                                                       );

                            }else{
                                tvSpecialRemark.setText("Status:: "+ CheckValidate.checkemptyTV(jsonArrayDetails.getJSONObject(0).getString("Inq_Status")));
                            }



                            //add checkboxes
                            for(int i = 0; i < jsonArrayInquiryForList.length(); i++) {
                                cb = new CheckBox(InquiryDetailsActivity.this);
                                cb.setText(jsonArrayInquiryForList.getJSONObject(i).getString("Inq_For"));
                                cb.setId(i);
                                cb.setClickable(false);
                                cb.setChecked(jsonArrayInquiryForList.getJSONObject(i).getBoolean("Value"));
                                //cb.setEnabled(false);
                                ll.addView(cb);
                            }

                            //add checkboxes
                            for(int i = 0; i < jsonArrayInquiryStageList.length(); i++) {
                                cb2 = new CheckBox(InquiryDetailsActivity.this);
                                cb2.setText(jsonArrayInquiryStageList.getJSONObject(i).getString("Inq_Stage"));
                                cb2.setId(i);
                                cb2.setClickable(false);
                                cb2.setChecked(jsonArrayInquiryStageList.getJSONObject(i).getBoolean("Value"));
                                //cb.setEnabled(false);
                                ll2.addView(cb2);
                            }

                            adapter = new InquiryFollowupAdapter(InquiryDetailsActivity.this, jsonArrayInquiryTranList);
                            recyclerView.setAdapter(adapter);

                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(InquiryDetailsActivity.this, "Error1 " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InquiryDetailsActivity.this, "Error2 " + e.toString());
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InquiryDetailsActivity.this, "Error3 "+ e.toString());
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(InquiryDetailsActivity.this, "Error4 " + status.getMessage());
                    if(status.getCode()== 401){

                        AlertDialog.Builder builder = new AlertDialog.Builder(InquiryDetailsActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Alert...");
                        builder.setMessage("Please login again...");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", then he is allowed to exit from application

                                Intent intent = new Intent(InquiryDetailsActivity.this, MainActivity.class);
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
