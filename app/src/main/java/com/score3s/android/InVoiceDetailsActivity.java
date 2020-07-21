package com.score3s.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.score3s.android.Adapter.AdapterInvoiceDetails;
import com.score3s.android.Constant.APIURL;
import com.score3s.android.DateWheel.DatePickerPopWin;
import com.score3s.android.SearchSpinnerCustom.OnSpinerItemClick;
import com.score3s.android.SearchSpinnerCustom.SpinnerDialog;
import com.score3s.android.Validations.CheckValidate;
import com.score3s.android.Validations.InputFilterDecimal;
import com.score3s.android.asynctasks.ClickListener;
import com.score3s.android.asynctasks.CustomProcessbar;
import com.score3s.android.asynctasks.NetworkUtils;
import com.score3s.android.asynctasks.ToastUtils;
import com.score3s.android.fontStyle.ShowAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.score3s.android.MainActivity.AUTHKEY;

public class InVoiceDetailsActivity extends Activity {


    ImageView btnBack,btnCancel;
    TextView tvTotalAmt, tvPointValue;
    TextView tvDate;
    AdapterInvoiceDetails adapter;

    double AltUnitQty,PrimaryUnitQty,TotalQty,Gross,cgstP = 0,sgstP = 0,igstP = 0,Rate,StockQty,CurrentQty;
    double DiscPer = 0,Disc = 0 ,DiscIIPer = 0,DiscII = 0,DiscIIIPer = 0,DiscIII = 0,TotalDisc = 0,OtherPer = 0,Other = 0,OtherIIPer = 0,OtherII = 0,AltUnitConversion=0  ;
    int UnitDecimalPlaces = 0,UDP=0,UDA=0;
    ArrayList<String> DIVISIONLIST = new ArrayList<>();
    ArrayList<String> ROUTELIST = new ArrayList<>();
    ArrayList<String> SALEMENLIST = new ArrayList<>();
    ArrayList<String> CLIENTCODELIST = new ArrayList<>();
    ArrayList<String> ITEMLIST = new ArrayList<>();
    ArrayList<String> ITEMIDLIST = new ArrayList<>();
    ArrayList<String> MRPLIST = new ArrayList<>();
    JSONArray jsonArray = new JSONArray();
    JSONObject jsonObject1 = new JSONObject();
    JSONObject jsonObject2 = new JSONObject();
    JSONObject jsonObjMRPDetails;

    Button btnUpdate,btnAdd,btnOk,btnMGDisc;
    SpinnerDialog Division, Route, Salemen, Client;
    JSONArray jsonArrayDivision, jsonArrayRoute, jsonArraySalesmen, jsonArrayClient, jsonArrayItem, jsonArrayMRP,jsonArrayInvoiceDetails,jsonArrayUnits,jsonArrayAlternetUnit;

    JSONObject jobjPerticularPosition;
    JSONObject jsonObjectAddValues;

    double sum = 0;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;
    TextView SelectedItemDivision, SelectedItemRoute, SelectedItemSaleman, SelectedItemClient;
    int edtInvHeadId ;
    ScrollView ScrollView;
    RecyclerView recyclerView;

    EditText edtAltUnitQty;
    EditText edtPrimaryUnitQty;
    EditText edtTotalQty;
    EditText edtFreeQty ;
    EditText edtRate ;
    EditText edtStock ;
    EditText edtGross ;
    EditText edtDiscPer;
    EditText edtDisc;
    EditText edtDiscIIPer;
    EditText edtDiscII;
    EditText edtDiscIIIPer;
    EditText edtDiscIII;
    EditText edtTotalDisc;
    EditText edtOtherPer;
    EditText edtOther;
    EditText edtOtherIIPer;
    EditText edtOtherII;
    EditText edtCGSTACID;
    EditText edtCGSTP;
    EditText edtCGST;
    EditText edtSGSTACID;
    EditText edtSGSTP;
    EditText edtSGST;
    EditText edtIGSTACID;
    EditText edtIGSTP;
    EditText edtIGST;
    EditText edtAmount;

    String  SetItemName = "" , tmpSetItemId = "";
    String  SetPrimaryUnit = "";
    String  SetAltUnit = "";
    String ITEMID = "0";
    String DivisionID = "0";
    String RoutID = "0";
    String SelectedItemACGSTType;

    private int GetUnitDecimalPlaces(String Unit)
    {
        for (int i = 0; i < jsonArrayUnits.length(); i++) {
            try {
                if (jsonArrayUnits.getJSONObject(i).getString("UnitAlias").equals(Unit.trim())) {

                    UnitDecimalPlaces = CheckValidate.checkemptyintger(jsonArrayUnits.getJSONObject(i).getString("UnitDecimalPlaces"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return UnitDecimalPlaces;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_details);

        tvDate = findViewById(R.id.selectDate);
        tvTotalAmt = findViewById(R.id.totalamt);
        tvPointValue = findViewById(R.id.tvPointValue);
        preferencesUserAuthKey = getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(android.R.animator.fade_out, android.R.animator.fade_in);
                finish();
            }
        });
        btnUpdate = findViewById(R.id.btnUpdate);

        if(getIntent().getStringExtra("btnType").equals("ViewOnly")){

            btnUpdate.setText("View Mode");
            btnUpdate.setEnabled(false);
            btnAdd.setVisibility(View.GONE);

        }else
        {
            btnUpdate.setEnabled(true);
            btnAdd.setVisibility(View.VISIBLE);
        }
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    if (SelectedItemDivision.getText().toString().trim().equals("Select Division")) {
                        ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Must select division.");
                        return;
                    }
                    else if (SelectedItemRoute.getText().toString().trim().equals("Select Route")) {
                        ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Must select route.");
                        return;
                    }
                    else if (SelectedItemClient.getText().toString().trim().equals("Select Client")) {
                        ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Must select client/outlet.");
                        return;
                    }
                    else if (SelectedItemSaleman.getText().toString().trim().equals("Select Salesman")) {
                        ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Must select salesman.");
                        return;
                    }
                    else
                    {

                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(InVoiceDetailsActivity.this);
                    final View mView = LayoutInflater.from(InVoiceDetailsActivity.this).inflate(R.layout.editable_invoice_list, null);
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();

                    edtAltUnitQty = mView.findViewById(R.id.edtAltUnitQty);
                    edtPrimaryUnitQty = mView.findViewById(R.id.edtPrimaryUnitQty);
                    edtTotalQty = mView.findViewById(R.id.edtTotalQty);
                    edtFreeQty = mView.findViewById(R.id.edtFreeQty);
                    edtRate = mView.findViewById(R.id.edtRate);
                    edtStock = mView.findViewById(R.id.edtStock);
                    edtGross = mView.findViewById(R.id.edtGross);
                    edtDiscPer = mView.findViewById(R.id.edtDiscPer);
                    edtDisc = mView.findViewById(R.id.edtDisc);
                    edtDiscIIPer = mView.findViewById(R.id.edtDiscIIPer);
                    edtDiscII = mView.findViewById(R.id.edtDiscII);
                    edtDiscIIIPer = mView.findViewById(R.id.edtDiscIIIPer);
                    edtDiscIII = mView.findViewById(R.id.edtDiscIII);
                    edtTotalDisc = mView.findViewById(R.id.edtTotalDisc);
                    edtOtherPer = mView.findViewById(R.id.edtOtherPer);
                    edtOther = mView.findViewById(R.id.edtOther);
                    edtOtherIIPer = mView.findViewById(R.id.edtOtherIIPer);
                    edtOtherII = mView.findViewById(R.id.edtOtherII);
                    edtCGSTACID = mView.findViewById(R.id.edtCGSTACID);
                    edtCGSTP = mView.findViewById(R.id.edtCGSTP);
                    edtCGST = mView.findViewById(R.id.edtCGST);
                    edtSGSTACID = mView.findViewById(R.id.edtSGSTACID);
                    edtSGSTP = mView.findViewById(R.id.edtSGSTP);
                    edtSGST = mView.findViewById(R.id.edtSGST);
                    edtIGSTACID = mView.findViewById(R.id.edtIGSTACID);
                    edtIGSTP = mView.findViewById(R.id.edtIGSTP);
                    edtIGST = mView.findViewById(R.id.edtIGST);
                    edtAmount = mView.findViewById(R.id.edtAmount);
                    btnOk = mView.findViewById(R.id.btnOk);
                    btnMGDisc = mView.findViewById(R.id.btnMGDisc);
                    btnCancel = mView.findViewById(R.id.btnCancel);

                        edtRate.setFilters(new InputFilter[]{new InputFilterDecimal(12,5)});
                        edtDiscPer.setFilters(new InputFilter[]{new InputFilterDecimal(2,2)});
                        edtDisc.setFilters(new InputFilter[]{new InputFilterDecimal(12,2)});
                        edtDiscIIPer.setFilters(new InputFilter[]{new InputFilterDecimal(2,2)});
                        edtDiscII.setFilters(new InputFilter[]{new InputFilterDecimal(12,2)});
                        edtDiscIIIPer.setFilters(new InputFilter[]{new InputFilterDecimal(2,2)});
                        edtDiscIII.setFilters(new InputFilter[]{new InputFilterDecimal(12,2)});
                        edtOtherPer.setFilters(new InputFilter[]{new InputFilterDecimal(2,2)});
                        edtOther.setFilters(new InputFilter[]{new InputFilterDecimal(12,2)});
                        edtOtherIIPer.setFilters(new InputFilter[]{new InputFilterDecimal(2,2)});
                        edtOtherII.setFilters(new InputFilter[]{new InputFilterDecimal(12,2)});

                    ClearControl();
                    edtPrimaryUnitQty.setText("");
                    edtAltUnitQty.setText("");
                    edtFreeQty.setText("");

                    TextView title = mView.findViewById(R.id.titlePopup);
                    final TextView SelectedItem = mView.findViewById(R.id.selectItem);
                    SelectedItem.setText("Select Item");

                    final TextView  SelectedAltUnit = mView.findViewById(R.id.selectAltUnit);
                    SelectedAltUnit.setText("");

                    final TextView SelectedPrimaryUnit = mView.findViewById(R.id.selectPrimaryUnit);
                    SelectedPrimaryUnit.setText("PCS");

                    final TextView SelectedMRP = mView.findViewById(R.id.selectMRP);
                    final SpinnerDialog spMRP;
                    final ArrayList<String> ITEMIDARRAY = new ArrayList<>();
                    final ArrayList<String> ALTUNITARRAY = new ArrayList<>();

                    final SpinnerDialog spItem;

                        //ItemCode Code Start

                        final TextView SelectedItemId = mView.findViewById(R.id.selectItemId);
                        SelectedItemId.setText("Item ID");
                        final SpinnerDialog spItemId;

                        spItemId = new SpinnerDialog(InVoiceDetailsActivity.this, ITEMIDLIST,
                                "Select or Search Item ID");

                        spItemId.setCancellable(true);
                        spItemId.setShowKeyboard(false);

                        spItemId.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(String item, int position) {

                                ITEMIDARRAY.clear();
                                ALTUNITARRAY.clear();
                                ClearControl();
                                edtPrimaryUnitQty.setText("");
                                edtAltUnitQty.setText("");
                                edtFreeQty.setText("");
                                PrimaryUnitQty = 0;
                                if (null != item && item.length() > 0 )
                                {
                                    int endIndex = item.lastIndexOf(" || ");
                                    if (endIndex != -1)
                                    {
                                        tmpSetItemId = item.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
                                    }
                                }

                                SelectedItemId.setText(tmpSetItemId);

                                for (int i = 0; i < jsonArrayItem.length(); i++) {
                                    try {
                                        if (jsonArrayItem.getJSONObject(i).getString("Id").equals(SelectedItemId.getText().toString())) {
                                            ITEMID = jsonArrayItem.getJSONObject(i).getString("Id");
                                            SetItemName = jsonArrayItem.getJSONObject(i).getString("ItemName");
                                            SetPrimaryUnit = jsonArrayItem.getJSONObject(i).getString("Unit");
                                            SetAltUnit = jsonArrayItem.getJSONObject(i).getString("SaleUnit");

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                SelectedItem.setText(SetItemName);

                                if(CheckValidate.checkemptystring(SetPrimaryUnit)  != "")
                                {
                                    SelectedPrimaryUnit.setText(CheckValidate.checkemptystring(SetPrimaryUnit));
                                }

                                if(CheckValidate.checkemptystring(SetAltUnit)  == "")
                                {

                                    SelectedAltUnit.setText("");
                                    SelectedAltUnit.setEnabled(false);
                                    edtAltUnitQty.setEnabled(false);

                                }
                                else
                                {
                                    // SelectedAltUnit.setText(CheckValidate.checkemptystring(SetAltUnit));
                                    SelectedAltUnit.setEnabled(true);
                                    edtAltUnitQty.setEnabled(true);

                                    for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                                        try {
                                            if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID)) {

                                                if(!jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").isEmpty() || !jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals("null"))
                                                {
                                                    ALTUNITARRAY.add(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias"));
                                                }

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if(ALTUNITARRAY.size() > 0)
                                    {
                                        SelectedAltUnit.setText(CheckValidate.checkemptystring(SetAltUnit));

                                        for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                                            try {
                                                if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID) && jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals(SelectedAltUnit.getText().toString())) {

                                                    AltUnitConversion = CheckValidate.checkemptyDouble(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitConversion"));

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    else
                                    {
                                        AltUnitConversion = 0;
                                    }


                                }

                                for (int i = 0; i < jsonArrayMRP.length(); i++) {
                                    try {
                                        if (jsonArrayMRP.getJSONObject(i).getString("ItemId").equals(ITEMID) && CheckValidate.checkemptyDouble(jsonArrayMRP.getJSONObject(i).getString("Stock")) > 0) {

                                            if(!jsonArrayMRP.getJSONObject(i).getString("ItemMRP").isEmpty() || !jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals("null"))
                                            {
                                                ITEMIDARRAY.add(jsonArrayMRP.getJSONObject(i).getString("ItemMRP"));
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(ITEMIDARRAY.size() > 0)
                                {
                                    SelectedMRP.setText(ITEMIDARRAY.get(0));
                                }
                                else
                                {
                                    Toast.makeText(InVoiceDetailsActivity.this, "No stock available", Toast.LENGTH_LONG).show();
                                    SelectedMRP.setText("");
                                    edtRate.setError(null);
                                    return;
                                }

                                if(SelectedPrimaryUnit.getText().toString().trim().length() > 0)
                                {
                                    UDP = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                    final int decPPU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                    edtPrimaryUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                                    edtFreeQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                                }

                                if(SelectedAltUnit.getText().toString().trim().length() > 0)
                                {
                                    UDA = GetUnitDecimalPlaces(SelectedAltUnit.getText().toString().trim());
                                    final int decPAU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                    edtAltUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPAU)});
                                }

                                try {
                                    for (int i = 0; i < jsonArrayMRP.length(); i++) {

                                        if (jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals(SelectedMRP.getText().toString()) && jsonArrayMRP.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString())) {
                                            getMRPDetails(jsonArrayMRP.getJSONObject(i).getString("ItemId"),jsonArrayMRP.getJSONObject(i).getString("MRPId"));
                                            break;
                                        }


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                        mView.findViewById(R.id.layoutItemId).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                spItemId.showSpinerDialog();
                            }
                        });

                        //ItemCode Code END
                    SelectedMRP.setText("Select MRP");

                    spItem = new SpinnerDialog(InVoiceDetailsActivity.this, ITEMLIST,
                            "Select or Search Item");

                    spItem.setCancellable(true);
                    spItem.setShowKeyboard(false);

                    spItem.bindOnSpinerListener(new OnSpinerItemClick() {
                        @Override
                        public void onClick(String item, int position) {

                            ITEMIDARRAY.clear();
                            ALTUNITARRAY.clear();

                            if (null != item && item.length() > 0 )
                            {

                                int endIndex = item.lastIndexOf(" || ");
                                if (endIndex != -1)
                                {
                                    SetItemName = item.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
                                }
                            }
                            SelectedItem.setText(SetItemName);

                            ClearControl();
                            edtPrimaryUnitQty.setText("");
                            edtAltUnitQty.setText("");
                            edtFreeQty.setText("");
                            PrimaryUnitQty = 0;

                            for (int i = 0; i < jsonArrayItem.length(); i++) {
                                try {
                                    if (jsonArrayItem.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString())) {
                                        ITEMID = jsonArrayItem.getJSONObject(i).getString("Id");
                                        SetPrimaryUnit = jsonArrayItem.getJSONObject(i).getString("Unit");
                                        SetAltUnit = jsonArrayItem.getJSONObject(i).getString("SaleUnit");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(!ITEMID.equals("0") || !ITEMID.isEmpty()) {
                                SelectedItemId.setText(ITEMID);
                            }
                            else
                            {
                                SelectedItemId.setText("Item ID");
                            }

                            if(CheckValidate.checkemptystring(SetPrimaryUnit)  != "")
                            {
                                SelectedPrimaryUnit.setText(CheckValidate.checkemptystring(SetPrimaryUnit));
                            }

                            if(CheckValidate.checkemptystring(SetAltUnit)  == "")
                            {
                                SelectedAltUnit.setText("");
                                SelectedAltUnit.setEnabled(false);
                                edtAltUnitQty.setEnabled(false);

                            }
                            else
                            {
                                // SelectedAltUnit.setText(CheckValidate.checkemptystring(SetAltUnit));
                                SelectedAltUnit.setEnabled(true);
                                edtAltUnitQty.setEnabled(true);

                                for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                                    try {
                                        if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID)) {

                                            if(!jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").isEmpty() || !jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals("null"))
                                            {
                                                ALTUNITARRAY.add(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias"));
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(ALTUNITARRAY.size() > 0)
                                {
                                    SelectedAltUnit.setText(CheckValidate.checkemptystring(SetAltUnit));

                                    for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                                        try {
                                            if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID) && jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals(SelectedAltUnit.getText().toString())) {

                                                AltUnitConversion = CheckValidate.checkemptyDouble(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitConversion"));

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                else
                                {
                                    AltUnitConversion = 0;
                                }
                            }

                            for (int i = 0; i < jsonArrayMRP.length(); i++) {
                                try {
                                    if (jsonArrayMRP.getJSONObject(i).getString("ItemId").equals(ITEMID) && CheckValidate.checkemptyDouble(jsonArrayMRP.getJSONObject(i).getString("Stock")) > 0) {

                                        if(!jsonArrayMRP.getJSONObject(i).getString("ItemMRP").isEmpty() || !jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals("null"))
                                        {
                                            ITEMIDARRAY.add(jsonArrayMRP.getJSONObject(i).getString("ItemMRP"));
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(ITEMIDARRAY.size() > 0)
                            {
                                SelectedMRP.setText(ITEMIDARRAY.get(0));
                            }
                            else
                            {
                                Toast.makeText(InVoiceDetailsActivity.this, "No stock available", Toast.LENGTH_LONG).show();
                                SelectedMRP.setText("");
                                edtRate.setError(null);
                                return;
                            }

                            if(SelectedPrimaryUnit.getText().toString().trim().length() > 0)
                            {
                                UDP = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                final int decPPU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtPrimaryUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                                edtFreeQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                            }

                            if(SelectedAltUnit.getText().toString().trim().length() > 0)
                            {
                                UDA = GetUnitDecimalPlaces(SelectedAltUnit.getText().toString().trim());
                                final int decPAU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtAltUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPAU)});
                            }

                            try {
                                for (int i = 0; i < jsonArrayMRP.length(); i++) {

                                    if (jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals(SelectedMRP.getText().toString()) && jsonArrayMRP.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString())) {
                                        getMRPDetails(jsonArrayMRP.getJSONObject(i).getString("ItemId"),jsonArrayMRP.getJSONObject(i).getString("MRPId"));
                                        break;
                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    mView.findViewById(R.id.layoutItem).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            spItem.showSpinerDialog();
                        }
                    });


                    spMRP = new SpinnerDialog(InVoiceDetailsActivity.this, ITEMIDARRAY,
                            "Select or Search MRP");

                    spMRP.setCancellable(true);
                    spMRP.setShowKeyboard(false);

                    spMRP.bindOnSpinerListener(new OnSpinerItemClick() {
                        @Override
                        public void onClick(String item, int position) {

                            SelectedMRP.setText(item);
                            ClearControl();

                             /*edtAltUnitQty.setText("");
                            edtFreeQty.setText("");*/
                            if(CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim()) >=0)
                            {
                                PrimaryUnitQty = CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim());
                            }else{
                                PrimaryUnitQty = 0;
                            }

                            if(CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim()) >=0)
                            {
                                AltUnitQty = CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());
                            }else{
                                AltUnitQty = 0;
                            }

                            ////start

                            if(SelectedPrimaryUnit.getText().toString().trim().length() > 0)
                            {
                                UDP = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                final int decPPU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtPrimaryUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                                edtFreeQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                            }

                            if(SelectedAltUnit.getText().toString().trim().length() > 0)
                            {
                                UDA = GetUnitDecimalPlaces(SelectedAltUnit.getText().toString().trim());
                                final int decPAU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtAltUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPAU)});
                            }

                            try {
                                for (int i = 0; i < jsonArrayMRP.length(); i++) {

                                    if (jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals(SelectedMRP.getText().toString()) && jsonArrayMRP.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString())) {
                                        getMRPDetails(jsonArrayMRP.getJSONObject(i).getString("ItemId"),jsonArrayMRP.getJSONObject(i).getString("MRPId"));
                                        break;
                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ////end

                        }
                    });


                    mView.findViewById(R.id.layoutMRP).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            spMRP.showSpinerDialog();
                        }
                    });

                        final SpinnerDialog spAltUnit;

                        spAltUnit = new SpinnerDialog(InVoiceDetailsActivity.this, ALTUNITARRAY,
                                "Select");

                        spAltUnit.setCancellable(true);
                        spAltUnit.setShowKeyboard(false);

                        spAltUnit.bindOnSpinerListener(new OnSpinerItemClick() {
                            @Override
                            public void onClick(String item, int position) {
                                SelectedAltUnit.setText(item);


                                for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                                    try {
                                        if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID) && jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals(SelectedAltUnit.getText().toString())) {

                                            AltUnitConversion = CheckValidate.checkemptyDouble(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitConversion"));

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(SelectedPrimaryUnit.getText().toString().trim().length() > 0)
                                {
                                    UDP = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                    final int decPPU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                    edtPrimaryUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                                    edtFreeQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                                }

                                if(SelectedAltUnit.getText().toString().trim().length() > 0)
                                {
                                    UDA = GetUnitDecimalPlaces(SelectedAltUnit.getText().toString().trim());
                                    final int decPAU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                    edtAltUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPAU)});
                                }

                                try{

                                    edtAmount.setError(null);
                                    double tmpAltUnitQty =  CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());
                                    if(tmpAltUnitQty > 0) {
                                        if (UDA > 0) {
                                            edtAltUnitQty.setText(String.format("%." + UDA + "f", tmpAltUnitQty));
                                        } else {
                                            edtAltUnitQty.setText(String.format("%.0f", tmpAltUnitQty));
                                        }

                                    }
                                    AltUnitQty = CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());
                                    PrimaryUnitQty =  CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim());


                                    double tmpTotalQty;
                                    if(AltUnitQty <= 0)
                                    {
                                        tmpTotalQty = PrimaryUnitQty;

                                    }else
                                    {
                                        tmpTotalQty = (AltUnitQty * AltUnitConversion) + PrimaryUnitQty;
                                    }
                                    String strTotalQty = String.format("%."+UDP+"f",tmpTotalQty);
                                    edtTotalQty.setText(strTotalQty);
                                    TotalQty = CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim());

                                    double tmpGross = TotalQty * Rate;
                                    String gross = String.format("%.2f", tmpGross);
                                    edtGross.setText(gross);
                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    if (DiscPer > 0) {
                                        double tmpDisc = ((Gross * DiscPer) / 100);
                                        if (tmpDisc > 0) {
                                            String txtDisc = String.format("%.2f", tmpDisc);
                                            edtDisc.setText(txtDisc);
                                        } else {
                                            edtDisc.setText("");
                                        }
                                        Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                    } else {
                                        Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                    }

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    if (DiscIIPer > 0) {
                                        double tmpDiscII = (((Gross - Disc) * DiscIIPer) / 100);
                                        if (tmpDiscII > 0) {
                                            String txtDiscII = String.format("%.2f", tmpDiscII);
                                            edtDiscII.setText(txtDiscII);

                                        } else {
                                            edtDiscII.setText("");
                                        }
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    } else {
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    }

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    if (DiscIIIPer > 0) {

                                        double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                        if (tmpDiscIII > 0) {
                                            String txtDiscIII = String.format("%.2f", tmpDiscIII);
                                            edtDiscIII.setText(txtDiscIII);
                                        } else {
                                            edtDiscIII.setText("");
                                        }
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    } else {
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }

                                    TotalDisc = Disc + DiscII + DiscIII;
                                    if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                    {
                                        edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                    }else
                                    {
                                        edtTotalDisc.setText("");
                                    }

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    if (OtherPer > 0) {
                                        double tmpOther = (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                        if (tmpOther > 0) {
                                            String txtOther = String.format("%.2f", tmpOther);
                                            edtOther.setText(txtOther);
                                        } else {
                                            edtOther.setText("");
                                        }
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    } else {
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if (OtherIIPer > 0) {
                                        double tmpOtherII = (((Gross - Disc - DiscII - DiscIII + Other) * OtherIIPer) / 100);
                                        if (tmpOtherII > 0) {
                                            String txtOtherII = String.format("%.2f", tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        } else {
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    } else {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if (cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f", tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    } else {
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if (sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f", tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    } else {
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if (igstP > 0) {
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f", tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    } else {
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f", totalAmount);
                                    edtAmount.setText(totalAmt);

                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                        mView.findViewById(R.id.LayoutAltUnit).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                spAltUnit.showSpinerDialog();
                            }
                        });


                    final String Title = String.valueOf(jsonArray.length() + 1);
                    title.setText(Title);

                        edtTotalQty.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                edtTotalQty.setError(null);
                                edtTotalQty.setCompoundDrawables(null, null, null, null);

                            }
                        });
                    //start textchanged
                        edtAltUnitQty.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try{

                                    edtAmount.setError(null);
                                    if (s.toString().trim().length() == 0 ) {
                                    }


                                    AltUnitQty =  CheckValidate.checkemptyDouble(s.toString());
                                    PrimaryUnitQty =  CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim());

                                    double tmpTotalQty;
                                    if(AltUnitQty <= 0)
                                    {
                                        tmpTotalQty = PrimaryUnitQty;

                                    }else
                                    {
                                        tmpTotalQty = (AltUnitQty * AltUnitConversion) + PrimaryUnitQty;
                                    }
                                    String strTotalQty = String.format("%."+UDP+"f",tmpTotalQty);
                                    edtTotalQty.setText(strTotalQty);
                                    TotalQty = CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim());

                                    double tmpGross = TotalQty * Rate;
                                    String gross = String.format("%.2f", tmpGross);
                                    edtGross.setText(gross);
                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    if (DiscPer > 0) {
                                        double tmpDisc = ((Gross * DiscPer) / 100);
                                        if (tmpDisc > 0) {
                                            String txtDisc = String.format("%.2f", tmpDisc);
                                            edtDisc.setText(txtDisc);
                                        } else {
                                            edtDisc.setText("");
                                        }
                                        Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                    } else {
                                        Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                    }

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    if (DiscIIPer > 0) {
                                        double tmpDiscII = (((Gross - Disc) * DiscIIPer) / 100);
                                        if (tmpDiscII > 0) {
                                            String txtDiscII = String.format("%.2f", tmpDiscII);
                                            edtDiscII.setText(txtDiscII);

                                        } else {
                                            edtDiscII.setText("");
                                        }
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    } else {
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    }

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    if (DiscIIIPer > 0) {

                                        double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                        if (tmpDiscIII > 0) {
                                            String txtDiscIII = String.format("%.2f", tmpDiscIII);
                                            edtDiscIII.setText(txtDiscIII);
                                        } else {
                                            edtDiscIII.setText("");
                                        }
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    } else {
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }

                                    TotalDisc = Disc + DiscII + DiscIII;
                                    if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                    {
                                        edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                    }else
                                    {
                                        edtTotalDisc.setText("");
                                    }

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    if (OtherPer > 0) {
                                        double tmpOther = (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                        if (tmpOther > 0) {
                                            String txtOther = String.format("%.2f", tmpOther);
                                            edtOther.setText(txtOther);
                                        } else {
                                            edtOther.setText("");
                                        }
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    } else {
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if (OtherIIPer > 0) {
                                        double tmpOtherII = (((Gross - Disc - DiscII - DiscIII + Other) * OtherIIPer) / 100);
                                        if (tmpOtherII > 0) {
                                            String txtOtherII = String.format("%.2f", tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        } else {
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    } else {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if (cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f", tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    } else {
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if (sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f", tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    } else {
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if (igstP > 0) {
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f", tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    } else {
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f", totalAmount);
                                    edtAmount.setText(totalAmt);

                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });


                        edtPrimaryUnitQty.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                edtPrimaryUnitQty.setError(null);
                                edtPrimaryUnitQty.setCompoundDrawables(null, null, null, null);

                            }
                        });

                        edtPrimaryUnitQty.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try{

                                    edtAmount.setError(null);
                                    if (s.toString().trim().length() == 0 ) {
                                    }

                                    PrimaryUnitQty = CheckValidate.checkemptyDouble(s.toString());
                                    AltUnitQty = CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());

                                    double tmpTotalQty;
                                    if(AltUnitQty <= 0)
                                    {
                                        tmpTotalQty = PrimaryUnitQty;

                                    }else
                                    {
                                        tmpTotalQty = (AltUnitQty * AltUnitConversion) + PrimaryUnitQty;
                                    }
                                    String strTotalQty = String.format("%."+UDP+"f",tmpTotalQty);
                                    edtTotalQty.setText(strTotalQty);
                                    TotalQty = CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim());

                                    double tmpGross = TotalQty * Rate;
                                    String gross = String.format("%.2f", tmpGross);
                                    edtGross.setText(gross);
                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    if (DiscPer > 0) {
                                        double tmpDisc = ((Gross * DiscPer) / 100);
                                        if (tmpDisc > 0) {
                                            String txtDisc = String.format("%.2f", tmpDisc);
                                            edtDisc.setText(txtDisc);
                                        } else {
                                            edtDisc.setText("");
                                        }
                                        Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                    } else {
                                        Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                    }

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    if (DiscIIPer > 0) {
                                        double tmpDiscII = (((Gross - Disc) * DiscIIPer) / 100);
                                        if (tmpDiscII > 0) {
                                            String txtDiscII = String.format("%.2f", tmpDiscII);
                                            edtDiscII.setText(txtDiscII);

                                        } else {
                                            edtDiscII.setText("");
                                        }
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    } else {
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    }

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    if (DiscIIIPer > 0) {

                                        double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                        if (tmpDiscIII > 0) {
                                            String txtDiscIII = String.format("%.2f", tmpDiscIII);
                                            edtDiscIII.setText(txtDiscIII);
                                        } else {
                                            edtDiscIII.setText("");
                                        }
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    } else {
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }

                                    TotalDisc = Disc + DiscII + DiscIII;
                                    if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                    {
                                        edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                    }else
                                    {
                                        edtTotalDisc.setText("");
                                    }

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    if (OtherPer > 0) {
                                        double tmpOther = (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                        if (tmpOther > 0) {
                                            String txtOther = String.format("%.2f", tmpOther);
                                            edtOther.setText(txtOther);
                                        } else {
                                            edtOther.setText("");
                                        }
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    } else {
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if (OtherIIPer > 0) {
                                        double tmpOtherII = (((Gross - Disc - DiscII - DiscIII + Other) * OtherIIPer) / 100);
                                        if (tmpOtherII > 0) {
                                            String txtOtherII = String.format("%.2f", tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        } else {
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    } else {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if (cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f", tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    } else {
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if (sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f", tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    } else {
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if (igstP > 0) {
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f", tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    } else {
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f", totalAmount);
                                    edtAmount.setText(totalAmt);

                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        edtRate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                edtRate.setError(null);
                                edtRate.setCompoundDrawables(null, null, null, null);

                            }
                        });

                        edtRate.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try
                                {
                                    edtAmount.setError(null);
                                    if (s.toString().trim().length() == 0 || CheckValidate.checkemptyDouble(s.toString()) < 0 ) {
                                        edtRate.setError("Rate blank not allowed");
                                    }
                                    else {
                                        edtRate.setError(null);
                                        edtRate.setCompoundDrawables(null, null, null, null);
                                    }
                                    double TotalQty = CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim());
                                    Rate = CheckValidate.checkemptyDouble(s.toString());
                                    double tmpGross = TotalQty * Rate;
                                    String gross = String.format("%.2f",tmpGross);
                                    edtGross.setText(gross);
                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    if(DiscPer > 0)
                                    {
                                        double  tmpDisc = ((Gross * DiscPer) / 100);
                                        if(tmpDisc > 0 ){
                                            String txtDisc = String.format("%.2f",tmpDisc);
                                            edtDisc.setText(txtDisc);
                                        }else{
                                            edtDisc.setText("");
                                        }
                                        Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                    }else
                                    {
                                        Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                    }

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    if(DiscIIPer > 0)
                                    {
                                        double  tmpDiscII = (((Gross-Disc) * DiscIIPer) / 100);
                                        if(tmpDiscII > 0){
                                            String txtDiscII = String.format("%.2f",tmpDiscII);
                                            edtDiscII.setText(txtDiscII);

                                        }else{
                                            edtDiscII.setText("");
                                        }
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    }else
                                    {
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    }

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    if (DiscIIIPer > 0) {

                                        double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                        if(tmpDiscIII > 0){
                                            String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                            edtDiscIII.setText(txtDiscIII);
                                        }else{
                                            edtDiscIII.setText("");
                                        }
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }else
                                    {
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }

                                    TotalDisc = Disc + DiscII + DiscIII;
                                    if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                    {
                                        edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                    }else
                                    {
                                        edtTotalDisc.setText("");
                                    }

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    if(OtherPer > 0){
                                        double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                        if(tmpOther > 0){
                                            String txtOther = String.format("%.2f",tmpOther);
                                            edtOther.setText(txtOther);
                                        }else{
                                            edtOther.setText("");
                                        }
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }else
                                    {
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if(OtherIIPer > 0)
                                    {
                                        double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                        if(tmpOtherII > 0){
                                            String txtOtherII = String.format("%.2f",tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        }else{
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }else
                                    {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);

                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        edtDiscPer.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try
                                {
                                    edtAmount.setError(null);
                                    if (s.toString().trim().length()!=0) {

                                        edtDisc.setEnabled(false);
                                    }
                                    else {
                                        edtDisc.setEnabled(true);
                                    }

                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(s.toString());
                                    double  tmpDisc = ((Gross * DiscPer) / 100);
                                    if(tmpDisc > 0){
                                        String txtDisc = String.format("%.2f",tmpDisc);
                                        edtDisc.setText(txtDisc);
                                    }else{
                                        edtDisc.setText("");
                                    }
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    if(DiscIIPer > 0)
                                    {
                                        double  tmpDiscII = (((Gross-Disc) * DiscIIPer) / 100);
                                        if(tmpDiscII > 0){
                                            String txtDiscII = String.format("%.2f",tmpDiscII);
                                            edtDiscII.setText(txtDiscII);

                                        }else{
                                            edtDiscII.setText("");
                                        }
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    }else
                                    {
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    }

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    if (DiscIIIPer > 0) {

                                        double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                        if(tmpDiscIII > 0){
                                            String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                            edtDiscIII.setText(txtDiscIII);
                                        }else{
                                            edtDiscIII.setText("");
                                        }
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }else
                                    {
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }

                                    TotalDisc = Disc + DiscII + DiscIII;
                                    if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                    {
                                        edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                    }else
                                    {
                                        edtTotalDisc.setText("");
                                    }

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    if(OtherPer > 0){
                                        double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                        if(tmpOther > 0){
                                            String txtOther = String.format("%.2f",tmpOther);
                                            edtOther.setText(txtOther);
                                        }else{
                                            edtOther.setText("");
                                        }
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }else
                                    {
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if(OtherIIPer > 0)
                                    {
                                        double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                        if(tmpOtherII > 0){
                                            String txtOtherII = String.format("%.2f",tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        }else{
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }else
                                    {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);
                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        edtDisc.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try
                                {
                                    edtAmount.setError(null);
                                    if (s.toString().trim().length()!=0) {
                                    }

                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    Disc = CheckValidate.checkemptyDouble(s.toString());

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    if(DiscIIPer > 0)
                                    {
                                        double  tmpDiscII = (((Gross-Disc) * DiscIIPer) / 100);
                                        if(tmpDiscII > 0){
                                            String txtDiscII = String.format("%.2f",tmpDiscII);
                                            edtDiscII.setText(txtDiscII);

                                        }else{
                                            edtDiscII.setText("");
                                        }
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    }else
                                    {
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    }

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    if (DiscIIIPer > 0) {

                                        double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                        if(tmpDiscIII > 0){
                                            String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                            edtDiscIII.setText(txtDiscIII);
                                        }else{
                                            edtDiscIII.setText("");
                                        }
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }else
                                    {
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }

                                    TotalDisc = Disc + DiscII + DiscIII;
                                    if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                    {
                                        edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                    }else
                                    {
                                        edtTotalDisc.setText("");
                                    }

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    if(OtherPer > 0){
                                        double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                        if(tmpOther > 0){
                                            String txtOther = String.format("%.2f",tmpOther);
                                            edtOther.setText(txtOther);
                                        }else{
                                            edtOther.setText("");
                                        }
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }else
                                    {
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if(OtherIIPer > 0)
                                    {
                                        double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                        if(tmpOtherII > 0){
                                            String txtOtherII = String.format("%.2f",tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        }else{
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }else
                                    {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);

                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        edtDiscIIPer.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try
                                {
                                    edtAmount.setError(null);
                                    if (s.toString().trim().length()!=0) {

                                        edtDiscII.setEnabled(false);
                                    }
                                    else {
                                        edtDiscII.setEnabled(true);
                                    }

                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                    DiscIIPer = CheckValidate.checkemptyDouble(s.toString());
                                    double  tmpDiscII = (((Gross - Disc) * DiscIIPer) / 100);
                                    if(tmpDiscII > 0){
                                        String txtDiscII = String.format("%.2f",tmpDiscII);
                                        edtDiscII.setText(txtDiscII);
                                    }else{
                                        edtDiscII.setText("");
                                    }

                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    if (DiscIIIPer > 0) {

                                        double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                        if(tmpDiscIII > 0){
                                            String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                            edtDiscIII.setText(txtDiscIII);
                                        }else{
                                            edtDiscIII.setText("");
                                        }
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }else
                                    {
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }

                                    TotalDisc = Disc + DiscII + DiscIII;
                                    if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                    {
                                        edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                    }else
                                    {
                                        edtTotalDisc.setText("");
                                    }

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    if(OtherPer > 0){
                                        double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                        if(tmpOther > 0){
                                            String txtOther = String.format("%.2f",tmpOther);
                                            edtOther.setText(txtOther);
                                        }else{
                                            edtOther.setText("");
                                        }
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }else
                                    {
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if(OtherIIPer > 0)
                                    {
                                        double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                        if(tmpOtherII > 0){
                                            String txtOtherII = String.format("%.2f",tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        }else{
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }else
                                    {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);

                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        edtDiscII.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try
                                {
                                    edtAmount.setError(null);
                                    if (s.toString().trim().length()!=0) {
                                    }

                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    DiscII = CheckValidate.checkemptyDouble(s.toString());

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    if (DiscIIIPer > 0) {

                                        double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                        if(tmpDiscIII > 0){
                                            String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                            edtDiscIII.setText(txtDiscIII);
                                        }else{
                                            edtDiscIII.setText("");
                                        }
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }else
                                    {
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }

                                    TotalDisc = Disc + DiscII + DiscIII;
                                    if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                    {
                                        edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                    }else
                                    {
                                        edtTotalDisc.setText("");
                                    }

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    if(OtherPer > 0){
                                        double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                        if(tmpOther > 0){
                                            String txtOther = String.format("%.2f",tmpOther);
                                            edtOther.setText(txtOther);
                                        }else{
                                            edtOther.setText("");
                                        }
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }else
                                    {
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if(OtherIIPer > 0)
                                    {
                                        double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                        if(tmpOtherII > 0){
                                            String txtOtherII = String.format("%.2f",tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        }else{
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }else
                                    {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);
                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        edtDiscIIIPer.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try
                                {
                                    edtAmount.setError(null);
                                    if (s.toString().trim().length()!=0) {
                                        edtDiscIII.setEnabled(false);
                                    }
                                    else {
                                        edtDiscIII.setEnabled(true);
                                    }

                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                    DiscIIIPer = CheckValidate.checkemptyDouble(s.toString());
                                    double  tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                    if(tmpDiscIII > 0){
                                        String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                        edtDiscIII.setText(txtDiscIII);
                                    }else{
                                        edtDiscIII.setText("");
                                    }
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());

                                    TotalDisc = Disc + DiscII + DiscIII;
                                    if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                    {
                                        edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                    }else
                                    {
                                        edtTotalDisc.setText("");
                                    }

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    if(OtherPer > 0){
                                        double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                        if(tmpOther > 0){
                                            String txtOther = String.format("%.2f",tmpOther);
                                            edtOther.setText(txtOther);
                                        }else{
                                            edtOther.setText("");
                                        }
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }else
                                    {
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if(OtherIIPer > 0)
                                    {
                                        double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                        if(tmpOtherII > 0){
                                            String txtOtherII = String.format("%.2f",tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        }else{
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }else
                                    {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);
                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        edtDiscIII.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try
                                {
                                    edtAmount.setError(null);
                                    if (s.toString().trim().length()!=0) {
                                    }

                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    DiscIII = CheckValidate.checkemptyDouble(s.toString());

                                    TotalDisc = Disc + DiscII + DiscIII;
                                    if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                    {
                                        edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                    }else
                                    {
                                        edtTotalDisc.setText("");
                                    }

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    if(OtherPer > 0){
                                        double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                        if(tmpOther > 0){
                                            String txtOther = String.format("%.2f",tmpOther);
                                            edtOther.setText(txtOther);
                                        }else{
                                            edtOther.setText("");
                                        }
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }else
                                    {
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if(OtherIIPer > 0)
                                    {
                                        double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                        if(tmpOtherII > 0){
                                            String txtOtherII = String.format("%.2f",tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        }else{
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }else
                                    {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);
                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        edtOtherPer.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try
                                {
                                    edtAmount.setError(null);
                                    if (s.toString().trim().length()!=0) {

                                        edtOther.setEnabled(false);

                                    }
                                    else {
                                        edtOther.setEnabled(true);
                                    }

                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());

                                    OtherPer = CheckValidate.checkemptyDouble(s.toString());
                                    double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                    if(tmpOther> 0) {
                                        String txtOther = String.format("%.2f",tmpOther);
                                        edtOther.setText(txtOther);
                                    }else{
                                        edtOther.setText("");
                                    }
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if(OtherIIPer > 0)
                                    {
                                        double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                        if(tmpOtherII > 0){
                                            String txtOtherII = String.format("%.2f",tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        }else{
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }else
                                    {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);
                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        edtOther.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try
                                {
                                    edtAmount.setError(null);
                                    if (s.toString().trim().length()!=0) {
                                    }

                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    Other = CheckValidate.checkemptyDouble(s.toString());

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if(OtherIIPer > 0)
                                    {
                                        double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                        if(tmpOtherII > 0){
                                            String txtOtherII = String.format("%.2f",tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        }else{
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }else
                                    {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);
                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        edtOtherIIPer.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try
                                {
                                    edtAmount.setError(null);
                                    if (s.toString().trim().length()!=0) {

                                        edtOtherII.setEnabled(false);
                                    }
                                    else {
                                        edtOtherII.setEnabled(true);
                                    }

                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());

                                    OtherIIPer = CheckValidate.checkemptyDouble(s.toString());
                                    double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                    if(tmpOtherII > 0){
                                        String txtOtherII = String.format("%.2f",tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    }else{
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);
                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        edtOtherII.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try
                                {
                                    edtAmount.setError(null);
                                    if (s.toString().trim().length()!=0) {
                                    }

                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    OtherII = CheckValidate.checkemptyDouble(s.toString());

                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);
                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });


                     //end text changed
                        btnMGDisc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try
                                {
                                    edtAmount.setError(null);

                                    DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                    if(DiscPer == 0)
                                    {
                                        Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());
                                        Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                        cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                        sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                        igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());
                                        double tmpgstPer = 0;
                                        if(cgstP > 0 && sgstP > 0)
                                        {
                                            tmpgstPer = cgstP+sgstP;
                                        }
                                        else
                                        {
                                            tmpgstPer = igstP;
                                        }

                                        if(tmpgstPer > 0)
                                        {
                                            double  tmpDisc = (Disc /((1+(tmpgstPer/100)))) ;

                                            if(tmpDisc > 0){
                                                String txtDisc= String.format("%.2f",tmpDisc);
                                                edtDisc.setText(txtDisc);

                                            }else{
                                                edtDisc.setText("");
                                            }
                                        }
                                        Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                        DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                        if(DiscIIPer > 0)
                                        {
                                            double  tmpDiscII = (((Gross-Disc) * DiscIIPer) / 100);
                                            if(tmpDiscII > 0){
                                                String txtDiscII = String.format("%.2f",tmpDiscII);
                                                edtDiscII.setText(txtDiscII);

                                            }else{
                                                edtDiscII.setText("");
                                            }
                                            DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                        }else
                                        {
                                            DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                        }

                                        DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                        if (DiscIIIPer > 0) {

                                            double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                            if(tmpDiscIII > 0){
                                                String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                                edtDiscIII.setText(txtDiscIII);
                                            }else{
                                                edtDiscIII.setText("");
                                            }
                                            DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                        }else
                                        {
                                            DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                        }

                                        TotalDisc = Disc + DiscII + DiscIII;
                                        if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                        {
                                            edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                        }else
                                        {
                                            edtTotalDisc.setText("");
                                        }

                                        OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                        if(OtherPer > 0){
                                            double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                            if(tmpOther > 0){
                                                String txtOther = String.format("%.2f",tmpOther);
                                                edtOther.setText(txtOther);
                                            }else{
                                                edtOther.setText("");
                                            }
                                            Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                        }else
                                        {
                                            Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                        }

                                        OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                        if(OtherIIPer > 0)
                                        {
                                            double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                            if(tmpOtherII > 0){
                                                String txtOtherII = String.format("%.2f",tmpOtherII);
                                                edtOtherII.setText(txtOtherII);
                                            }else{
                                                edtOtherII.setText("");
                                            }
                                            OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                        }else
                                        {
                                            OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                        }

                                        double cgst = 0;
                                        if(cgstP > 0) {
                                            double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                            String txtcgst = String.format("%.2f",tmpCgst);
                                            edtCGST.setText(txtcgst);
                                            cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                        }else{
                                            edtCGST.setText("");
                                        }

                                        double sgst = 0;
                                        if(sgstP > 0) {
                                            double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                            String txtsgst = String.format("%.2f",tmpSgst);
                                            edtSGST.setText(txtsgst);
                                            sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                        }else{
                                            edtSGST.setText("");
                                        }

                                        double igst = 0;
                                        if(igstP > 0){
                                            double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                            String txtigst = String.format("%.2f",tmpIgst);
                                            edtIGST.setText(txtigst);
                                            igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                        }else{
                                            edtIGST.setText("");
                                        }

                                        double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                        String totalAmt = String.format("%.2f",totalAmount);
                                        edtAmount.setText(totalAmt);
                                    }


                                }catch (NumberFormatException e){
                                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();


                            }
                        });

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                if (SelectedItem.getText().toString().trim().length() == 0 || SelectedItem.getText().toString().trim().equals("Select Item") ) {
                                    ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Must select item.");
                                    return;
                                }else if(CheckValidate.checkemptyDouble(SelectedMRP.getText().toString().trim()) <= 0 || SelectedItem.getText().toString().trim().equals("Select MRP")){
                                    ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","MRP zero not allow.");
                                    return;
                                }else if(CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim()) <= 0 && CheckValidate.checkemptyDouble(edtFreeQty.getText().toString().trim()) <= 0 ) {
                                    edtPrimaryUnitQty.setError("Qty Blank not allow.");
                                    return;
                                }else if(CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim()) > CheckValidate.checkemptyDouble(edtStock.getText().toString().trim())){
                                    edtPrimaryUnitQty.setError("Sale Qty not more then Stock.");
                                    return;
                                }else if(edtRate.getText().toString().trim().length() == 0){
                                    edtRate.setError("Rate Blank not allow.");
                                    return;
                                }else if(CheckValidate.checkemptyDouble(edtAmount.getText().toString().trim()) < 0 || edtAmount.getText().toString().trim().length() == 0){
                                    edtAmount.setError("Negative Billing not allow.");
                                    ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Negative Billing not allow.");
                                    return;
                                }
                                else{

                                    String ItemID = null, MRPID = null, strPrimaryUnitId = null, strAltUnitId = null;
                                    for (int i = 0; i < jsonArrayItem.length(); i++) {
                                        try {
                                            if (jsonArrayItem.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString().trim())) {

                                                ItemID = jsonArrayItem.getJSONObject(i).getString("Id");

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    for (int i = 0; i < jsonArrayUnits.length(); i++) {
                                        try {
                                            if (jsonArrayUnits.getJSONObject(i).getString("UnitAlias").equals(SelectedPrimaryUnit.getText().toString().trim())) {

                                                strPrimaryUnitId = jsonArrayUnits.getJSONObject(i).getString("UnitID");

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    for (int i = 0; i < jsonArrayUnits.length(); i++) {
                                        try {
                                            if (jsonArrayUnits.getJSONObject(i).getString("UnitAlias").equals(SelectedAltUnit.getText().toString().trim())) {

                                                strAltUnitId = jsonArrayUnits.getJSONObject(i).getString("UnitID");

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    for (int i = 0; i < jsonArrayMRP.length(); i++) {
                                        try {
                                            if (jsonArrayMRP.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString().trim()) && jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals(SelectedMRP.getText().toString().trim())) {

                                                MRPID = jsonArrayMRP.getJSONObject(i).getString("MRPId");

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        JSONObject jsonObject = new JSONObject();

                                        jsonObject.put("ItemId", ItemID);
                                        jsonObject.put("MRPId", MRPID);
                                        jsonObject.put("ItemName", SelectedItem.getText().toString().trim());
                                        jsonObject.put("ItemMRP", SelectedMRP.getText().toString().trim());
                                        jsonObject.put("AlternetUnitID", strAltUnitId);
                                        jsonObject.put("AlternetUnit", SelectedAltUnit.getText().toString().trim());
                                        jsonObject.put("AlternetUnitQty", edtAltUnitQty.getText().toString().trim());
                                        jsonObject.put("PrimaryUnitID", strPrimaryUnitId);
                                        jsonObject.put("PrimaryUnit", SelectedPrimaryUnit.getText().toString().trim());
                                        jsonObject.put("PrimaryUnitQty", edtPrimaryUnitQty.getText().toString().trim());
                                        jsonObject.put("TotalQty", edtTotalQty.getText().toString().trim());
                                        jsonObject.put("FreeQty", edtFreeQty.getText().toString().trim());
                                        jsonObject.put("Rate", edtRate.getText().toString().trim());
                                        jsonObject.put("Gross", edtGross.getText().toString().trim());
                                        jsonObject.put("DiscountPer", edtDiscPer.getText().toString());
                                        jsonObject.put("Discount", edtDisc.getText().toString());
                                        jsonObject.put("DiscountIIPer", edtDiscIIPer.getText().toString());
                                        jsonObject.put("DiscountII", edtDiscII.getText().toString());
                                        jsonObject.put("DiscountIIIPer", edtDiscIIIPer.getText().toString());
                                        jsonObject.put("DiscountIII", edtDiscIII.getText().toString());
                                        jsonObject.put("OtherPer", edtOtherPer.getText().toString().trim());
                                        jsonObject.put("Other", edtOther.getText().toString().trim());
                                        jsonObject.put("OtherIIPer", edtOtherIIPer.getText().toString().trim());
                                        jsonObject.put("OtherII", edtOtherII.getText().toString().trim());
                                        jsonObject.put("CGSTAccountID", edtCGSTACID.getText().toString().trim());
                                        jsonObject.put("CGSTPer", edtCGSTP.getText().toString().trim());
                                        jsonObject.put("CGSTAmt", edtCGST.getText().toString().trim());
                                        jsonObject.put("SGSTAccountID", edtSGSTACID.getText().toString().trim());
                                        jsonObject.put("SGSTPer", edtSGSTP.getText().toString().trim());
                                        jsonObject.put("SGSTAmt", edtSGST.getText().toString().trim());
                                        jsonObject.put("IGSTAccountID", edtIGSTACID.getText().toString().trim());
                                        jsonObject.put("IGSTPer", edtIGSTP.getText().toString().trim());
                                        jsonObject.put("IGSTAmt", edtIGST.getText().toString().trim());
                                        jsonObject.put("NetAmount", edtAmount.getText().toString());
                                        jsonArrayInvoiceDetails.put(jsonArrayInvoiceDetails.length(), jsonObject);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    adapter.notifyDataSetChanged();


                                    calculateSum();
                                    dialog.dismiss();

                                }

                            } catch (Exception e) {
                                Toast.makeText(InVoiceDetailsActivity.this, "Error in Ok Click", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                        //end ok click
                    });
                    //end ok
                }
                }catch (Exception e)
                {
                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (SelectedItemDivision.getText().toString().trim().equals("Select Division")) {
                        ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Must select division.");
                        return;
                    }
                    else if (SelectedItemRoute.getText().toString().trim().equals("Select Route")) {
                        ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Must select route.");
                        return;
                    }
                    else if (SelectedItemClient.getText().toString().trim().equals("Select Client")) {
                        ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Must select client.");
                        return;
                    }
                    else if (SelectedItemSaleman.getText().toString().trim().equals("Select Salesman")) {
                        ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Must select salesman.");
                        return;
                    }
                    else if (jsonArrayInvoiceDetails.length() == 0) {
                        ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Add atleast 1 item.");
                        return;
                    }
                    else {
                        jsonObjectAddValues = new JSONObject();

                        if (jsonArrayDivision.length() != 0 || jsonArrayRoute.length() != 0 || jsonArraySalesmen.length() != 0 || jsonArrayClient.length() != 0) {

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

                            for (int i = 0; i < jsonArrayClient.length(); i++) {
                                try {
                                    if (jsonArrayClient.getJSONObject(i).getString("AccountName").equals(SelectedItemClient.getText().toString().trim())) {

                                        jsonObjectAddValues.put("ClientId", jsonArrayClient.getJSONObject(i).getString("Id"));

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                jsonObjectAddValues.put("InvId", getIntent().getStringExtra("id"));
                                jsonObjectAddValues.put("RoundOff", tvPointValue.getText().toString());
                                jsonObjectAddValues.put("Amount", tvTotalAmt.getText().toString());
                                jsonObjectAddValues.put("InvDate", tvDate.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            addInVoice();
                        }
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        SelectedItemDivision = findViewById(R.id.tvDivision);
        SelectedItemRoute = findViewById(R.id.tvRoute);
        SelectedItemSaleman = findViewById(R.id.tvSaleman);
        SelectedItemClient = findViewById(R.id.tvClient);
        //ScrollView = findViewById(R.id.ScrollView);

        recyclerView = findViewById(R.id.listViewinvoiceDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ////getItem();
//        Date c = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//        String formattedDate = df.format(c);
//        tvDate.setText(formattedDate);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                try {

                    final String Number = String.valueOf((position + 1));

                    try {
                        jobjPerticularPosition = jsonArrayInvoiceDetails.getJSONObject(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    TextView myTextView = itemView.findViewById(R.id.title);

                    final TextView tvItem = itemView.findViewById(R.id.tvItem);
                    final TextView tvMRP = itemView.findViewById(R.id.tvMRP);
                    final TextView tvAlternetQtyUnit = itemView.findViewById(R.id.tvAlternetQtyUnit);
                    final TextView tvAlternetQty = itemView.findViewById(R.id.tvAlternetQty);
                    final TextView tvPrimaryQtyUnit = itemView.findViewById(R.id.tvPrimaryQtyUnit);
                    final TextView tvPrimaryQty = itemView.findViewById(R.id.tvPrimaryQty);
                    final TextView tvTotalQty = itemView.findViewById(R.id.tvTotalQty);
                    final TextView tvFreeQty = itemView.findViewById(R.id.tvFreeQty);
                    final TextView tvRate = itemView.findViewById(R.id.tvRate);
                    final TextView tvGross = itemView.findViewById(R.id.tvGross);
                    final TextView tvDiscPer = itemView.findViewById(R.id.tvDiscPer);
                    final TextView tvDisc = itemView.findViewById(R.id.tvDisc);
                    final TextView tvDiscIIPer = itemView.findViewById(R.id.tvDiscIIPer);
                    final TextView tvDiscII = itemView.findViewById(R.id.tvDiscII);
                    final TextView tvDiscIIIPer = itemView.findViewById(R.id.tvDiscIIIPer);
                    final TextView tvDiscIII = itemView.findViewById(R.id.tvDiscIII);
                    final TextView tvOtherPer = itemView.findViewById(R.id.tvOtherPer);
                    final TextView tvOther = itemView.findViewById(R.id.tvOther);
                    final TextView tvOtherIIPer = itemView.findViewById(R.id.tvOtherIIPer);
                    final TextView tvOtherII = itemView.findViewById(R.id.tvOtherII);
                    final TextView tvCGSTACID = itemView.findViewById(R.id.tvCGSTACID);
                    final TextView tvCGSTP = itemView.findViewById(R.id.tvCGSTP);
                    final TextView tvCGST = itemView.findViewById(R.id.tvCGST);
                    final TextView tvSGSTACID = itemView.findViewById(R.id.tvSGSTACID);
                    final TextView tvSGSTP = itemView.findViewById(R.id.tvSGSTP);
                    final TextView tvSGST = itemView.findViewById(R.id.tvSGST);
                    final TextView tvIGSTACID = itemView.findViewById(R.id.tvIGSTACID);
                    final TextView tvIGSTP = itemView.findViewById(R.id.tvIGSTP);
                    final TextView tvIGST = itemView.findViewById(R.id.tvIGST);
                    final TextView tvAmount = itemView.findViewById(R.id.tvAmount);


                   String chkItemId = "";
                    for (int i = 0; i < jsonArrayItem.length(); i++) {
                        try {
                            if (jsonArrayItem.getJSONObject(i).getString("ItemName").equals(tvItem.getText().toString())) {
                                chkItemId = jsonArrayItem.getJSONObject(i).getString("Id");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if(chkItemId.equals("0") || chkItemId.isEmpty())
                    {
                        ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Error...","Item is deactivated so you can't edit...");
                        return;
                    }

                    myTextView.setText(Number);
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(InVoiceDetailsActivity.this);
                    final View mView = LayoutInflater.from(InVoiceDetailsActivity.this).inflate(R.layout.editable_invoice_list, null);
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    TextView title = mView.findViewById(R.id.titlePopup);


                    //PrimaryUnitQty = CheckValidate.checkemptyDouble(tvPrimaryQty.getText().toString().trim());
                    //AltUnitQty = CheckValidate.checkemptyDouble(tvAlternetQty.getText().toString().trim());
                    //Gross = CheckValidate.checkemptyDouble(tvGross.getText().toString().trim());
                    //Rate = CheckValidate.checkemptyDouble(tvRate.getText().toString().trim());

                    edtAltUnitQty = mView.findViewById(R.id.edtAltUnitQty);
                    edtPrimaryUnitQty = mView.findViewById(R.id.edtPrimaryUnitQty);
                    edtTotalQty = mView.findViewById(R.id.edtTotalQty);
                    edtFreeQty = mView.findViewById(R.id.edtFreeQty);
                    edtRate = mView.findViewById(R.id.edtRate);
                    edtStock = mView.findViewById(R.id.edtStock);
                    edtGross = mView.findViewById(R.id.edtGross);
                    edtDiscPer = mView.findViewById(R.id.edtDiscPer);
                    edtDisc = mView.findViewById(R.id.edtDisc);
                    edtDiscIIPer = mView.findViewById(R.id.edtDiscIIPer);
                    edtDiscII = mView.findViewById(R.id.edtDiscII);
                    edtDiscIIIPer = mView.findViewById(R.id.edtDiscIIIPer);
                    edtDiscIII = mView.findViewById(R.id.edtDiscIII);
                    edtTotalDisc = mView.findViewById(R.id.edtTotalDisc);
                    edtOtherPer = mView.findViewById(R.id.edtOtherPer);
                    edtOther = mView.findViewById(R.id.edtOther);
                    edtOtherIIPer = mView.findViewById(R.id.edtOtherIIPer);
                    edtOtherII = mView.findViewById(R.id.edtOtherII);
                    edtCGSTACID = mView.findViewById(R.id.edtCGSTACID);
                    edtCGSTP = mView.findViewById(R.id.edtCGSTP);
                    edtCGST = mView.findViewById(R.id.edtCGST);
                    edtSGSTACID = mView.findViewById(R.id.edtSGSTACID);
                    edtSGSTP = mView.findViewById(R.id.edtSGSTP);
                    edtSGST = mView.findViewById(R.id.edtSGST);
                    edtIGSTACID = mView.findViewById(R.id.edtIGSTACID);
                    edtIGSTP = mView.findViewById(R.id.edtIGSTP);
                    edtIGST = mView.findViewById(R.id.edtIGST);
                    edtAmount = mView.findViewById(R.id.edtAmount);
                    btnOk = mView.findViewById(R.id.btnOk);
                    btnMGDisc = mView.findViewById(R.id.btnMGDisc);
                    btnCancel = mView.findViewById(R.id.btnCancel);

                    if(getIntent().getStringExtra("btnType").equals("ViewOnly")){
                            btnOk.setText("View Mode");
                            btnOk.setEnabled(false);
                        btnMGDisc.setEnabled(false);
                       /* btnOk.setVisibility(View.GONE);*/

                    }else
                    {
                        /*btnOk.setVisibility(View.VISIBLE);*/
                        btnOk.setEnabled(true);
                        btnMGDisc.setEnabled(true);
                    }
                    title.setText(Number);

                    edtAltUnitQty.setText(tvAlternetQty.getText().toString().trim());
                    edtPrimaryUnitQty.setText(tvPrimaryQty.getText().toString().trim());
                    edtTotalQty.setText(tvTotalQty.getText().toString().trim());
                    edtFreeQty.setText(tvFreeQty.getText().toString().trim());
                    edtRate.setText(tvRate.getText().toString().trim());
                    edtGross.setText(tvGross.getText().toString().trim());
                    edtDiscPer.setText(tvDiscPer.getText().toString().trim());
                    edtDisc.setText(tvDisc.getText().toString().trim());
                    edtDiscIIPer.setText(tvDiscIIPer.getText().toString().trim());
                    edtDiscII.setText(tvDiscII.getText().toString().trim());
                    edtDiscIIIPer.setText(tvDiscIIIPer.getText().toString().trim());
                    edtDiscIII.setText(tvDiscIII.getText().toString().trim());
                    edtOtherPer.setText(tvOtherPer.getText().toString().trim());
                    edtOther.setText(tvOther.getText().toString().trim());
                    edtOtherIIPer.setText(tvOtherIIPer.getText().toString().trim());
                    edtOtherII.setText(tvOtherII.getText().toString().trim());
                    edtCGSTACID.setText(tvCGSTACID.getText().toString().trim());
                    edtCGSTP.setText(tvCGSTP.getText().toString().trim());
                    edtCGST.setText(tvCGST.getText().toString().trim());
                    edtSGSTACID.setText(tvSGSTACID.getText().toString().trim());
                    edtSGSTP.setText(tvSGSTP.getText().toString().trim());
                    edtSGST.setText(tvSGST.getText().toString().trim());
                    edtIGSTACID.setText(tvIGSTACID.getText().toString().trim());
                    edtIGSTP.setText(tvIGSTP.getText().toString().trim());
                    edtIGST.setText(tvIGST.getText().toString().trim());
                    edtAmount.setText(tvAmount.getText().toString().trim());

                    if(CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim())  > 0){
                        edtDisc.setEnabled(false);
                    }else{
                        edtDisc.setEnabled(true);
                    }
                    if(CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim())  > 0){
                        edtDiscII.setEnabled(false);
                    }else{
                        edtDiscII.setEnabled(true);
                    }
                    if(CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim())  > 0){
                        edtDiscIII.setEnabled(false);
                    }else{
                        edtDiscIII.setEnabled(true);
                    }
                    if(CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim())  > 0){
                        edtOther.setEnabled(false);
                    }else{
                        edtOther.setEnabled(true);
                    }
                    if(CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim())  > 0){
                        edtOtherII.setEnabled(false);
                    }else{
                        edtOtherII.setEnabled(true);
                    }


                    edtRate.setFilters(new InputFilter[]{new InputFilterDecimal(12,5)});
                    edtDiscPer.setFilters(new InputFilter[]{new InputFilterDecimal(2,2)});
                    edtDisc.setFilters(new InputFilter[]{new InputFilterDecimal(12,2)});
                    edtDiscIIPer.setFilters(new InputFilter[]{new InputFilterDecimal(2,2)});
                    edtDiscII.setFilters(new InputFilter[]{new InputFilterDecimal(12,2)});
                    edtDiscIIIPer.setFilters(new InputFilter[]{new InputFilterDecimal(2,2)});
                    edtDiscIII.setFilters(new InputFilter[]{new InputFilterDecimal(12,2)});
                    edtOtherPer.setFilters(new InputFilter[]{new InputFilterDecimal(2,2)});
                    edtOther.setFilters(new InputFilter[]{new InputFilterDecimal(12,2)});
                    edtOtherIIPer.setFilters(new InputFilter[]{new InputFilterDecimal(2,2)});
                    edtOtherII.setFilters(new InputFilter[]{new InputFilterDecimal(12,2)});

                    if(CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim()) >=0)
                    {
                        PrimaryUnitQty = CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim());
                    }else{
                        PrimaryUnitQty = 0;
                    }

                    if(CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim()) >=0)
                    {
                        AltUnitQty = CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());
                    }else{
                        AltUnitQty = 0;
                    }

                    final TextView SelectedItem = mView.findViewById(R.id.selectItem);
                    SelectedItem.setText(tvItem.getText().toString());

                    final TextView  SelectedAltUnit = mView.findViewById(R.id.selectAltUnit);
                    final SpinnerDialog spAltUnit;


                    final TextView SelectedPrimaryUnit = mView.findViewById(R.id.selectPrimaryUnit);
                    SelectedPrimaryUnit.setText(tvPrimaryQtyUnit.getText().toString());

                    final TextView SelectedMRP = mView.findViewById(R.id.selectMRP);
                    final SpinnerDialog spMRP;

                    final ArrayList<String> ITEMIDARRAY = new ArrayList<>();
                    final ArrayList<String> ALTUNITARRAY = new ArrayList<>();

                    final SpinnerDialog spItem;

                    for (int i = 0; i < jsonArrayItem.length(); i++) {
                        try {
                            if (jsonArrayItem.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString())) {
                                ITEMID = jsonArrayItem.getJSONObject(i).getString("Id");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(CheckValidate.checkemptystring(tvAlternetQtyUnit.getText().toString())  == "")
                    {

                        SelectedAltUnit.setText("");
                        SelectedAltUnit.setEnabled(false);
                        edtAltUnitQty.setEnabled(false);

                    }
                    else
                    {

                        SelectedAltUnit.setEnabled(true);
                        edtAltUnitQty.setEnabled(true);

                        for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                            try {
                                if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID)) {

                                    if(!jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").isEmpty() || !jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals("null"))
                                    {
                                        ALTUNITARRAY.add(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias"));
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if(ALTUNITARRAY.size() > 0)
                        {
                            SelectedAltUnit.setText(tvAlternetQtyUnit.getText().toString());

                            for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                                try {
                                    if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID) && jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals(SelectedAltUnit.getText().toString())) {

                                        AltUnitConversion = CheckValidate.checkemptyDouble(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitConversion"));

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {
                            AltUnitConversion = 0;
                        }
                    }

                    for (int i = 0; i < jsonArrayMRP.length(); i++) {
                        try {
                            if (jsonArrayMRP.getJSONObject(i).getString("ItemId").equals(ITEMID)) {
                                ITEMIDARRAY.add(jsonArrayMRP.getJSONObject(i).getString("ItemMRP"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    SelectedMRP.setText(tvMRP.getText().toString());

                    if(SelectedPrimaryUnit.getText().toString().trim().length() > 0)
                    {
                        UDP = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                        final int decPPU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                        edtPrimaryUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                        edtFreeQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                    }

                    if(SelectedAltUnit.getText().toString().trim().length() > 0)
                    {
                        UDA = GetUnitDecimalPlaces(SelectedAltUnit.getText().toString().trim());
                        final int decPAU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                        edtAltUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPAU)});
                    }

                    try {
                        for (int i = 0; i < jsonArrayMRP.length(); i++) {

                            if (jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals(SelectedMRP.getText().toString()) && jsonArrayMRP.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString())) {
                                getMRPDetails(jsonArrayMRP.getJSONObject(i).getString("ItemId"),jsonArrayMRP.getJSONObject(i).getString("MRPId"));
                                break;
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //ItemCode Code Start

                    final TextView SelectedItemId = mView.findViewById(R.id.selectItemId);
                    SelectedItemId.setText(ITEMID);
                    final SpinnerDialog spItemId;

                    spItemId = new SpinnerDialog(InVoiceDetailsActivity.this, ITEMIDLIST,
                            "Select or Search Item ID");

                    spItemId.setCancellable(true);
                    spItemId.setShowKeyboard(false);

                    spItemId.bindOnSpinerListener(new OnSpinerItemClick() {
                        @Override
                        public void onClick(String item, int position) {

                            ITEMIDARRAY.clear();
                            ALTUNITARRAY.clear();
                            ClearControl();
                            ClearGstControl();

                            if (null != item && item.length() > 0 )
                            {
                                int endIndex = item.lastIndexOf(" || ");
                                if (endIndex != -1)
                                {
                                    tmpSetItemId = item.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
                                }
                            }

                            SelectedItemId.setText(tmpSetItemId);

                            if(CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim()) >=0)
                            {
                                PrimaryUnitQty = CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim());
                            }else{
                                PrimaryUnitQty = 0;
                            }



                            for (int i = 0; i < jsonArrayItem.length(); i++) {
                                try {
                                    if (jsonArrayItem.getJSONObject(i).getString("Id").equals(SelectedItemId.getText().toString())) {
                                        ITEMID = jsonArrayItem.getJSONObject(i).getString("Id");
                                        SetItemName = jsonArrayItem.getJSONObject(i).getString("ItemName");
                                        SetPrimaryUnit = jsonArrayItem.getJSONObject(i).getString("Unit");
                                        SetAltUnit = jsonArrayItem.getJSONObject(i).getString("SaleUnit");

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            SelectedItem.setText(SetItemName);

                            if(CheckValidate.checkemptystring(SetPrimaryUnit)  != "")
                            {
                                SelectedPrimaryUnit.setText(CheckValidate.checkemptystring(SetPrimaryUnit));
                            }

                            if(CheckValidate.checkemptystring(SetAltUnit)  == "")
                            {

                                SelectedAltUnit.setText("");
                                edtAltUnitQty.setText("");
                                AltUnitQty = CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());
                                SelectedAltUnit.setEnabled(false);
                                edtAltUnitQty.setEnabled(false);

                            }
                            else
                            {

                                SelectedAltUnit.setEnabled(true);
                                edtAltUnitQty.setEnabled(true);
                                if(CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim()) >=0)
                                {
                                    AltUnitQty = CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());
                                }else{
                                    AltUnitQty = 0;
                                }

                                for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                                    try {
                                        if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID)) {

                                            if(!jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").isEmpty() || !jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals("null"))
                                            {
                                                ALTUNITARRAY.add(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias"));
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(ALTUNITARRAY.size() > 0)
                                {
                                    SelectedAltUnit.setText(CheckValidate.checkemptystring(SetAltUnit));

                                    for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                                        try {
                                            if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID) && jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals(SelectedAltUnit.getText().toString())) {

                                                AltUnitConversion = CheckValidate.checkemptyDouble(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitConversion"));

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                else
                                {
                                    AltUnitConversion = 0;
                                }
                            }

                            for (int i = 0; i < jsonArrayMRP.length(); i++) {
                                try {
                                    if (jsonArrayMRP.getJSONObject(i).getString("ItemId").equals(ITEMID) && CheckValidate.checkemptyDouble(jsonArrayMRP.getJSONObject(i).getString("Stock")) > 0) {

                                        if(!jsonArrayMRP.getJSONObject(i).getString("ItemMRP").isEmpty() || !jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals("null"))
                                        {
                                            ITEMIDARRAY.add(jsonArrayMRP.getJSONObject(i).getString("ItemMRP"));
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(ITEMIDARRAY.size() > 0)
                            {
                                SelectedMRP.setText(ITEMIDARRAY.get(0));
                            }
                            else
                            {
                                Toast.makeText(InVoiceDetailsActivity.this, "No stock available", Toast.LENGTH_LONG).show();
                                SelectedMRP.setText("");
                                edtRate.setError(null);
                                edtStock.setText("");
                                return;
                            }

                            if(SelectedPrimaryUnit.getText().toString().trim().length() > 0)
                            {
                                UDP = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                final int decPPU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtPrimaryUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                                edtFreeQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                            }

                            if(SelectedAltUnit.getText().toString().trim().length() > 0)
                            {
                                UDA = GetUnitDecimalPlaces(SelectedAltUnit.getText().toString().trim());
                                final int decPAU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtAltUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPAU)});
                            }

                            try {
                                for (int i = 0; i < jsonArrayMRP.length(); i++) {

                                    if (jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals(SelectedMRP.getText().toString()) && jsonArrayMRP.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString())) {
                                        getMRPDetails(jsonArrayMRP.getJSONObject(i).getString("ItemId"),jsonArrayMRP.getJSONObject(i).getString("MRPId"));
                                        break;
                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    mView.findViewById(R.id.layoutItemId).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            spItemId.showSpinerDialog();
                        }
                    });

                    //ItemCode Code END

                    spItem = new SpinnerDialog(InVoiceDetailsActivity.this, ITEMLIST,
                            "Select or Search Item");

                    spItem.setCancellable(true);
                    spItem.setShowKeyboard(false);

                    spItem.bindOnSpinerListener(new OnSpinerItemClick() {
                        @Override
                        public void onClick(String item, int position) {

                            ITEMIDARRAY.clear();
                            ALTUNITARRAY.clear();
                            ClearControl();
                            ClearGstControl();

                            if (null != item && item.length() > 0 )
                            {
                                int endIndex = item.lastIndexOf(" || ");
                                if (endIndex != -1)
                                {
                                    SetItemName = item.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
                                }
                            }
                            SelectedItem.setText(SetItemName);

                            if(CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim()) >=0)
                            {
                                PrimaryUnitQty = CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim());
                            }else{
                                PrimaryUnitQty = 0;
                            }


                            for (int i = 0; i < jsonArrayItem.length(); i++) {
                                try {
                                    if (jsonArrayItem.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString())) {
                                        ITEMID = jsonArrayItem.getJSONObject(i).getString("Id");
                                        SetPrimaryUnit = jsonArrayItem.getJSONObject(i).getString("Unit");
                                        SetAltUnit = jsonArrayItem.getJSONObject(i).getString("SaleUnit");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(!ITEMID.equals("0") || !ITEMID.isEmpty()) {
                                SelectedItemId.setText(ITEMID);
                            }
                            else
                            {
                                SelectedItemId.setText("Item ID");
                            }

                            if(CheckValidate.checkemptystring(SetPrimaryUnit)  != "")
                            {
                                SelectedPrimaryUnit.setText(CheckValidate.checkemptystring(SetPrimaryUnit));
                            }

                            if(CheckValidate.checkemptystring(SetAltUnit)  == "")
                            {
                                SelectedAltUnit.setText("");
                                edtAltUnitQty.setText("");
                                AltUnitQty = CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());
                                SelectedAltUnit.setEnabled(false);
                                edtAltUnitQty.setEnabled(false);

                            }
                            else
                            {

                                SelectedAltUnit.setEnabled(true);
                                edtAltUnitQty.setEnabled(true);

                                if(CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim()) >=0)
                                {
                                    AltUnitQty = CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());
                                }else{
                                    AltUnitQty = 0;
                                }

                                for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                                    try {
                                        if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID)) {

                                            if(!jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").isEmpty() || !jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals("null"))
                                            {
                                                ALTUNITARRAY.add(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias"));
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(ALTUNITARRAY.size() > 0)
                                {
                                    SelectedAltUnit.setText(CheckValidate.checkemptystring(SetAltUnit));

                                    for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                                        try {
                                            if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID) && jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals(SelectedAltUnit.getText().toString())) {

                                                AltUnitConversion = CheckValidate.checkemptyDouble(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitConversion"));

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                else
                                {
                                    AltUnitConversion = 0;
                                }
                            }

                            for (int i = 0; i < jsonArrayMRP.length(); i++) {
                                try {
                                    if (jsonArrayMRP.getJSONObject(i).getString("ItemId").equals(ITEMID) && CheckValidate.checkemptyDouble(jsonArrayMRP.getJSONObject(i).getString("Stock")) > 0) {

                                        if(!jsonArrayMRP.getJSONObject(i).getString("ItemMRP").isEmpty() || !jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals("null"))
                                        {
                                            ITEMIDARRAY.add(jsonArrayMRP.getJSONObject(i).getString("ItemMRP"));
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(ITEMIDARRAY.size() > 0)
                            {
                                SelectedMRP.setText(ITEMIDARRAY.get(0));
                            }
                            else
                            {
                                Toast.makeText(InVoiceDetailsActivity.this, "No stock available", Toast.LENGTH_LONG).show();
                                SelectedMRP.setText("");
                                edtStock.setText("");
                                edtRate.setError(null);
                                return;
                            }

                            if(SelectedPrimaryUnit.getText().toString().trim().length() > 0)
                            {
                                UDP = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                final int decPPU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtPrimaryUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                                edtFreeQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                            }

                            if(SelectedAltUnit.getText().toString().trim().length() > 0)
                            {
                                UDA = GetUnitDecimalPlaces(SelectedAltUnit.getText().toString().trim());
                                final int decPAU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtAltUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPAU)});
                            }

                            try {
                                for (int i = 0; i < jsonArrayMRP.length(); i++) {

                                    if (jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals(SelectedMRP.getText().toString()) && jsonArrayMRP.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString())) {
                                        getMRPDetails(jsonArrayMRP.getJSONObject(i).getString("ItemId"),jsonArrayMRP.getJSONObject(i).getString("MRPId"));
                                        break;
                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    mView.findViewById(R.id.layoutItem).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            spItem.showSpinerDialog();
                        }
                    });


                    spMRP = new SpinnerDialog(InVoiceDetailsActivity.this, ITEMIDARRAY,
                            "Select or Search MRP");

                    spMRP.setCancellable(true);
                    spMRP.setShowKeyboard(false);

                    spMRP.bindOnSpinerListener(new OnSpinerItemClick() {
                        @Override
                        public void onClick(String item, int position) {

                            SelectedMRP.setText(item);
                            ClearControl();
                            if(CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim()) >=0)
                            {
                                PrimaryUnitQty = CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim());
                            }else{
                                PrimaryUnitQty = 0;
                            }

                            if(CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim()) >=0)
                            {
                                AltUnitQty = CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());
                            }else{
                                AltUnitQty = 0;
                            }
                            ClearGstControl();
                            ////start

                            if(SelectedPrimaryUnit.getText().toString().trim().length() > 0)
                            {
                                UDP = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                final int decPPU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtPrimaryUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                                edtFreeQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                            }

                            if(SelectedAltUnit.getText().toString().trim().length() > 0)
                            {
                                UDA = GetUnitDecimalPlaces(SelectedAltUnit.getText().toString().trim());
                                final int decPAU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtAltUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPAU)});
                            }

                            try {
                                for (int i = 0; i < jsonArrayMRP.length(); i++) {

                                    if (jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals(SelectedMRP.getText().toString()) && jsonArrayMRP.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString())) {
                                        getMRPDetails(jsonArrayMRP.getJSONObject(i).getString("ItemId"),jsonArrayMRP.getJSONObject(i).getString("MRPId"));
                                        break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ////end

                        }
                    });


                    mView.findViewById(R.id.layoutMRP).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            spMRP.showSpinerDialog();
                        }
                    });

                    spAltUnit = new SpinnerDialog(InVoiceDetailsActivity.this, ALTUNITARRAY,
                            "Select");

                    spAltUnit.setCancellable(true);
                    spAltUnit.setShowKeyboard(false);

                    spAltUnit.bindOnSpinerListener(new OnSpinerItemClick() {
                        @Override
                        public void onClick(String item, int position) {
                            SelectedAltUnit.setText(item);


                            for (int i = 0; i < jsonArrayAlternetUnit.length(); i++) {
                                try {
                                    if (jsonArrayAlternetUnit.getJSONObject(i).getString("ItemID").equals(ITEMID) && jsonArrayAlternetUnit.getJSONObject(i).getString("UnitAlias").equals(SelectedAltUnit.getText().toString())) {

                                        AltUnitConversion = CheckValidate.checkemptyDouble(jsonArrayAlternetUnit.getJSONObject(i).getString("UnitConversion"));

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(SelectedPrimaryUnit.getText().toString().trim().length() > 0)
                            {
                                UDP = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                final int decPPU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtPrimaryUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                                edtFreeQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPPU)});
                            }

                            if(SelectedAltUnit.getText().toString().trim().length() > 0)
                            {
                                UDA = GetUnitDecimalPlaces(SelectedAltUnit.getText().toString().trim());
                                final int decPAU = GetUnitDecimalPlaces(SelectedPrimaryUnit.getText().toString().trim());
                                edtAltUnitQty.setFilters(new InputFilter[]{new InputFilterDecimal(12,decPAU)});
                            }

                            try{

                                edtAmount.setError(null);
                                double tmpAltUnitQty =  CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());
                                if(tmpAltUnitQty > 0) {
                                    if (UDA > 0) {
                                        edtAltUnitQty.setText(String.format("%." + UDA + "f", tmpAltUnitQty));
                                    } else {
                                        edtAltUnitQty.setText(String.format("%.0f", tmpAltUnitQty));
                                    }

                                }
                                AltUnitQty = CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());
                                PrimaryUnitQty =  CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim());


                                double tmpTotalQty;
                                if(AltUnitQty <= 0)
                                {
                                    tmpTotalQty = PrimaryUnitQty;

                                }else
                                {
                                    tmpTotalQty = (AltUnitQty * AltUnitConversion) + PrimaryUnitQty;
                                }
                                String strTotalQty = String.format("%."+UDP+"f",tmpTotalQty);
                                edtTotalQty.setText(strTotalQty);
                                TotalQty = CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim());

                                double tmpGross = TotalQty * Rate;
                                String gross = String.format("%.2f", tmpGross);
                                edtGross.setText(gross);
                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                if (DiscPer > 0) {
                                    double tmpDisc = ((Gross * DiscPer) / 100);
                                    if (tmpDisc > 0) {
                                        String txtDisc = String.format("%.2f", tmpDisc);
                                        edtDisc.setText(txtDisc);
                                    } else {
                                        edtDisc.setText("");
                                    }
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                } else {
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                }

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                if (DiscIIPer > 0) {
                                    double tmpDiscII = (((Gross - Disc) * DiscIIPer) / 100);
                                    if (tmpDiscII > 0) {
                                        String txtDiscII = String.format("%.2f", tmpDiscII);
                                        edtDiscII.setText(txtDiscII);

                                    } else {
                                        edtDiscII.setText("");
                                    }
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                } else {
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                }

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                if (DiscIIIPer > 0) {

                                    double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                    if (tmpDiscIII > 0) {
                                        String txtDiscIII = String.format("%.2f", tmpDiscIII);
                                        edtDiscIII.setText(txtDiscIII);
                                    } else {
                                        edtDiscIII.setText("");
                                    }
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                } else {
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }

                                TotalDisc = Disc + DiscII + DiscIII;
                                if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                {
                                    edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                }else
                                {
                                    edtTotalDisc.setText("");
                                }

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                if (OtherPer > 0) {
                                    double tmpOther = (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                    if (tmpOther > 0) {
                                        String txtOther = String.format("%.2f", tmpOther);
                                        edtOther.setText(txtOther);
                                    } else {
                                        edtOther.setText("");
                                    }
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                } else {
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if (OtherIIPer > 0) {
                                    double tmpOtherII = (((Gross - Disc - DiscII - DiscIII + Other) * OtherIIPer) / 100);
                                    if (tmpOtherII > 0) {
                                        String txtOtherII = String.format("%.2f", tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    } else {
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                } else {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if (cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f", tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                } else {
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if (sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f", tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                } else {
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if (igstP > 0) {
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f", tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                } else {
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f", totalAmount);
                                edtAmount.setText(totalAmt);

                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                    mView.findViewById(R.id.LayoutAltUnit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            spAltUnit.showSpinerDialog();
                        }
                    });



                    //start Textchanged

                    edtAltUnitQty.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try{

                                edtAmount.setError(null);
                                if (s.toString().trim().length() == 0 ) {
                                }


                                AltUnitQty =  CheckValidate.checkemptyDouble(s.toString());
                                PrimaryUnitQty =  CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim());

                                double tmpTotalQty;
                                if(AltUnitQty <= 0)
                                {
                                    tmpTotalQty = PrimaryUnitQty;

                                }else
                                {
                                    tmpTotalQty = (AltUnitQty * AltUnitConversion) + PrimaryUnitQty;
                                }
                                String strTotalQty = String.format("%."+UDP+"f",tmpTotalQty);
                                edtTotalQty.setText(strTotalQty);
                                TotalQty = CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim());

                                double tmpGross = TotalQty * Rate;
                                String gross = String.format("%.2f", tmpGross);
                                edtGross.setText(gross);
                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                if (DiscPer > 0) {
                                    double tmpDisc = ((Gross * DiscPer) / 100);
                                    if (tmpDisc > 0) {
                                        String txtDisc = String.format("%.2f", tmpDisc);
                                        edtDisc.setText(txtDisc);
                                    } else {
                                        edtDisc.setText("");
                                    }
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                } else {
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                }

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                if (DiscIIPer > 0) {
                                    double tmpDiscII = (((Gross - Disc) * DiscIIPer) / 100);
                                    if (tmpDiscII > 0) {
                                        String txtDiscII = String.format("%.2f", tmpDiscII);
                                        edtDiscII.setText(txtDiscII);

                                    } else {
                                        edtDiscII.setText("");
                                    }
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                } else {
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                }

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                if (DiscIIIPer > 0) {

                                    double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                    if (tmpDiscIII > 0) {
                                        String txtDiscIII = String.format("%.2f", tmpDiscIII);
                                        edtDiscIII.setText(txtDiscIII);
                                    } else {
                                        edtDiscIII.setText("");
                                    }
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                } else {
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }

                                TotalDisc = Disc + DiscII + DiscIII;
                                if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                {
                                    edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                }else
                                {
                                    edtTotalDisc.setText("");
                                }

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                if (OtherPer > 0) {
                                    double tmpOther = (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                    if (tmpOther > 0) {
                                        String txtOther = String.format("%.2f", tmpOther);
                                        edtOther.setText(txtOther);
                                    } else {
                                        edtOther.setText("");
                                    }
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                } else {
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if (OtherIIPer > 0) {
                                    double tmpOtherII = (((Gross - Disc - DiscII - DiscIII + Other) * OtherIIPer) / 100);
                                    if (tmpOtherII > 0) {
                                        String txtOtherII = String.format("%.2f", tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    } else {
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                } else {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if (cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f", tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                } else {
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if (sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f", tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                } else {
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if (igstP > 0) {
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f", tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                } else {
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f", totalAmount);
                                edtAmount.setText(totalAmt);

                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


                    edtPrimaryUnitQty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            edtPrimaryUnitQty.setError(null);
                            edtPrimaryUnitQty.setCompoundDrawables(null, null, null, null);

                        }
                    });

                    edtPrimaryUnitQty.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try{

                                edtAmount.setError(null);
                                if (s.toString().trim().length() == 0 ) {
                                }

                                PrimaryUnitQty = CheckValidate.checkemptyDouble(s.toString());
                                AltUnitQty = CheckValidate.checkemptyDouble(edtAltUnitQty.getText().toString().trim());

                                double tmpTotalQty;
                                if(AltUnitQty <= 0)
                                {
                                    tmpTotalQty = PrimaryUnitQty;

                                }else
                                {
                                    tmpTotalQty = (AltUnitQty * AltUnitConversion) + PrimaryUnitQty;
                                }
                                String strTotalQty = String.format("%."+UDP+"f",tmpTotalQty);
                                edtTotalQty.setText(strTotalQty);
                                TotalQty = CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim());

                                double tmpGross = TotalQty * Rate;
                                String gross = String.format("%.2f", tmpGross);
                                edtGross.setText(gross);
                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                if (DiscPer > 0) {
                                    double tmpDisc = ((Gross * DiscPer) / 100);
                                    if (tmpDisc > 0) {
                                        String txtDisc = String.format("%.2f", tmpDisc);
                                        edtDisc.setText(txtDisc);
                                    } else {
                                        edtDisc.setText("");
                                    }
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                } else {
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                }

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                if (DiscIIPer > 0) {
                                    double tmpDiscII = (((Gross - Disc) * DiscIIPer) / 100);
                                    if (tmpDiscII > 0) {
                                        String txtDiscII = String.format("%.2f", tmpDiscII);
                                        edtDiscII.setText(txtDiscII);

                                    } else {
                                        edtDiscII.setText("");
                                    }
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                } else {
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                }

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                if (DiscIIIPer > 0) {

                                    double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                    if (tmpDiscIII > 0) {
                                        String txtDiscIII = String.format("%.2f", tmpDiscIII);
                                        edtDiscIII.setText(txtDiscIII);
                                    } else {
                                        edtDiscIII.setText("");
                                    }
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                } else {
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }

                                TotalDisc = Disc + DiscII + DiscIII;
                                if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                {
                                    edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                }else
                                {
                                    edtTotalDisc.setText("");
                                }

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                if (OtherPer > 0) {
                                    double tmpOther = (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                    if (tmpOther > 0) {
                                        String txtOther = String.format("%.2f", tmpOther);
                                        edtOther.setText(txtOther);
                                    } else {
                                        edtOther.setText("");
                                    }
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                } else {
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if (OtherIIPer > 0) {
                                    double tmpOtherII = (((Gross - Disc - DiscII - DiscIII + Other) * OtherIIPer) / 100);
                                    if (tmpOtherII > 0) {
                                        String txtOtherII = String.format("%.2f", tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    } else {
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                } else {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if (cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f", tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                } else {
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if (sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f", tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                } else {
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if (igstP > 0) {
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f", tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                } else {
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f", totalAmount);
                                edtAmount.setText(totalAmt);

                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    edtRate.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try
                            {
                                edtAmount.setError(null);
                                if (s.toString().trim().length() == 0 || CheckValidate.checkemptyDouble(s.toString()) < 0 ) {
                                    edtRate.setError("Rate blank not allowed");
                                }
                                else {
                                    edtRate.setError(null);
                                    edtRate.setCompoundDrawables(null, null, null, null);
                                }

                                double TotalQty = CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim());
                                Rate = CheckValidate.checkemptyDouble(s.toString());
                                double tmpGross = TotalQty * Rate;
                                String gross = String.format("%.2f",tmpGross);
                                edtGross.setText(gross);
                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                if(DiscPer > 0)
                                {
                                    double  tmpDisc = ((Gross * DiscPer) / 100);
                                    if(tmpDisc > 0 ){
                                        String txtDisc = String.format("%.2f",tmpDisc);
                                        edtDisc.setText(txtDisc);
                                    }else{
                                        edtDisc.setText("");
                                    }
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                }else
                                {
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                }

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                if(DiscIIPer > 0)
                                {
                                    double  tmpDiscII = (((Gross-Disc) * DiscIIPer) / 100);
                                    if(tmpDiscII > 0){
                                        String txtDiscII = String.format("%.2f",tmpDiscII);
                                        edtDiscII.setText(txtDiscII);

                                    }else{
                                        edtDiscII.setText("");
                                    }
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                }else
                                {
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                }

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                if (DiscIIIPer > 0) {

                                    double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                    if(tmpDiscIII > 0){
                                        String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                        edtDiscIII.setText(txtDiscIII);
                                    }else{
                                        edtDiscIII.setText("");
                                    }
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }else
                                {
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }

                                TotalDisc = Disc + DiscII + DiscIII;
                                if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                {
                                    edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                }else
                                {
                                    edtTotalDisc.setText("");
                                }

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                if(OtherPer > 0){
                                    double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                    if(tmpOther > 0){
                                        String txtOther = String.format("%.2f",tmpOther);
                                        edtOther.setText(txtOther);
                                    }else{
                                        edtOther.setText("");
                                    }
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }else
                                {
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if(OtherIIPer > 0)
                                {
                                    double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                    if(tmpOtherII > 0){
                                        String txtOtherII = String.format("%.2f",tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    }else{
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }else
                                {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if(cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f",tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                }else{
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if(sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f",tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                }else{
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if(igstP > 0){
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f",tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                }else{
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f",totalAmount);
                                edtAmount.setText(totalAmt);
                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    edtDiscPer.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try
                            {
                                edtAmount.setError(null);
                                if (s.toString().trim().length()!=0) {

                                    edtDisc.setEnabled(false);
                                }
                                else {
                                    edtDisc.setEnabled(true);
                                }

                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(s.toString());
                                double  tmpDisc = ((Gross * DiscPer) / 100);
                                if(tmpDisc > 0){
                                    String txtDisc = String.format("%.2f",tmpDisc);
                                    edtDisc.setText(txtDisc);
                                }else{
                                    edtDisc.setText("");
                                }
                                Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                if(DiscIIPer > 0)
                                {
                                    double  tmpDiscII = (((Gross-Disc) * DiscIIPer) / 100);
                                    if(tmpDiscII > 0){
                                        String txtDiscII = String.format("%.2f",tmpDiscII);
                                        edtDiscII.setText(txtDiscII);

                                    }else{
                                        edtDiscII.setText("");
                                    }
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                }else
                                {
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                }

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                if (DiscIIIPer > 0) {

                                    double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                    if(tmpDiscIII > 0){
                                        String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                        edtDiscIII.setText(txtDiscIII);
                                    }else{
                                        edtDiscIII.setText("");
                                    }
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }else
                                {
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }

                                TotalDisc = Disc + DiscII + DiscIII;
                                if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                {
                                    edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                }else
                                {
                                    edtTotalDisc.setText("");
                                }

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                if(OtherPer > 0){
                                    double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                    if(tmpOther > 0){
                                        String txtOther = String.format("%.2f",tmpOther);
                                        edtOther.setText(txtOther);
                                    }else{
                                        edtOther.setText("");
                                    }
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }else
                                {
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if(OtherIIPer > 0)
                                {
                                    double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                    if(tmpOtherII > 0){
                                        String txtOtherII = String.format("%.2f",tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    }else{
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }else
                                {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if(cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f",tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                }else{
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if(sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f",tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                }else{
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if(igstP > 0){
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f",tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                }else{
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f",totalAmount);
                                edtAmount.setText(totalAmt);

                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    edtDisc.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try
                            {
                                edtAmount.setError(null);
                                if (s.toString().trim().length()!=0) {
                                }

                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                Disc = CheckValidate.checkemptyDouble(s.toString());

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                if(DiscIIPer > 0)
                                {
                                    double  tmpDiscII = (((Gross-Disc) * DiscIIPer) / 100);
                                    if(tmpDiscII > 0){
                                        String txtDiscII = String.format("%.2f",tmpDiscII);
                                        edtDiscII.setText(txtDiscII);

                                    }else{
                                        edtDiscII.setText("");
                                    }
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                }else
                                {
                                    DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                }

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                if (DiscIIIPer > 0) {

                                    double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                    if(tmpDiscIII > 0){
                                        String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                        edtDiscIII.setText(txtDiscIII);
                                    }else{
                                        edtDiscIII.setText("");
                                    }
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }else
                                {
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }

                                TotalDisc = Disc + DiscII + DiscIII;
                                if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                {
                                    edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                }else
                                {
                                    edtTotalDisc.setText("");
                                }

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                if(OtherPer > 0){
                                    double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                    if(tmpOther > 0){
                                        String txtOther = String.format("%.2f",tmpOther);
                                        edtOther.setText(txtOther);
                                    }else{
                                        edtOther.setText("");
                                    }
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }else
                                {
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if(OtherIIPer > 0)
                                {
                                    double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                    if(tmpOtherII > 0){
                                        String txtOtherII = String.format("%.2f",tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    }else{
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }else
                                {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if(cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f",tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                }else{
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if(sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f",tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                }else{
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if(igstP > 0){
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f",tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                }else{
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f",totalAmount);
                                edtAmount.setText(totalAmt);
                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    edtDiscIIPer.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try
                            {
                                edtAmount.setError(null);
                                if (s.toString().trim().length()!=0) {

                                    edtDiscII.setEnabled(false);
                                }
                                else {
                                    edtDiscII.setEnabled(true);
                                }

                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                DiscIIPer = CheckValidate.checkemptyDouble(s.toString());
                                double  tmpDiscII = (((Gross - Disc) * DiscIIPer) / 100);
                                if(tmpDiscII > 0){
                                    String txtDiscII = String.format("%.2f",tmpDiscII);
                                    edtDiscII.setText(txtDiscII);
                                }else{
                                    edtDiscII.setText("");
                                }

                                DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                if (DiscIIIPer > 0) {

                                    double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                    if(tmpDiscIII > 0){
                                        String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                        edtDiscIII.setText(txtDiscIII);
                                    }else{
                                        edtDiscIII.setText("");
                                    }
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }else
                                {
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }

                                TotalDisc = Disc + DiscII + DiscIII;
                                if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                {
                                    edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                }else
                                {
                                    edtTotalDisc.setText("");
                                }

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                if(OtherPer > 0){
                                    double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                    if(tmpOther > 0){
                                        String txtOther = String.format("%.2f",tmpOther);
                                        edtOther.setText(txtOther);
                                    }else{
                                        edtOther.setText("");
                                    }
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }else
                                {
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if(OtherIIPer > 0)
                                {
                                    double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                    if(tmpOtherII > 0){
                                        String txtOtherII = String.format("%.2f",tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    }else{
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }else
                                {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if(cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f",tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                }else{
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if(sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f",tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                }else{
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if(igstP > 0){
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f",tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                }else{
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f",totalAmount);
                                edtAmount.setText(totalAmt);
                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    edtDiscII.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try
                            {
                                edtAmount.setError(null);
                                if (s.toString().trim().length()!=0) {
                                }

                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                DiscII = CheckValidate.checkemptyDouble(s.toString());

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                if (DiscIIIPer > 0) {

                                    double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                    if(tmpDiscIII > 0){
                                        String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                        edtDiscIII.setText(txtDiscIII);
                                    }else{
                                        edtDiscIII.setText("");
                                    }
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }else
                                {
                                    DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                }

                                TotalDisc = Disc + DiscII + DiscIII;
                                if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                {
                                    edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                }else
                                {
                                    edtTotalDisc.setText("");
                                }

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                if(OtherPer > 0){
                                    double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                    if(tmpOther > 0){
                                        String txtOther = String.format("%.2f",tmpOther);
                                        edtOther.setText(txtOther);
                                    }else{
                                        edtOther.setText("");
                                    }
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }else
                                {
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if(OtherIIPer > 0)
                                {
                                    double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                    if(tmpOtherII > 0){
                                        String txtOtherII = String.format("%.2f",tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    }else{
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }else
                                {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if(cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f",tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                }else{
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if(sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f",tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                }else{
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if(igstP > 0){
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f",tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                }else{
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f",totalAmount);
                                edtAmount.setText(totalAmt);
                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    edtDiscIIIPer.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try
                            {
                                edtAmount.setError(null);
                                if (s.toString().trim().length()!=0) {
                                    edtDiscIII.setEnabled(false);
                                }
                                else {
                                    edtDiscIII.setEnabled(true);
                                }

                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                DiscIIIPer = CheckValidate.checkemptyDouble(s.toString());
                                double  tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                if(tmpDiscIII > 0){
                                    String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                    edtDiscIII.setText(txtDiscIII);
                                }else{
                                    edtDiscIII.setText("");
                                }
                                DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                TotalDisc = Disc + DiscII + DiscIII;
                                if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                {
                                    edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                }else
                                {
                                    edtTotalDisc.setText("");
                                }

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                if(OtherPer > 0){
                                    double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                    if(tmpOther > 0){
                                        String txtOther = String.format("%.2f",tmpOther);
                                        edtOther.setText(txtOther);
                                    }else{
                                        edtOther.setText("");
                                    }
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }else
                                {
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if(OtherIIPer > 0)
                                {
                                    double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                    if(tmpOtherII > 0){
                                        String txtOtherII = String.format("%.2f",tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    }else{
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }else
                                {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if(cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f",tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                }else{
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if(sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f",tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                }else{
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if(igstP > 0){
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f",tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                }else{
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f",totalAmount);
                                edtAmount.setText(totalAmt);
                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    edtDiscIII.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try
                            {
                                edtAmount.setError(null);
                                if (s.toString().trim().length()!=0) {
                                }

                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                DiscIII = CheckValidate.checkemptyDouble(s.toString());

                                TotalDisc = Disc + DiscII + DiscIII;
                                if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                {
                                    edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                }else
                                {
                                    edtTotalDisc.setText("");
                                }

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                if(OtherPer > 0){
                                    double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                    if(tmpOther > 0){
                                        String txtOther = String.format("%.2f",tmpOther);
                                        edtOther.setText(txtOther);
                                    }else{
                                        edtOther.setText("");
                                    }
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }else
                                {
                                    Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                }

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if(OtherIIPer > 0)
                                {
                                    double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                    if(tmpOtherII > 0){
                                        String txtOtherII = String.format("%.2f",tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    }else{
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }else
                                {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if(cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f",tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                }else{
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if(sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f",tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                }else{
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if(igstP > 0){
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f",tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                }else{
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f",totalAmount);
                                edtAmount.setText(totalAmt);
                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    edtOtherPer.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try
                            {
                                edtAmount.setError(null);
                                if (s.toString().trim().length()!=0) {

                                    edtOther.setEnabled(false);

                                }
                                else {
                                    edtOther.setEnabled(true);
                                }

                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());

                                OtherPer = CheckValidate.checkemptyDouble(s.toString());
                                double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                if(tmpOther> 0) {
                                    String txtOther = String.format("%.2f",tmpOther);
                                    edtOther.setText(txtOther);
                                }else{
                                    edtOther.setText("");
                                }
                                Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if(OtherIIPer > 0)
                                {
                                    double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                    if(tmpOtherII > 0){
                                        String txtOtherII = String.format("%.2f",tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    }else{
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }else
                                {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if(cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f",tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                }else{
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if(sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f",tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                }else{
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if(igstP > 0){
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f",tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                }else{
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f",totalAmount);
                                edtAmount.setText(totalAmt);
                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    edtOther.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try
                            {
                                edtAmount.setError(null);
                                if (s.toString().trim().length()!=0) {
                                }

                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                Other = CheckValidate.checkemptyDouble(s.toString());

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                if(OtherIIPer > 0)
                                {
                                    double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                    if(tmpOtherII > 0){
                                        String txtOtherII = String.format("%.2f",tmpOtherII);
                                        edtOtherII.setText(txtOtherII);
                                    }else{
                                        edtOtherII.setText("");
                                    }
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }else
                                {
                                    OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                }

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if(cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f",tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                }else{
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if(sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f",tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                }else{
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if(igstP > 0){
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f",tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                }else{
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f",totalAmount);
                                edtAmount.setText(totalAmt);
                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    edtOtherIIPer.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try
                            {
                                edtAmount.setError(null);
                                if (s.toString().trim().length()!=0) {

                                    edtOtherII.setEnabled(false);
                                }
                                else {
                                    edtOtherII.setEnabled(true);
                                }

                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());

                                OtherIIPer = CheckValidate.checkemptyDouble(s.toString());
                                double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                if(tmpOtherII > 0){
                                    String txtOtherII = String.format("%.2f",tmpOtherII);
                                    edtOtherII.setText(txtOtherII);
                                }else{
                                    edtOtherII.setText("");
                                }
                                OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if(cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f",tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                }else{
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if(sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f",tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                }else{
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if(igstP > 0){
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f",tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                }else{
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f",totalAmount);
                                edtAmount.setText(totalAmt);
                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    edtOtherII.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try
                            {
                                edtAmount.setError(null);
                                if (s.toString().trim().length()!=0) {
                                }

                                Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                                DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());

                                OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());

                                OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                OtherII = CheckValidate.checkemptyDouble(s.toString());

                                cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                                double cgst = 0;
                                if(cgstP > 0) {
                                    double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                    String txtcgst = String.format("%.2f",tmpCgst);
                                    edtCGST.setText(txtcgst);
                                    cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                }else{
                                    edtCGST.setText("");
                                }

                                double sgst = 0;
                                if(sgstP > 0) {
                                    double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                    String txtsgst = String.format("%.2f",tmpSgst);
                                    edtSGST.setText(txtsgst);
                                    sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                }else{
                                    edtSGST.setText("");
                                }

                                double igst = 0;
                                if(igstP > 0){
                                    double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                    String txtigst = String.format("%.2f",tmpIgst);
                                    edtIGST.setText(txtigst);
                                    igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                }else{
                                    edtIGST.setText("");
                                }

                                double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                String totalAmt = String.format("%.2f",totalAmount);
                                edtAmount.setText(totalAmt);
                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    //End Textchanged
                    btnMGDisc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try
                            {
                                edtAmount.setError(null);

                                DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                                if(DiscPer == 0)
                                {
                                    Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());
                                    cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                                    sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                                    igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());
                                    double tmpgstPer = 0;
                                    if(cgstP > 0 && sgstP > 0)
                                    {
                                        tmpgstPer = cgstP+sgstP;
                                    }
                                    else
                                    {
                                        tmpgstPer = igstP;
                                    }

                                    if(tmpgstPer > 0)
                                    {
                                        double  tmpDisc = (Disc /((1+(tmpgstPer/100)))) ;

                                        if(tmpDisc > 0){
                                            String txtDisc= String.format("%.2f",tmpDisc);
                                            edtDisc.setText(txtDisc);

                                        }else{
                                            edtDisc.setText("");
                                        }
                                    }
                                    Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                                    DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                                    if(DiscIIPer > 0)
                                    {
                                        double  tmpDiscII = (((Gross-Disc) * DiscIIPer) / 100);
                                        if(tmpDiscII > 0){
                                            String txtDiscII = String.format("%.2f",tmpDiscII);
                                            edtDiscII.setText(txtDiscII);

                                        }else{
                                            edtDiscII.setText("");
                                        }
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    }else
                                    {
                                        DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());
                                    }

                                    DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                                    if (DiscIIIPer > 0) {

                                        double tmpDiscIII = (((Gross - Disc - DiscII) * DiscIIIPer) / 100);
                                        if(tmpDiscIII > 0){
                                            String txtDiscIII = String.format("%.2f",tmpDiscIII);
                                            edtDiscIII.setText(txtDiscIII);
                                        }else{
                                            edtDiscIII.setText("");
                                        }
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }else
                                    {
                                        DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());
                                    }

                                    TotalDisc = Disc + DiscII + DiscIII;
                                    if(CheckValidate.checkemptyDouble(String.valueOf(TotalDisc)) > 0)
                                    {
                                        edtTotalDisc.setText(String.format("%.2f",TotalDisc));
                                    }else
                                    {
                                        edtTotalDisc.setText("");
                                    }

                                    OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                                    if(OtherPer > 0){
                                        double  tmpOther= (((Gross - Disc - DiscII - DiscIII) * OtherPer) / 100);
                                        if(tmpOther > 0){
                                            String txtOther = String.format("%.2f",tmpOther);
                                            edtOther.setText(txtOther);
                                        }else{
                                            edtOther.setText("");
                                        }
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }else
                                    {
                                        Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());
                                    }

                                    OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                                    if(OtherIIPer > 0)
                                    {
                                        double  tmpOtherII= (((Gross - Disc - DiscII - DiscIII + Other ) * OtherIIPer) / 100);
                                        if(tmpOtherII > 0){
                                            String txtOtherII = String.format("%.2f",tmpOtherII);
                                            edtOtherII.setText(txtOtherII);
                                        }else{
                                            edtOtherII.setText("");
                                        }
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }else
                                    {
                                        OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());
                                    }

                                    double cgst = 0;
                                    if(cgstP > 0) {
                                        double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                        String txtcgst = String.format("%.2f",tmpCgst);
                                        edtCGST.setText(txtcgst);
                                        cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                                    }else{
                                        edtCGST.setText("");
                                    }

                                    double sgst = 0;
                                    if(sgstP > 0) {
                                        double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                        String txtsgst = String.format("%.2f",tmpSgst);
                                        edtSGST.setText(txtsgst);
                                        sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                                    }else{
                                        edtSGST.setText("");
                                    }

                                    double igst = 0;
                                    if(igstP > 0){
                                        double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                        String txtigst = String.format("%.2f",tmpIgst);
                                        edtIGST.setText(txtigst);
                                        igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                                    }else{
                                        edtIGST.setText("");
                                    }

                                    double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                                    String totalAmt = String.format("%.2f",totalAmount);
                                    edtAmount.setText(totalAmt);
                                }


                            }catch (NumberFormatException e){
                                Toast.makeText(InVoiceDetailsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();

                        }
                    });
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (SelectedItem.getText().toString().trim().length() == 0 || SelectedItem.getText().toString().trim().equals("Select Item") ) {
                                ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Must select item.");
                                return;
                            }else if(CheckValidate.checkemptyDouble(SelectedMRP.getText().toString().trim()) <= 0 || SelectedItem.getText().toString().trim().equals("Select MRP")){
                                ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","MRP zero not allow.");
                                return;
                            }else if(CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim()) <= 0 && CheckValidate.checkemptyDouble(edtFreeQty.getText().toString().trim()) <= 0 ) {
                                edtPrimaryUnitQty.setError("Qty Blank not allow.");
                                return;
                            }else if(edtRate.getText().toString().trim().length() == 0){
                                edtRate.setError("Rate Blank not allow.");
                                return;
                            }else if(CheckValidate.checkemptyDouble(edtAmount.getText().toString().trim()) < 0 || edtAmount.getText().toString().trim().length() == 0){
                                edtAmount.setError("Negative Billing not allow.");
                                ShowAlert.ShowAlert(InVoiceDetailsActivity.this,"Alert...","Negative Billing not allow.");
                                return;
                            }
                            else{
                                String ItemID = null, MRPID = null, strPrimaryUnitId = null, strAltUnitId = null;
                                for (int i = 0; i < jsonArrayItem.length(); i++) {
                                    try {
                                        if (jsonArrayItem.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString().trim())) {

                                            ItemID = jsonArrayItem.getJSONObject(i).getString("Id");

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                for (int i = 0; i < jsonArrayUnits.length(); i++) {
                                    try {
                                        if (jsonArrayUnits.getJSONObject(i).getString("UnitAlias").equals(SelectedPrimaryUnit.getText().toString().trim())) {

                                            strPrimaryUnitId = jsonArrayUnits.getJSONObject(i).getString("UnitID");

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                for (int i = 0; i < jsonArrayUnits.length(); i++) {
                                    try {
                                        if (jsonArrayUnits.getJSONObject(i).getString("UnitAlias").equals(SelectedAltUnit.getText().toString().trim())) {

                                            strAltUnitId = jsonArrayUnits.getJSONObject(i).getString("UnitID");

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                for (int i = 0; i < jsonArrayMRP.length(); i++) {
                                    try {
                                        if (jsonArrayMRP.getJSONObject(i).getString("ItemName").equals(SelectedItem.getText().toString().trim()) && jsonArrayMRP.getJSONObject(i).getString("ItemMRP").equals(SelectedMRP.getText().toString().trim())) {

                                            MRPID = jsonArrayMRP.getJSONObject(i).getString("MRPId");

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                                try {

                                    jobjPerticularPosition.put("ItemId", ItemID);
                                    jobjPerticularPosition.put("ItemName", SelectedItem.getText().toString());
                                    jobjPerticularPosition.put("MRPId", MRPID);
                                    jobjPerticularPosition.put("ItemMRP", SelectedMRP.getText().toString());
                                    jobjPerticularPosition.put("AlternetUnitID", strAltUnitId);
                                    jobjPerticularPosition.put("AlternetUnit", SelectedAltUnit.getText().toString().trim());
                                    jobjPerticularPosition.put("AlternetUnitQty", edtAltUnitQty.getText().toString().trim());
                                    jobjPerticularPosition.put("PrimaryUnitID", strPrimaryUnitId);
                                    jobjPerticularPosition.put("PrimaryUnit", SelectedPrimaryUnit.getText().toString().trim());
                                    jobjPerticularPosition.put("PrimaryUnitQty", edtPrimaryUnitQty.getText().toString().trim());
                                    jobjPerticularPosition.put("TotalQty", edtTotalQty.getText().toString().trim());
                                    jobjPerticularPosition.put("FreeQty", edtFreeQty.getText().toString());
                                    jobjPerticularPosition.put("Rate", edtRate.getText().toString());
                                    jobjPerticularPosition.put("Gross", edtGross.getText().toString());
                                    jobjPerticularPosition.put("DiscountPer", edtDiscPer.getText().toString());
                                    jobjPerticularPosition.put("Discount", edtDisc.getText().toString());
                                    jobjPerticularPosition.put("DiscountIIPer", edtDiscIIPer.getText().toString());
                                    jobjPerticularPosition.put("DiscountII", edtDiscII.getText().toString());
                                    jobjPerticularPosition.put("DiscountIIIPer", edtDiscIIIPer.getText().toString());
                                    jobjPerticularPosition.put("DiscountIII", edtDiscIII.getText().toString());
                                    jobjPerticularPosition.put("OtherPer", edtOtherPer.getText().toString());
                                    jobjPerticularPosition.put("Other", edtOther.getText().toString());
                                    jobjPerticularPosition.put("OtherIIPer", edtOtherIIPer.getText().toString());
                                    jobjPerticularPosition.put("OtherII", edtOtherII.getText().toString());
                                    jobjPerticularPosition.put("CGSTAccountID", edtCGSTACID.getText().toString().trim());
                                    jobjPerticularPosition.put("CGSTPer", edtCGSTP.getText().toString().trim());
                                    jobjPerticularPosition.put("CGSTAmt", edtCGST.getText().toString());
                                    jobjPerticularPosition.put("SGSTAccountID", edtSGSTACID.getText().toString().trim());
                                    jobjPerticularPosition.put("SGSTPer", edtSGSTP.getText().toString().trim());
                                    jobjPerticularPosition.put("SGSTAmt", edtSGST.getText().toString());
                                    jobjPerticularPosition.put("IGSTAccountID", edtIGSTACID.getText().toString().trim());
                                    jobjPerticularPosition.put("IGSTPer", edtIGSTP.getText().toString().trim());
                                    jobjPerticularPosition.put("IGSTAmt", edtIGST.getText().toString());
                                    jobjPerticularPosition.put("NetAmount", edtAmount.getText().toString());



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                adapter.notifyDataSetChanged();
                                calculateSum();

                                dialog.dismiss();

                                tvItem.setText(SelectedItem.getText().toString().trim());
                                tvMRP.setText(SelectedMRP.getText().toString().trim());
                                tvAlternetQtyUnit.setText(SelectedAltUnit.getText().toString().trim());
                                tvAlternetQty.setText(edtAltUnitQty.getText().toString().trim());
                                tvPrimaryQtyUnit.setText(SelectedPrimaryUnit.getText().toString().trim());
                                tvPrimaryQty.setText(edtPrimaryUnitQty.getText().toString().trim());
                                tvTotalQty.setText(edtTotalQty.getText().toString().trim());
                                tvFreeQty.setText(CheckValidate.checkemptyTV(edtFreeQty.getText().toString().trim()));
                                tvRate.setText(edtRate.getText().toString().trim());
                                tvGross.setText(CheckValidate.checkemptyTV(edtGross.getText().toString().trim()));
                                tvDiscPer.setText(CheckValidate.checkemptyTV(edtDiscPer.getText().toString().trim()));
                                tvDisc.setText(CheckValidate.checkemptyTV(edtDisc.getText().toString().trim()));
                                tvDiscIIPer.setText(CheckValidate.checkemptyTV(edtDiscIIPer.getText().toString().trim()));
                                tvDiscII.setText(CheckValidate.checkemptyTV(edtDiscII.getText().toString().trim()));
                                tvDiscIIIPer.setText(CheckValidate.checkemptyTV(edtDiscIIIPer.getText().toString().trim()));
                                tvDiscIII.setText(CheckValidate.checkemptyTV(edtDiscIII.getText().toString().trim()));
                                tvOtherPer.setText(CheckValidate.checkemptyTV(edtOtherPer.getText().toString().trim()));
                                tvOther.setText(CheckValidate.checkemptyTV(edtOther.getText().toString().trim()));
                                tvOtherIIPer.setText(CheckValidate.checkemptyTV(edtOtherIIPer.getText().toString().trim()));
                                tvOtherII.setText(CheckValidate.checkemptyTV(edtOtherII.getText().toString().trim()));
                                tvCGSTACID.setText(CheckValidate.checkemptyTV(edtCGSTACID.getText().toString().trim()));
                                tvCGSTP.setText(CheckValidate.checkemptyTV(edtCGSTP.getText().toString().trim()));
                                tvCGST.setText(CheckValidate.checkemptyTV(edtCGST.getText().toString().trim()));
                                tvSGSTACID.setText(CheckValidate.checkemptyTV(edtSGSTACID.getText().toString().trim()));
                                tvSGSTP.setText(CheckValidate.checkemptyTV(edtSGSTP.getText().toString().trim()));
                                tvSGST.setText(CheckValidate.checkemptyTV(edtSGST.getText().toString().trim()));
                                tvIGSTACID.setText(CheckValidate.checkemptyTV(edtIGSTACID.getText().toString().trim()));
                                tvIGSTP.setText(CheckValidate.checkemptyTV(edtIGSTP.getText().toString().trim()));
                                tvIGST.setText(CheckValidate.checkemptyTV(edtIGST.getText().toString().trim()));
                                tvAmount.setText(CheckValidate.checkemptyTV(edtAmount.getText().toString().trim()));

                            }

                        }
                    });

                }
                catch (Exception e) {
                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Item detail filling error ");
                }

            }

            @Override
            public void onLongClick(View view, int position) {

                removeItemFromList(position);
//
//                return true;

            }
        }));



        if (NetworkUtils.isInternetAvailable(InVoiceDetailsActivity.this)) {
            getDivision();
        } else {
            ShowAlert.ShowAlertOkCancle(InVoiceDetailsActivity.this,"No Internet !","Are Sure want Exit ?");
        }

    }
    // END ON Create
    // method to remove list item
    protected void removeItemFromList(int position) {
        final int deletePosition = position;

        AlertDialog.Builder alert = new AlertDialog.Builder(
                InVoiceDetailsActivity.this,R.style.CustomDialogTheme);

        alert.setTitle("Delete");
        alert.setMessage("Do you want delete this item?");
        alert.setPositiveButton("YES", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TOD O Auto-generated method stub

                // main code on after clicking yes
                jsonArrayInvoiceDetails.remove(deletePosition);
                adapter.notifyDataSetChanged();
                calculateSum();
                recyclerView.setAdapter(adapter);


            }
        });
        alert.setNegativeButton("CANCEL", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        alert.show();

    }

    private void calculateSum() {

        try{

            sum = 0;

            for (int i = 0; i < jsonArrayInvoiceDetails.length(); i++) {
                try {
                    JSONObject jobj = jsonArrayInvoiceDetails.getJSONObject(i);
                    double amount = CheckValidate.checkemptyDouble(jobj.getString("NetAmount"));
                    sum = sum + amount;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = df.format(c);
            tvDate.setText(formattedDate);

            double roundoffvalue = Math.round(sum)- sum;
            tvPointValue.setText(String.format("%.2f",roundoffvalue));
            double tmpTotalAmt = Math.round(sum);
            tvTotalAmt.setText(String.format("%.2f",tmpTotalAmt));


        }catch(NumberFormatException e){

            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, e.toString());

        }

    }

    //start clear control
    public void ClearControl()
    {

        edtRate.setText("");
        edtDiscPer.setText("");
        edtDisc.setText("");
        edtDiscIIPer.setText("");
        edtDiscII.setText("");
        edtDiscIIIPer.setText("");
        edtDiscIII.setText("");
        edtOtherPer.setText("");
        edtOther.setText("");
        edtOtherIIPer.setText("");
        edtOtherII.setText("");
    }

    public void ClearGstControl()
    {
        edtCGSTACID.setText("");
        edtCGSTP.setText("");
        edtSGSTACID.setText("");
        edtSGSTP.setText("");
        edtIGSTACID.setText("");
        edtIGSTP.setText("");
    }
    // End clear control

    public void openDatePickerDialog(View view) {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);

        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(InVoiceDetailsActivity.this, new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                tvDate.setText(dateDesc);
            }
        }).textConfirm("CONFIRM") //text of confirm button
                .textCancel("CANCEL") //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .minYear(Calendar.getInstance().get(Calendar.YEAR)) //min year in loop
                .maxYear(2550) // max year in loop
                .dateChose(formattedDate) // date chose when init popwindow
                .build();
        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Only current date entry allow!");
        //pickerPopWin.showPopWin(this);
        pickerPopWin.dismissPopWin();
    }


    private void getDivision() {

        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(this);
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

                            for (int i = 0; i < jsonArrayDivision.length(); i++) {
                                DIVISIONLIST.add(jsonArrayDivision.getJSONObject(i).getString("DivisionName"));
                            }

                            Division = new SpinnerDialog(InVoiceDetailsActivity.this, DIVISIONLIST,
                                    "Select or Search Division");

                            Division.setCancellable(true);
                            Division.setShowKeyboard(false);

                            Division.bindOnSpinerListener(new OnSpinerItemClick() {
                                @Override
                                public void onClick(String item, int position) {
                                    SelectedItemDivision.setText(item);
                                   // ScrollView.smoothScrollTo(0,0);
                                    for(int i = 0;i < jsonArrayDivision.length();i++){
                                        try {
                                            if(jsonArrayDivision.getJSONObject(i).getString("DivisionName").equals(SelectedItemDivision.getText().toString())){
                                                DivisionID = jsonArrayDivision.getJSONObject(i).getString("Id");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    getClient(DivisionID,RoutID);
                                    SelectedItemClient.setText("Select Client");
                                }
                            });

                            findViewById(R.id.layoutDivision).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Division.showSpinerDialog();
                                }
                            });
                            getRoute();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void getRoute() {


        AQuery aq;
        aq = new AQuery(this);
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
                            Route = new SpinnerDialog(InVoiceDetailsActivity.this, ROUTELIST,
                                    "Select or Search Route");

                            Route.setCancellable(true);
                            Route.setShowKeyboard(false);

                            Route.bindOnSpinerListener(new OnSpinerItemClick() {
                                @Override
                                public void onClick(String item, int position) {
                                    SelectedItemRoute.setText(item);
                                    //ScrollView.smoothScrollTo(0,0);
                                    for(int i = 0;i < jsonArrayRoute.length();i++){
                                        try {
                                            if(jsonArrayRoute.getJSONObject(i).getString("RouteName").equals(SelectedItemRoute.getText().toString())){
                                                RoutID = jsonArrayRoute.getJSONObject(i).getString("Id");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    // DivisionID = SelectedItemDivision.getText().toString();
                                    if(DivisionID == "" || DivisionID =="0"){
                                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Please Select Division First ");
                                    }
                                    else {
                                        getClient(DivisionID,RoutID);
                                    }
                                    //getClient(DivisionID,RoutID);
                                    SelectedItemClient.setText("Select Client");

                                }
                            });

                            findViewById(R.id.layoutRoute).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Route.showSpinerDialog();
                                }
                            });
                            getSalemen();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void getSalemen() {


        AQuery aq;
        aq = new AQuery(this);
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
                            Salemen = new SpinnerDialog(InVoiceDetailsActivity.this, SALEMENLIST,
                                    "Select or Search Saleman");

                            Salemen.setCancellable(true);
                            Salemen.setShowKeyboard(false);

                            Salemen.bindOnSpinerListener(new OnSpinerItemClick() {
                                @Override
                                public void onClick(String item, int position) {
                                    SelectedItemSaleman.setText(item);
                                    //ScrollView.smoothScrollTo(0,0);

                                }
                            });

                            findViewById(R.id.layoutSaleman).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Salemen.showSpinerDialog();
                                }
                            });
                            getItem();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void getClient(String divId,String routeId) {


        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.CLIENT;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);
        params.put("DivisionId", divId);
        params.put("RouteId", routeId);

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {
                            if (ErrorMessage.equalsIgnoreCase("")) {
                                CLIENTCODELIST.clear();
                                jsonArrayClient = jRootObject.getJSONArray("AccountMasters");
                                for (int i = 0; i < jsonArrayClient.length(); i++) {
                                    CLIENTCODELIST.add(jsonArrayClient.getJSONObject(i).getString("AccountName"));
                                }

                                Client = new SpinnerDialog(InVoiceDetailsActivity.this, CLIENTCODELIST,
                                        "Select or Search Client");

                                Client.setCancellable(true);

                                Client.bindOnSpinerListener(new OnSpinerItemClick() {
                                    @Override
                                    public void onClick(String item, int position) {
                                        SelectedItemClient.setText(item);
                                        SelectedItemACGSTType = "";

                                        for (int i = 0; i < jsonArrayClient.length(); i++) {
                                            try {
                                                if (jsonArrayClient.getJSONObject(i).getString("AccountName").equals(SelectedItemClient.getText().toString().trim())) {

                                                    if(CheckValidate.checkemptystring(jsonArrayClient.getJSONObject(i).getString("AccountGSTType")) != "")
                                                    {
                                                        SelectedItemACGSTType = CheckValidate.checkemptystring(jsonArrayClient.getJSONObject(i).getString("AccountGSTType"));
                                                    }
                                                    else {
                                                        SelectedItemACGSTType = "NA";
                                                    }

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        //ScrollView.smoothScrollTo(0,0);

                                    }
                                });

                                findViewById(R.id.layoutClient).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Client.showSpinerDialog();
                                    }
                                });

                            }
                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void GetItemUnits(String itemId ) {

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.GETALTUNITS;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);
        params.put("intItemID", itemId);

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {
                            if (ErrorMessage.equalsIgnoreCase("")) {


                                jsonArrayAlternetUnit = jRootObject.getJSONArray("alternetUnit");

                            }

                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Deactivted Or Not alternet Unit " + ErrorMessage);
                        }
                        GetUnits();
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void GetUnits() {

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.GETUNIT;
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
                            if (ErrorMessage.equalsIgnoreCase("")) {


                                jsonArrayUnits = jRootObject.getJSONArray("Units");

                            }

                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Deactivted Or Not alternet Unit " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void getItem() {

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.ITEM;
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
                            if (ErrorMessage.equalsIgnoreCase("")) {
                                ITEMLIST.clear();
                                ITEMIDLIST.clear();
                                jsonArrayItem = jRootObject.getJSONArray("Items");
                                for (int i = 0; i < jsonArrayItem.length(); i++) {
                                    ITEMLIST.add(jsonArrayItem.getJSONObject(i).getString("ItemName") + " || " + "Stock = " + jsonArrayItem.getJSONObject(i).getString("ItemStock"));
                                    ITEMIDLIST.add(jsonArrayItem.getJSONObject(i).getString("Id") + " || " + "Stock = " + jsonArrayItem.getJSONObject(i).getString("ItemStock"));
                                }
                                getMRP();
                            }
                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void getInvoiceDetails() {
        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }

        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.GETINVOICEBYID;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);
        params.put("InvId", getIntent().getStringExtra("id"));

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {

                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {
                            if (ErrorMessage.equalsIgnoreCase("")) {
                                SelectedItemACGSTType = "";
                                jsonArrayInvoiceDetails = jRootObject.getJSONArray("InvoiceDetails");
                                adapter = new AdapterInvoiceDetails(InVoiceDetailsActivity.this, jsonArrayInvoiceDetails,jsonArrayMRP);
                                recyclerView.setAdapter(adapter);
                                JSONObject jobj = jRootObject.getJSONObject("InvoiceHeads");
                                tvPointValue.setText(jobj.getString("RoundOff"));
                                tvTotalAmt.setText(jobj.getString("Amount"));
                                JSONObject jsonObject = jRootObject.getJSONObject("InvoiceHeads");
                                SimpleDateFormat sd1 = new SimpleDateFormat("dd-MM-yyyy");
                                Date dt = sd1.parse(jsonObject.getString("InvDate"));
                                SimpleDateFormat sd2 = new SimpleDateFormat("dd-MM-yyyy");
                                String newDate = sd2.format(dt);
                                tvDate.setText(newDate);

                                SelectedItemDivision.setText(jsonObject.getString("DivsionName"));
                                SelectedItemClient.setText(jsonObject.getString("Client"));
                                SelectedItemRoute.setText(jsonObject.getString("RouteName"));
                                SelectedItemSaleman.setText(jsonObject.getString("SalesMan"));
                                if(CheckValidate.checkemptystring(jsonObject.getString("AccountGSTType")) != "")
                                {
                                    SelectedItemACGSTType = CheckValidate.checkemptystring(jsonObject.getString("AccountGSTType"));
                                }
                                else {
                                    SelectedItemACGSTType = "NA";
                                }

                                for(int i = 0;i < jsonArrayDivision.length();i++){
                                    try {
                                        if(jsonArrayDivision.getJSONObject(i).getString("DivisionName").equals(SelectedItemDivision.getText().toString())){
                                            DivisionID = jsonArrayDivision.getJSONObject(i).getString("Id");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                for(int i = 0;i < jsonArrayRoute.length();i++){
                                    try {
                                        if(jsonArrayRoute.getJSONObject(i).getString("RouteName").equals(SelectedItemRoute.getText().toString())){
                                            RoutID = jsonArrayRoute.getJSONObject(i).getString("Id");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                getClient(DivisionID,RoutID);
                            }


                            CustomProcessbar.hideProcessBar();

                        } else {

                            CustomProcessbar.hideProcessBar();
                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();
                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });
    }

    private void getMRP() {

        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.MRP;
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

                            jsonArrayMRP = jRootObject.getJSONArray("MRPs");

                            GetItemUnits("0");

                            CustomProcessbar.hideProcessBar();
                            getInvoiceDetails();
                        } else {
                            CustomProcessbar.hideProcessBar();
                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();
                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }
    private void addInVoice() {

        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.POSTINVOICE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);
        params.put("InvoiceHeads", String.valueOf(jsonObjectAddValues));
        params.put("InvoiceDetails", String.valueOf(jsonArrayInvoiceDetails));

       // ToastUtils.showErrorToast(InVoiceDetailsActivity.this,  jsonObjectAddValues.toString());
       // ToastUtils.showErrorToast(InVoiceDetailsActivity.this,  jsonArrayInvoiceDetails.toString());

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {
                            editorUserAuthKey.putString("SELECTVALUE","3");
                            editorUserAuthKey.apply();

                            Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                            startActivity(intent);
                            CustomProcessbar.hideProcessBar();
                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this,  jRootObject.getString("Status"));

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error " + ErrorMessage);
                            return;

                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error1 ");
                        return;
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error2 ");
                        return;
                    }
                } else {
                    CustomProcessbar.hideProcessBar();
                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error3 ");
                    return;
                }
                super.callback(url, jRootObject, status);
            }

        });

    }

    private void getMRPDetails(String itemId,String mrpId) {

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
        params.put("ItemId", itemId);
        params.put("MRPId", mrpId);
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jRootObject, AjaxStatus status) {

                if (jRootObject != null) {
                    Log.d("DEBUG", "status " + status.getError() + status.getMessage() + jRootObject.toString());
                    try {
                        String ErrorMessage = "";
                        ErrorMessage = jRootObject.getString("ErrorMessage");
                        if (ErrorMessage.equalsIgnoreCase("")) {

                            jsonObjMRPDetails = jRootObject.getJSONObject("MRPDetail");

                            if(edtRate.getText().toString().trim().equals(""))
                            {
                                edtRate.setText(jsonObjMRPDetails.getString("SalesRate"));
                            }
                            edtStock.setText(jsonObjMRPDetails.getString("Stock"));

                            if(SelectedItemACGSTType.trim().equals("B2BWS")  || SelectedItemACGSTType.trim().equals("B2CWS")|| SelectedItemACGSTType.trim().equals("NA")|| SelectedItemACGSTType.trim().isEmpty())
                            {
                                if( CheckValidate.checkemptyDouble(edtCGSTACID.getText().toString().trim()) <= 0)
                                {
                                    edtCGSTACID.setText(jsonObjMRPDetails.getString("CGSTAccountID"));
                                }
                                if( CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim()) <= 0)
                                {
                                    edtCGSTP.setText(jsonObjMRPDetails.getString("CGSTPerc"));
                                }
                                 if( CheckValidate.checkemptyDouble(edtSGSTACID.getText().toString().trim()) <= 0)
                                {
                                    edtSGSTACID.setText(jsonObjMRPDetails.getString("SGSTAccountID"));
                                }
                                if( CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim()) <= 0)
                                {
                                    edtSGSTP.setText(jsonObjMRPDetails.getString("SGSTPerc"));
                                }
                                edtIGSTACID.setText("");
                                edtIGSTP.setText("");

                            }
                            else
                            {
                                edtCGSTACID.setText("");
                                edtCGSTP.setText("");
                                edtSGSTACID.setText("");
                                edtSGSTP.setText("");
                                if( CheckValidate.checkemptyDouble(edtIGSTACID.getText().toString().trim()) <= 0)
                                {
                                    edtIGSTACID.setText(jsonObjMRPDetails.getString("IGSTAccountID"));
                                }
                                if( CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim()) <= 0)
                                {
                                    edtIGSTP.setText(jsonObjMRPDetails.getString("IGSTPerc"));
                                }
                            }

                            Rate = CheckValidate.checkemptyDouble(edtRate.getText().toString());
                            //edtPrimaryUnitQty.setText(String.format("%."+UDP+"f",PrimaryUnitQty));

                            if(PrimaryUnitQty > 0)
                            {
                                edtPrimaryUnitQty.setText(String.format("%."+UDP+"f",PrimaryUnitQty));
                            }
                            else{
                                edtPrimaryUnitQty.setText("");
                            }

                            double tmpTotalQty;
                            if(CheckValidate.checkemptyDouble(String.valueOf(AltUnitQty)) <= 0)
                            {
                                edtAltUnitQty.setText("");
                                tmpTotalQty =  CheckValidate.checkemptyDouble(edtPrimaryUnitQty.getText().toString().trim());
                            }else
                            {
                                edtAltUnitQty.setText(String.format("%."+UDA+"f",AltUnitQty));
                                tmpTotalQty = (AltUnitQty * AltUnitConversion) + PrimaryUnitQty;
                            }
                            String strTotalQty = String.format("%."+UDP+"f",tmpTotalQty);
                            edtTotalQty.setText(strTotalQty);
                            TotalQty = CheckValidate.checkemptyDouble(edtTotalQty.getText().toString().trim());

                            double   tmpGross =  TotalQty * Rate;
                            String gross =  String.format("%.2f",tmpGross);
                            edtGross.setText(gross);

                            Gross = CheckValidate.checkemptyDouble(edtGross.getText().toString().trim());
                            cgstP = CheckValidate.checkemptyDouble(edtCGSTP.getText().toString().trim());
                            sgstP = CheckValidate.checkemptyDouble(edtSGSTP.getText().toString().trim());
                            igstP = CheckValidate.checkemptyDouble(edtIGSTP.getText().toString().trim());

                            DiscPer = CheckValidate.checkemptyDouble(edtDiscPer.getText().toString().trim());
                            Disc = CheckValidate.checkemptyDouble(edtDisc.getText().toString().trim());

                            DiscIIPer = CheckValidate.checkemptyDouble(edtDiscIIPer.getText().toString().trim());
                            DiscII = CheckValidate.checkemptyDouble(edtDiscII.getText().toString().trim());

                            DiscIIIPer = CheckValidate.checkemptyDouble(edtDiscIIIPer.getText().toString().trim());
                            DiscIII = CheckValidate.checkemptyDouble(edtDiscIII.getText().toString().trim());

                            OtherPer = CheckValidate.checkemptyDouble(edtOtherPer.getText().toString().trim());
                            Other = CheckValidate.checkemptyDouble(edtOther.getText().toString().trim());

                            OtherIIPer = CheckValidate.checkemptyDouble(edtOtherIIPer.getText().toString().trim());
                            OtherII = CheckValidate.checkemptyDouble(edtOtherII.getText().toString().trim());

                            double cgst = 0;
                            if(cgstP > 0) {
                                double tmpCgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * cgstP) / 100);
                                String txtcgst = String.format("%.2f",tmpCgst);
                                edtCGST.setText(txtcgst);
                                cgst = CheckValidate.checkemptyDouble(edtCGST.getText().toString().trim());
                            }else{
                                edtCGSTP.setText("");
                                edtCGST.setText("");
                            }

                            double sgst = 0;
                            if(sgstP > 0) {
                                double tmpSgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * sgstP) / 100);
                                String txtsgst = String.format("%.2f",tmpSgst);
                                edtSGST.setText(txtsgst);
                                sgst = CheckValidate.checkemptyDouble(edtSGST.getText().toString().trim());
                            }else{
                                edtSGSTP.setText("");
                                edtSGST.setText("");
                            }

                            double igst = 0;
                            if(igstP > 0){
                                double tmpIgst = (((Gross - Disc - DiscII - DiscIII + Other + OtherII) * igstP) / 100);
                                String txtigst = String.format("%.2f",tmpIgst);
                                edtIGST.setText(txtigst);
                                igst = CheckValidate.checkemptyDouble(edtIGST.getText().toString().trim());
                            }else{
                                edtIGSTP.setText("");
                                edtIGST.setText("");
                            }

                            double totalAmount = (Gross - Disc - DiscII - DiscIII + Other + OtherII + cgst + sgst + igst);
                            String totalAmt = String.format("%.2f",totalAmount);
                            edtAmount.setText(totalAmt);
                            CustomProcessbar.hideProcessBar();

                        } else {
                            CustomProcessbar.hideProcessBar();

                            ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();

                    ToastUtils.showErrorToast(InVoiceDetailsActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });


    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
