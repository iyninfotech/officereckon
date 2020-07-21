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
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.score3s.android.Adapter.OutletAdapter;
import com.score3s.android.Constant.APIURL;
import com.score3s.android.asynctasks.CustomProcessbar;
import com.score3s.android.asynctasks.NetworkUtils;
import com.score3s.android.asynctasks.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.score3s.android.MainActivity.AUTHKEY;

public class OutletFragment extends Fragment {

    ListView listView;
    OutletAdapter adapter;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;
    RecyclerView recyclerView;
    EditText edtSearch;
    JSONArray jsonArray;
    public OutletFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_outlet, container, false);
        edtSearch = view.findViewById(R.id.edtSearch);

        preferencesUserAuthKey = getActivity().getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");

        if (NetworkUtils.isInternetAvailable(getActivity())) {

            getOutlet();

        } else {

            NetworkUtils.showNetworkAlertDialog(getActivity());
        }



        recyclerView = view.findViewById(R.id.listViewOutlet);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                JSONArray arrayTemplist= new JSONArray();
                String searchString =edtSearch.getText().toString().toLowerCase();
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    String OutletName = null,OutletArea = null;
                    try {
                        OutletName = jsonArray.getJSONObject(i).getString("OutletName").toLowerCase();
                        OutletArea = jsonArray.getJSONObject(i).getString("Area").toLowerCase();
                        if (OutletName.contains(searchString) || OutletArea.contains(searchString))
                        {
                            arrayTemplist.put(jsonArray.getJSONObject(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                adapter = new OutletAdapter(getActivity(),arrayTemplist);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        return view;
    }


    private void getOutlet() {

        try {
            CustomProcessbar.showProcessBar(getActivity(), false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(getActivity());
        String url = APIURL.BASE_URL + APIURL.OUTLET;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);

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

                            jsonArray = jRootObject.getJSONArray("Outlets");
                            adapter = new OutletAdapter(getActivity(),jsonArray);
                            recyclerView.setAdapter(adapter);


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
