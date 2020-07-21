package com.score3s.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.score3s.android.Constant.APIURL;
import com.score3s.android.asynctasks.CustomProcessbar;
import com.score3s.android.asynctasks.NetworkUtils;
import com.score3s.android.asynctasks.ToastUtils;
import com.score3s.android.fontStyle.ShowAlert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.score3s.android.MainActivity.AUTHKEY;

public class DashBoardFragment extends Fragment {
    public DashBoardFragment() {
        // Required empty public constructor
    }

    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;
    JSONObject jsonObject;
    TextView tvTotalCalls,tvProdectiveCalls,tvLineSold,tvTarget,tvAchivement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        preferencesUserAuthKey = getActivity().getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");

        tvTotalCalls = view.findViewById(R.id.tvTotalCalls);
        tvProdectiveCalls = view.findViewById(R.id.tvprodectiveCalls);
        tvLineSold = view.findViewById(R.id.tvLineSold);
        tvTarget = view.findViewById(R.id.tvTarget);
        tvAchivement = view.findViewById(R.id.tvAchivement);


        if (NetworkUtils.isInternetAvailable(getActivity())) {

            getDashboard();

        } else {

            ShowAlert.ShowAlertOkCancle(getActivity(),"No Internet !","Try Again ?");
        }

        return view;
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
        params.put("UserName", "Mohit");
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
                            tvTotalCalls.setText(jsonObject.getString("TotalCalls"));
                            tvProdectiveCalls.setText(jsonObject.getString("ProductiveCalls"));
                            tvLineSold.setText(jsonObject.getString("LineSold"));
                            tvTarget.setText(jsonObject.getString("Target"));
                            tvAchivement.setText(jsonObject.getString("Achivement"));

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
