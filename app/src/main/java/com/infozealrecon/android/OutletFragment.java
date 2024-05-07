package com.infozealrecon.android;

import static android.content.Context.MODE_PRIVATE;
import static com.infozealrecon.android.MainActivity.AUTHKEY;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.infozealrecon.android.Adapter.OutletAdapter;
import com.infozealrecon.android.Constant.APIURL;
import com.infozealrecon.android.SearchSpinnerCustom.OnSpinerItemClick;
import com.infozealrecon.android.SearchSpinnerCustom.SpinnerDialog;
import com.infozealrecon.android.asynctasks.CustomProcessbar;
import com.infozealrecon.android.asynctasks.NetworkUtils;
import com.infozealrecon.android.asynctasks.StringUtils;
import com.infozealrecon.android.asynctasks.ToastUtils;
import com.infozealrecon.android.fontStyle.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OutletFragment extends Fragment {

    ListView listView;
    OutletAdapter adapter;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;
    RecyclerView recyclerView;
    EditText edtSearch;
    EditText edtItemSearchBy;
    JSONArray jsonArray;
    String SearchType,SearchIdCode;
    ArrayList<String> ITEMSEARCHBY = new ArrayList<>();
    Button btnSearchBy;
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

       /* ITEMSEARCHBY.add("Start With"); //SW
        ITEMSEARCHBY.add("End With"); //EW*/

        ITEMSEARCHBY.add("All Data"); //""
        ITEMSEARCHBY.add("isContains"); //ALL

        //start code Search by
        edtItemSearchBy = view.findViewById(R.id.edtItemSearchBy);
        final TextView selectItemSearchBy =  view.findViewById(R.id.selectItemSearchBy);
        btnSearchBy = view.findViewById(R.id.btnSearchBy);
        selectItemSearchBy.setText("All Data");
        edtItemSearchBy.setHint("");
        edtItemSearchBy.setEnabled(false);

        final SpinnerDialog spItemSearchBy;

        spItemSearchBy = new SpinnerDialog(getActivity(), ITEMSEARCHBY,
                "Search Type");

        spItemSearchBy.setCancellable(true);
        spItemSearchBy.setShowKeyboard(false);

        spItemSearchBy.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {

                try {
                    edtSearch.setText("");
                    selectItemSearchBy.setText(item);
                    if(item.equals("All Data"))
                    {
                        edtItemSearchBy.setHint("");
                        edtItemSearchBy.setEnabled(false);
                    }else {
                        edtItemSearchBy.setEnabled(true);
                        edtItemSearchBy.setHint("Please Enter " + item.trim());
                    }

                } catch (NullPointerException e) {

                    ShowAlert.ShowAlert(getActivity(), "Alert...", "Due to law internet item detail not found press OK and Re-Select Item ID from Item ID List.");
                }

            }
        });

        view.findViewById(R.id.LayoutItemSearchBy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spItemSearchBy.showSpinerDialog();
            }
        });

        edtItemSearchBy.requestFocus();

        btnSearchBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    SearchType=""; SearchIdCode ="";
                   /* if (selectItemSearchBy.getText().equals("Start With")){

                        SearchType = "SW";

                        if (StringUtils.isBlank(edtItemSearchBy.getText().toString().trim())) {
                            edtItemSearchBy.requestFocus();
                            ToastUtils.showErrorToast(getActivity(), "Please enter Value");
                        } else {
                            SearchIdCode = edtItemSearchBy.getText().toString().trim();
                        }
                    }else if (selectItemSearchBy.getText().equals("End With")){

                        SearchType = "EW";

                        if (StringUtils.isBlank(edtItemSearchBy.getText().toString().trim())) {
                            edtItemSearchBy.requestFocus();
                            ToastUtils.showErrorToast(getActivity(), "Please enter Value");
                        } else {
                            SearchIdCode = edtItemSearchBy.getText().toString().trim();
                        }
                    }else */
                    if (selectItemSearchBy.getText().equals("isContains")){

                        SearchType = "ALL";

                        if (StringUtils.isBlank(edtItemSearchBy.getText().toString().trim())) {
                            edtItemSearchBy.setText("");
                            edtItemSearchBy.requestFocus();
                            ToastUtils.showErrorToast(getActivity(), "Please enter Value");
                            return;
                        } else {
                            SearchIdCode = edtItemSearchBy.getText().toString().trim();
                        }
                    }else if (selectItemSearchBy.getText().equals("All Data")){
                        edtItemSearchBy.setText("");
                        SearchType = "";
                        SearchIdCode="";
                    }

                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        try {
                            CustomProcessbar.showProcessBar(getActivity(), false, getString(R.string.please_wait));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        edtSearch.setText("");
                        getClintList(SearchType,SearchIdCode);

                    } else {

                        NetworkUtils.showNetworkAlertDialog(getActivity());
                    }

                } catch (NullPointerException e) {
                    //System.out.print();
                    ShowAlert.ShowAlert(getActivity(), "Alert...", "Due to law internet item detail not found press OK and Re-Select Item ID from Item ID List.");
                }


            }
        });
        //End code Search by




        recyclerView = view.findViewById(R.id.listViewOutlet);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    JSONArray arrayTemplist = new JSONArray();
                    String searchString = edtSearch.getText().toString().toLowerCase();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String Party_Key = null, OutletName = null, OutletArea = null, OutletNumber = null,OutletCP = null;
                        String Party_Add = null,Party_Add1 = null,Party_Area = null,Party_Stat = null,Party_OtherInfo1=null,Party_OtherInfo2=null,Remark1=null,Remark2=null,Remark3=null,Party_StatusRemark1=null,Party_SpclRemark=null,Party_Status=null;
                        try {
                            OutletName = jsonArray.getJSONObject(i).getString("Party_Name").toLowerCase();
                            OutletArea = jsonArray.getJSONObject(i).getString("Party_City").toLowerCase();
                            OutletCP = jsonArray.getJSONObject(i).getString("Party_Cp").toLowerCase();
                            OutletNumber = jsonArray.getJSONObject(i).getString("Party_Mobno").toLowerCase();

                            Party_Add = jsonArray.getJSONObject(i).getString("Party_Add").toLowerCase();
                            Party_Add1 = jsonArray.getJSONObject(i).getString("Party_Add1").toLowerCase();
                            Party_Area = jsonArray.getJSONObject(i).getString("Party_Area").toLowerCase();
                            Party_Stat = jsonArray.getJSONObject(i).getString("Party_Stat").toLowerCase();

                            Party_OtherInfo1 = jsonArray.getJSONObject(i).getString("Party_OtherInfo1").toLowerCase();
                            Party_OtherInfo2 = jsonArray.getJSONObject(i).getString("Party_OtherInfo2").toLowerCase();
                            Remark1 = jsonArray.getJSONObject(i).getString("Remark1").toLowerCase();
                            Remark2 = jsonArray.getJSONObject(i).getString("Remark2").toLowerCase();
                            Remark3 = jsonArray.getJSONObject(i).getString("Remark3").toLowerCase();

                            Party_StatusRemark1 = jsonArray.getJSONObject(i).getString("Party_StatusRemark1").toLowerCase();
                            Party_SpclRemark = jsonArray.getJSONObject(i).getString("Party_SpclRemark").toLowerCase();
                            Party_Status = jsonArray.getJSONObject(i).getString("Party_Status").toLowerCase();
                            Party_Key = jsonArray.getJSONObject(i).getString("Party_Key").toLowerCase();

                            if (Party_Key.contains(searchString) || OutletName.contains(searchString) || OutletArea.contains(searchString) || OutletCP.contains(searchString) || OutletNumber.contains(searchString)
                                    || Party_Add.contains(searchString) || Party_Add1.contains(searchString)  || Party_Area.contains(searchString)  || Party_Stat.contains(searchString)
                                    || Party_OtherInfo1.contains(searchString) || Party_OtherInfo2.contains(searchString) || Remark1.contains(searchString) || Remark2.contains(searchString)
                                    || Remark3.contains(searchString) || Party_StatusRemark1.contains(searchString) || Party_SpclRemark.contains(searchString) || Party_Status.contains(searchString)
                            ) {
                                arrayTemplist.put(jsonArray.getJSONObject(i));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showErrorToast(getActivity(), "No Data in List");
                        }

                    }
                    adapter = new OutletAdapter(getActivity(), arrayTemplist);
                    recyclerView.setAdapter(adapter);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        return view;
    }

    private void getClintList(String SearchPatternType,String strDetail) {

        try {
            CustomProcessbar.showProcessBar(getActivity(), false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(getActivity());
        String url = APIURL.BASE_URL + APIURL.CLIENT;
        Map<String, String> params = new HashMap<String, String>();
        params.put("SearchPattern", SearchPatternType);
        params.put("Party_Find", strDetail);

        aq.ajax(url,params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("Success")) {
                            try {
                                CustomProcessbar.showProcessBar(getActivity(), false, getString(R.string.please_wait));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            jsonArray=null;

                            jsonArray = jRootObject.getJSONArray("PartyMasterList");
                            adapter = new OutletAdapter(getActivity(), jsonArray);
                            recyclerView.setAdapter(adapter);
                            // ShowAlert.ShowAlert(getActivity(), "Alert...", jsonArray.toString());
                            // ToastUtils.showErrorToast(getActivity(), "Error " + jsonArray);
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
                    } finally {

                    }
                } else {
                    CustomProcessbar.hideProcessBar();
                    ToastUtils.showErrorToast(getActivity(), "Error4 " + status.getMessage());
                    if(status.getCode()== 401){

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false);
                        builder.setTitle("Alert...");
                        builder.setMessage("Please login again...");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", then he is allowed to exit from application

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
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
