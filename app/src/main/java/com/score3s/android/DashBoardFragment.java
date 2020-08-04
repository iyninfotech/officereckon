package com.score3s.android;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.score3s.android.Adapter.InVoiceAdapter;
import com.score3s.android.Constant.APIURL;
import com.score3s.android.asynctasks.CustomProcessbar;
import com.score3s.android.asynctasks.NetworkUtils;
import com.score3s.android.asynctasks.ToastUtils;
import com.score3s.android.fontStyle.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.score3s.android.MainActivity.AUTHKEY;
import static com.score3s.android.AddInvoiceActivity.DEFHEADDATA;

public class DashBoardFragment extends Fragment {
    public DashBoardFragment() {
        // Required empty public constructor
    }

    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey,FullName;
    JSONObject jsonObject;
    JSONArray jsonArray;
    double sum = 0;
    int totalInv= 0;
    TextView tvUserName,tvTotalSalesAmt,tvTotalInvoice,tvDevloperInfoDetails,AppVersionInfo;
    SharedPreferences preferencesDEFHEADDATA;
    SharedPreferences.Editor editorDEFHEADDATA;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        preferencesUserAuthKey = getActivity().getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();

        preferencesDEFHEADDATA = getActivity().getSharedPreferences(DEFHEADDATA, MODE_PRIVATE);
        editorDEFHEADDATA = preferencesDEFHEADDATA.edit();

        AuthKey = preferencesUserAuthKey.getString("auth", "");
        FullName = preferencesUserAuthKey.getString("FullName", "");


        tvUserName = view.findViewById(R.id.tvUserName);
        tvTotalSalesAmt = view.findViewById(R.id.tvTotalSalesAmt);
        tvTotalInvoice = view.findViewById(R.id.tvTotalInvoice);
        tvDevloperInfoDetails = view.findViewById(R.id.tvDevloperInfoDetails);
        AppVersionInfo = view.findViewById(R.id.AppVersionInfo);


        if (NetworkUtils.isInternetAvailable(getActivity())) {

            tvUserName.setText("Hello, " + FullName);
            String AboutDevloper = " <i> Devloped By </i>  <h4> Infozeal eSolutions Private Limited </h4> Email Id = support@infozeal.co.in  <br/> Phone No. = +91 (79) 49117200 <br/>Website = www.infozeal.co.in";
            tvDevloperInfoDetails.setText(Html.fromHtml(AboutDevloper));
            getVersionInfo();
            //getDashboard();

        } else {

            ShowAlert.ShowAlertOkCancle(getActivity(),"No Internet !","Try Again ?");
        }

        return view;
    }

    //get the current version number and name
    private void getVersionInfo() {
        String versionName = null;
        int versionCode = -1;
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionName = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        AppVersionInfo.setText(String.format("App Version :: %s", versionName));
        getInvoice();
    }

    private void getInvoice() {

        try {
            CustomProcessbar.showProcessBar(getActivity(), false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(getActivity());
        String url = APIURL.BASE_URL + APIURL.InVoice;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {

                            jsonArray = jRootObject.getJSONArray("Invoices");

                            calculateSum();
                            CustomProcessbar.hideProcessBar();


                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(getActivity(), "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(getActivity(), "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(getActivity(), "Error ");
                    }
                    finally {

                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(getActivity(), "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void calculateSum() {
        sum = 0;

        totalInv = jsonArray.length();
        tvTotalInvoice.setText(String.format("%d",totalInv));

        for(int i = 0; i <jsonArray.length();i++){
            try {
                JSONObject jobj = jsonArray.getJSONObject(i);
                double amount = Double.parseDouble(jobj.getString("Amount"));
                sum = sum + amount;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String Sum = String.valueOf(sum);
        tvTotalSalesAmt.setText(Sum);
    }

    private void getDashboard() {

        try {
            CustomProcessbar.showProcessBar(getActivity(), false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AQuery aq;
        aq = new AQuery(getActivity());
        String url = APIURL.BASE_URL + APIURL.DASHBOARD;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);
        params.put("UserName", FullName);
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

                            jsonObject = jRootObject.getJSONObject("Dashboards");
                            /*tvTotalCalls.setText(jsonObject.getString("TotalCalls"));
                            tvProdectiveCalls.setText(jsonObject.getString("ProductiveCalls"));
                            tvLineSold.setText(jsonObject.getString("LineSold"));
                            tvTarget.setText(jsonObject.getString("Target"));
                            tvAchivement.setText(jsonObject.getString("Achivement"));*/

                        } else {
                            ToastUtils.showErrorToast(getActivity(), "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(getActivity(), "Error ");
                    } catch (Exception e) {
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(getActivity(), "Error ");
                    }
                } else {
                    ToastUtils.showErrorToast(getActivity(), "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

}
