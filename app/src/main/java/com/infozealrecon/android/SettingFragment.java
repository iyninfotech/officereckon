package com.infozealrecon.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.infozealrecon.android.Constant.APIURL;
import com.infozealrecon.android.SearchSpinnerCustom.OnSpinerItemClick;
import com.infozealrecon.android.SearchSpinnerCustom.SpinnerDialog;
import com.infozealrecon.android.asynctasks.CustomProcessbar;
import com.infozealrecon.android.asynctasks.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.infozealrecon.android.MainActivity.AUTHKEY;

public class SettingFragment extends Fragment {
    /*public SettingFragment() {
        // Required empty public constructor
    }*/
    private Handler handler;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;

    ArrayList<String> DIVISIONLIST = new ArrayList<>();
    ArrayList<String> ROUTELIST = new ArrayList<>();
    ArrayList<String> SALEMENLIST = new ArrayList<>();
    ArrayList<String> GODOWNLIST = new ArrayList<>();
    JSONObject jsonObjMRPDetails;
    SpinnerDialog Division, Route, Salemen, GodownSD, Client;
    JSONArray jsonArrayDivision, jsonArrayRoute, jsonArraySalesmen, jsonArrayGodown;
    JSONArray jsonArray = new JSONArray();
    TextView SelectedItemDivision, SelectedItemRoute, SelectedItemSaleman, SelectedItemGodown, SelectedItemClient;
    Button btnSubmit;
    JSONObject jsonObjectAddValues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        handler = new Handler();

        preferencesUserAuthKey = getActivity().getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");

        SelectedItemDivision = view.findViewById(R.id.tvDivision);
        SelectedItemRoute = view.findViewById(R.id.tvRoute);
        SelectedItemSaleman = view.findViewById(R.id.tvSaleman);
        SelectedItemGodown = view.findViewById(R.id.tvGodown);
        SelectedItemClient = view.findViewById(R.id.tvClient);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    if (SelectedItemDivision.getText().toString().trim().equals("Select Division")) {

                        ToastUtils.showErrorToast(getActivity(), "Please,select division ");

                        return;
                    } else if (SelectedItemRoute.getText().toString().trim().equals("Select Route")) {

                        ToastUtils.showErrorToast(getActivity(), "Please,select route ");
                        return;
                    } else if (SelectedItemClient.getText().toString().trim().equals("Select Client")) {

                        ToastUtils.showErrorToast(getActivity(), "Please,select client ");
                    } else if (SelectedItemSaleman.getText().toString().trim().equals("Select Salesman")) {

                        ToastUtils.showErrorToast(getActivity(), "Please,select salesman ");
                        return;
                    } else {
                        jsonObjectAddValues = new JSONObject();

                        for (int i = 0; i < jsonArrayDivision.length(); i++) {
                            try {
                                if (jsonArrayDivision.getJSONObject(i).getString("DivisionName").equals(SelectedItemDivision.getText().toString().trim())) {

                                    jsonObjectAddValues.put("DivId", jsonArrayDivision.getJSONObject(i).getString("Id"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int i = 0; i < jsonArrayRoute.length(); i++) {
                            try {
                                if (jsonArrayRoute.getJSONObject(i).getString("RouteName").equals(SelectedItemRoute.getText().toString().trim())) {

                                    jsonObjectAddValues.put("RouteId", jsonArrayRoute.getJSONObject(i).getString("Id"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int i = 0; i < jsonArraySalesmen.length(); i++) {
                            try {
                                if (jsonArraySalesmen.getJSONObject(i).getString("SalesmanName").equals(SelectedItemSaleman.getText().toString().trim())) {

                                    jsonObjectAddValues.put("SalesId", jsonArraySalesmen.getJSONObject(i).getString("Id"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int i = 0; i < jsonArrayGodown.length(); i++) {
                            try {
                                if (jsonArrayGodown.getJSONObject(i).getString("GodownName").equals(SelectedItemGodown.getText().toString().trim())) {

                                    jsonObjectAddValues.put("GodownId", jsonArrayGodown.getJSONObject(i).getString("Id"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        SaveData();
                    }
                } catch (Exception e) {
                    ToastUtils.showErrorToast(getActivity(), e.toString());
                }
            }
        });


        getDivision();

        return view;
    }

    private void getDivision() {

        try {
            CustomProcessbar.showProcessBar(getActivity(), false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(getActivity());
        String url = APIURL.BASE_URL + APIURL.DIVISION;
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
                            DIVISIONLIST.clear();
                            jsonArrayDivision = jRootObject.getJSONArray("Divisions");
                            Log.d("array", jsonArrayDivision.toString());
                            for (int i = 0; i < jsonArrayDivision.length(); i++) {
                                DIVISIONLIST.add(jsonArrayDivision.getJSONObject(i).getString("DivisionName"));
                            }
                            Division = new SpinnerDialog(getActivity(), DIVISIONLIST,
                                    "Select or Search Division");

                            Division.setCancellable(true);
                            Division.setShowKeyboard(false);

                            Division.bindOnSpinerListener(new OnSpinerItemClick() {
                                @Override
                                public void onClick(String item, int position) {
                                    SelectedItemDivision.setText(item);
                                }
                            });


                            getActivity().findViewById(R.id.layoutDivision).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Division.showSpinerDialog();
                                }
                            });

                            getRoute();

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
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(getActivity(), "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void getRoute() {

        AQuery aq;
        aq = new AQuery(getActivity());
        String url = APIURL.BASE_URL + APIURL.ROUTE;
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

                            jsonArrayRoute = jRootObject.getJSONArray("Routes");
                            for (int i = 0; i < jsonArrayRoute.length(); i++) {
                                ROUTELIST.add(jsonArrayRoute.getJSONObject(i).getString("RouteName"));
                            }
                            Route = new SpinnerDialog(getActivity(), ROUTELIST,
                                    "Select or Search Route");

                            Route.setCancellable(true);
                            Route.setShowKeyboard(false);

                            Route.bindOnSpinerListener(new OnSpinerItemClick() {
                                @Override
                                public void onClick(String item, int position) {
                                    SelectedItemRoute.setText(item);
                                }
                            });

                            getActivity().findViewById(R.id.layoutRoute).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Route.showSpinerDialog();
                                }
                            });
                            getSalemen();

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
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(getActivity(), "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void getSalemen() {

        AQuery aq;
        aq = new AQuery(getActivity());
        String url = APIURL.BASE_URL + APIURL.SALESMEN;
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
                            jsonArraySalesmen = jRootObject.getJSONArray("Salesmans");
                            for (int i = 0; i < jsonArraySalesmen.length(); i++) {
                                SALEMENLIST.add(jsonArraySalesmen.getJSONObject(i).getString("SalesmanName"));
                            }
                            Salemen = new SpinnerDialog(getActivity(), SALEMENLIST,
                                    "Select or Search Saleman");

                            Salemen.setCancellable(true);
                            Salemen.setShowKeyboard(false);

                            Salemen.bindOnSpinerListener(new OnSpinerItemClick() {
                                @Override
                                public void onClick(String item, int position) {
                                    SelectedItemSaleman.setText(item);
                                }
                            });

                            getActivity().findViewById(R.id.layoutSaleman).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Salemen.showSpinerDialog();
                                }
                            });
                            getGodown();

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
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(getActivity(), "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void getGodown() {

        AQuery aq;
        aq = new AQuery(getActivity());
        String url = APIURL.BASE_URL + APIURL.GODOWN;
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
                            jsonArrayGodown = jRootObject.getJSONArray("Godowns");
                            for (int i = 0; i < jsonArrayGodown.length(); i++) {
                                GODOWNLIST.add(jsonArrayGodown.getJSONObject(i).getString("GodownName"));
                            }
                            GodownSD = new SpinnerDialog(getActivity(), GODOWNLIST,
                                    "Select or Search Godown");

                            GodownSD.setCancellable(true);
                            GodownSD.setShowKeyboard(false);

                            GodownSD.bindOnSpinerListener(new OnSpinerItemClick() {
                                @Override
                                public void onClick(String item, int position) {
                                    SelectedItemGodown.setText(item);
                                }
                            });

                            getActivity().findViewById(R.id.layoutGodown).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GodownSD.showSpinerDialog();
                                }
                            });

                            CustomProcessbar.hideProcessBar();
                            ToastUtils.showErrorToast(getActivity(), "this step ok ");

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
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(getActivity(), "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void SaveData() {
        try {
            CustomProcessbar.showProcessBar(getActivity(), false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AQuery aq;
        aq = new AQuery(getActivity());
        String url = APIURL.BASE_URL + APIURL.POSTSETTING;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);
        params.put("InvoiceHeads", String.valueOf(jsonObjectAddValues));

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {
                            editorUserAuthKey.putString("SELECTVALUE", "3");
                            editorUserAuthKey.apply();



                            /*Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                            startActivity(intent);
                            CustomProcessbar.hideProcessBar();
                            ToastUtils.showErrorToast(AddInvoiceActivity.this,  jRootObject.getString("Status"));*/


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
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(getActivity(), "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

}
