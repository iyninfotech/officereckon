package com.score3s.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class InvoiceGridFragment extends Fragment {
    public InvoiceGridFragment() {
    }

    InVoiceAdapter adapter;
    RecyclerView recyclerView;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;
    JSONArray jsonArray;
    double sum = 0;
    TextView tvTotal;
    LinearLayout TotalLayout;
    EditText edtSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoicegrid, container, false);
        recyclerView = view.findViewById(R.id.listViewinVoice);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TotalLayout = view.findViewById(R.id.total);
        edtSearch = view.findViewById(R.id.edtSearch);
        tvTotal = view.findViewById(R.id.tvTotal);
        preferencesUserAuthKey = getActivity().getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");



        if (NetworkUtils.isInternetAvailable(getActivity())) {

            getInvoice();

        } else {


            editorUserAuthKey.putString("SELECTVALUE","0");
            editorUserAuthKey.apply();
            ShowAlert.ShowAlertOkCancle(getActivity(),"No Internet !","Are Sure want Exit ?");
        }


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                JSONArray arrayTemplist= new JSONArray();
                String searchString =edtSearch.getText().toString().toLowerCase();
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    String ClientName = null,invoiceNo = null;
                    try {
                        ClientName = jsonArray.getJSONObject(i).getString("ClientName").toLowerCase();
                        invoiceNo = jsonArray.getJSONObject(i).getString("InvNo").toLowerCase();
                        if (ClientName.contains(searchString) || invoiceNo.contains(searchString) )
                        {
                            arrayTemplist.put(jsonArray.getJSONObject(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                adapter = new InVoiceAdapter(getActivity(),arrayTemplist);
                recyclerView.setAdapter(adapter);
            }

        });

        return view;
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

                            adapter = new InVoiceAdapter(getActivity(),jsonArray);
                            recyclerView.setAdapter(adapter);

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
                        adapter.notifyDataSetChanged();
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
        tvTotal.setText(Sum);

        TotalLayout.setVisibility(View.VISIBLE);

    }


}
