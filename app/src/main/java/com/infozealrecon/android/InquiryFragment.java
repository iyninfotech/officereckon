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
import com.infozealrecon.android.Adapter.InquiryAdapter;
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

public class InquiryFragment extends Fragment {

    ListView listView;
    InquiryAdapter adapter;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;

    String AuthKey;
    String UserName;
    RecyclerView recyclerView;
    EditText edtSearch;
    EditText edtItemSearchBy;
    JSONArray jsonArray;
    String SearchByStatus,SearchByCity;
    ArrayList<String> ITEMSEARCHBYSTATUS = new ArrayList<>();
    ArrayList<String> ITEMSEARCHBY = new ArrayList<>();
    Button btnSearchByStatus,btnSearchBy;
    public InquiryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inquiry, container, false);
        edtSearch = view.findViewById(R.id.edtSearch);

        preferencesUserAuthKey = getActivity().getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");
        UserName = preferencesUserAuthKey.getString("username", "");

        ITEMSEARCHBYSTATUS.add("All"); //A
        ITEMSEARCHBYSTATUS.add("Pending");//P
        ITEMSEARCHBYSTATUS.add("Confirm");//C
        ITEMSEARCHBYSTATUS.add("Lost");//L

        //start code Search by status

        final TextView selectItemSearchBystatus =  view.findViewById(R.id.selectItemSearchBystatus);

        selectItemSearchBystatus.setText("All");


        final SpinnerDialog spItemSearchByStatus;

        spItemSearchByStatus = new SpinnerDialog(getActivity(), ITEMSEARCHBYSTATUS,
                "Status");

        spItemSearchByStatus.setCancellable(true);
        spItemSearchByStatus.setShowKeyboard(false);

        spItemSearchByStatus.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {

                try {
                    selectItemSearchBystatus.setText(item);

                } catch (NullPointerException e) {
                    ShowAlert.ShowAlert(getActivity(), "Alert...", "Due to law internet item detail not found press OK and Re-Select Item ID from Item ID List.");
                }

            }
        });

        view.findViewById(R.id.LayoutItemSearchByStatus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spItemSearchByStatus.showSpinerDialog();
            }
        });


        //End code Search by status


        ITEMSEARCHBY.add("BY CITY");
        ITEMSEARCHBY.add("All Data");


        //start code Search by City
        edtItemSearchBy = view.findViewById(R.id.edtItemSearchBy);
        final TextView selectItemSearchBy =  view.findViewById(R.id.selectItemSearchBy);
        btnSearchBy = view.findViewById(R.id.btnSearchBy);
        selectItemSearchBy.setText("BY CITY");
        edtItemSearchBy.setHint("Please Enter " + selectItemSearchBy.getText());

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
                    SearchByStatus="";
                   if (selectItemSearchBystatus.getText().equals("All")){
                        SearchByStatus = "A";
                    }else if (selectItemSearchBystatus.getText().equals("Pending")){
                        SearchByStatus = "P";
                    }else if (selectItemSearchBystatus.getText().equals("Confirm")){
                       SearchByStatus = "C";
                    }else if (selectItemSearchBystatus.getText().equals("Lost")){
                       SearchByStatus = "L";
                    }

                    SearchByCity ="";
                    if (selectItemSearchBy.getText().equals("BY CITY")){

                        if (StringUtils.isBlank(edtItemSearchBy.getText().toString().trim())) {
                            edtItemSearchBy.setText("");
                            edtItemSearchBy.requestFocus();
                            ToastUtils.showErrorToast(getActivity(), "Please enter Value");
                            return;
                        } else {
                            SearchByCity = edtItemSearchBy.getText().toString().trim();
                        }
                    }else if (selectItemSearchBy.getText().equals("All Data")){
                        edtItemSearchBy.setText("");
                        SearchByCity="";
                    }

                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        try {
                            CustomProcessbar.showProcessBar(getActivity(), false, getString(R.string.please_wait));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        edtSearch.setText("");
                        getInquiryList(SearchByStatus,SearchByCity);

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




        recyclerView = view.findViewById(R.id.listViewInquiry);
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
                        String InquiryName = null, InquiryArea = null, InquiryNumber = null,InquiryCP = null,Inq_Code=null,Inq_No=null;
                        String Inq_Add1 = null, Inq_Add2 = null, Inq_Area = null,Inq_State = null,Inq_Mobile2=null,Inq_Mobile3=null;
                        String Inq_Status = null, Inq_Phone = null, Inq_Reference = null,Inq_Passon = null,Inq_Remark=null;
                        try {
                            Inq_Code = jsonArray.getJSONObject(i).getString("Inq_Code").toLowerCase();
                            Inq_No = jsonArray.getJSONObject(i).getString("Inq_No").toLowerCase();

                            InquiryName = jsonArray.getJSONObject(i).getString("Inq_Name").toLowerCase();
                            InquiryArea = jsonArray.getJSONObject(i).getString("Inq_City").toLowerCase();
                            InquiryCP = jsonArray.getJSONObject(i).getString("Inq_Contactperson").toLowerCase();
                            InquiryNumber = jsonArray.getJSONObject(i).getString("Inq_Mobile").toLowerCase();

                            Inq_Add1 = jsonArray.getJSONObject(i).getString("Inq_Add1").toLowerCase();
                            Inq_Add2 = jsonArray.getJSONObject(i).getString("Inq_Add2").toLowerCase();
                            Inq_Area = jsonArray.getJSONObject(i).getString("Inq_Area").toLowerCase();
                            Inq_State = jsonArray.getJSONObject(i).getString("Inq_State").toLowerCase();
                            Inq_Mobile2 = jsonArray.getJSONObject(i).getString("Inq_Mobile2").toLowerCase();
                            Inq_Mobile3 = jsonArray.getJSONObject(i).getString("Inq_Mobile3").toLowerCase();
                            Inq_Status = jsonArray.getJSONObject(i).getString("Inq_Status").toLowerCase();
                            Inq_Phone = jsonArray.getJSONObject(i).getString("Inq_Phone").toLowerCase();
                            Inq_Reference = jsonArray.getJSONObject(i).getString("Inq_Reference").toLowerCase();
                            Inq_Remark = jsonArray.getJSONObject(i).getString("Inq_Remark").toLowerCase();

                            if (InquiryName.contains(searchString) || InquiryArea.contains(searchString) || InquiryCP.contains(searchString) || InquiryNumber.contains(searchString)
                                    || Inq_Code.contains(searchString)|| Inq_No.contains(searchString)|| Inq_Add1.contains(searchString)
                                    || Inq_Add2.contains(searchString)|| Inq_Area.contains(searchString)|| Inq_State.contains(searchString)
                                    || Inq_Mobile2.contains(searchString)|| Inq_Mobile3.contains(searchString)|| Inq_Status.contains(searchString)
                                    || Inq_Phone.contains(searchString)|| Inq_Reference.contains(searchString)|| Inq_Remark.contains(searchString)) {
                                arrayTemplist.put(jsonArray.getJSONObject(i));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showErrorToast(getActivity(), "No Data in List");
                        }

                    }
                    adapter = new InquiryAdapter(getActivity(), arrayTemplist);
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

    private void getInquiryList(String SearchPatternType,String strDetail) {

        try {
            CustomProcessbar.showProcessBar(getActivity(), false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(getActivity());
        String url = APIURL.BASE_URL + APIURL.GETInquiryHead;
        Map<String, String> params = new HashMap<String, String>();
        params.put("Inquiry_Status", SearchPatternType);
        params.put("Inquiry_City", strDetail);

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

                            jsonArray = jRootObject.getJSONArray("InquiryHeadList");
                            adapter = new InquiryAdapter(getActivity(), jsonArray);
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
