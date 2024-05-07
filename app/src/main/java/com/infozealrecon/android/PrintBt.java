package com.infozealrecon.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.infozealrecon.android.Adapter.AdapterInvoiceDetails;
import com.infozealrecon.android.Constant.APIURL;
import com.infozealrecon.android.SearchSpinnerCustom.SpinnerDialog;
import com.infozealrecon.android.asynctasks.CustomProcessbar;
import com.infozealrecon.android.asynctasks.ToastUtils;
import com.infozealrecon.android.asynctasks.UnicodeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.infozealrecon.android.AddInvoiceActivity.DEFHEADDATA;
import static com.infozealrecon.android.MainActivity.AUTHKEY;

public class PrintBt extends Activity implements Runnable {
    ///start
    ImageView btnBack, btnCancel;
    TextView tvTotalAmt, tvPointValue;
    TextView tvDate;
    AdapterInvoiceDetails adapter;

    double AltUnitQty, PrimaryUnitQty, TotalQty, Gross, cgstP = 0, sgstP = 0, igstP = 0, Rate, StockQty, CurrentQty;
    double DiscPer = 0, Disc = 0, DiscIIPer = 0, DiscII = 0, DiscIIIPer = 0, DiscIII = 0, TotalDisc = 0, OtherPer = 0, Other = 0, OtherIIPer = 0, OtherII = 0, AltUnitConversion = 0;
    int UnitDecimalPlaces = 0, UDP = 0, UDA = 0;
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

    Button btnUpdate, btnAdd, btnOk;
    SpinnerDialog Division, Route, Salemen, Client;
    JSONArray jsonArrayDivision, jsonArrayRoute, jsonArraySalesmen, jsonArrayClient, jsonArrayItem, jsonArrayMRP, jsonArrayInvoiceDetails, jsonArrayUnits, jsonArrayAlternetUnit;

    JSONObject jobjPerticularPosition;
    JSONObject jsonObjectAddValues;

    double sum = 0;
    SharedPreferences preferencesDEFHEADDATA;
    SharedPreferences.Editor editorDEFHEADDATA;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey;
    TextView SelectedItemDivision, SelectedItemRoute, SelectedItemSaleman, SelectedItemClient;
    int edtInvHeadId;
    ScrollView ScrollView;
    RecyclerView recyclerView;

    String CompanyName, Salesman, InvoiceDate, InvoiceNum, roundoff, totalAmount;
    /////end

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.print_bt);

        preferencesDEFHEADDATA = getSharedPreferences(DEFHEADDATA, MODE_PRIVATE);
        editorDEFHEADDATA = preferencesDEFHEADDATA.edit();
        preferencesUserAuthKey = getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();

        AuthKey = preferencesUserAuthKey.getString("auth", "");
        CompanyName = preferencesUserAuthKey.getString("CompanyName", "");
        getInvoiceDetails();

        mScan = (Button) findViewById(R.id.Scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(PrintBt.this, "Message1", Toast.LENGTH_LONG).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(PrintBt.this,
                                DeviceListActivity.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });

        mPrint = (Button) findViewById(R.id.mPrint);
        mPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                Thread t = new Thread() {
                    public void run() {
                        try {
                            OutputStream os = mBluetoothSocket
                                    .getOutputStream();
                            String BILL = "";
                            BILL = CompanyName;
                            BILL = BILL + "\n";
                            BILL = BILL + "----------Tax Invoice---------";
                            BILL = BILL + "\n";
                            BILL = BILL + "Invoice No:   " + InvoiceNum + "\n";
                            BILL = BILL + "Invoice Date:" + InvoiceDate + "\n";
                            BILL = BILL + "------------------------------";
                            BILL = BILL + "\n";
                            BILL = BILL + "Item" + "          " + "Rate\n";
                            BILL = BILL + "------------------------------";
                            BILL = BILL + "\n";
                            BILL = BILL + "Total Qty:" + "     " + "2.0\n";

                            BILL = BILL + "Round Off:" + "  " + roundoff + "\n";
                            BILL = BILL + "Total Value:" + "  " + totalAmount + "\n";
                            BILL = BILL + "Salesman" + "  " + Salesman + "\n";
                            BILL = BILL + "------------------------------\n\n";
                            os.write(BILL.getBytes());

                            os.close();
                            //This is printer specific code you can comment ==== > Start

                            // Setting height
                            /*int gs = 29;
                            os.write(intToByteArray(gs));
                            int h = 104;
                            os.write(intToByteArray(h));
                            int n = 162;
                            os.write(intToByteArray(n));*/

                            // Setting Width
                            /*int gs_width = 29;
                            os.write(intToByteArray(gs_width));
                            int w = 119;
                            os.write(intToByteArray(w));
                            int n_width = 2;
                            os.write(intToByteArray(n_width));*/

                            // Print BarCode
                            /*int gs1 = 29;
                            os.write(intToByteArray(gs1));
                            int k = 107;
                            os.write(intToByteArray(k));
                            int m = 73;
                            os.write(intToByteArray(m));

                            String barCodeVal = "ASDFC028060000005";// "HELLO12345678912345012";
                            System.out.println("Barcode Length : "
                                    + barCodeVal.length());
                            int n1 = barCodeVal.length();
                            os.write(intToByteArray(n1));

                            for (int i = 0; i < barCodeVal.length(); i++) {
                                os.write((barCodeVal.charAt(i) + "").getBytes());
                            }*/
                            //printer specific code you can comment ==== > End
                        } catch (Exception e) {
                            Log.e("PrintBt", "Exe ", e);
                        }
                    }
                };
                t.start();
            }
        });

        mDisc = (Button) findViewById(R.id.dis);
        mDisc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.disable();
            }
        });


    }
    // END ON Create

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

                                jsonArrayInvoiceDetails = jRootObject.getJSONArray("InvoiceDetails");
                                JSONObject jobj = jRootObject.getJSONObject("InvoiceHeads");
                                roundoff = jobj.getString("RoundOff").toString();
                                totalAmount = jobj.getString("Amount").toString();

                                JSONObject jsonObject = jRootObject.getJSONObject("InvoiceHeads");
                                SimpleDateFormat sd1 = new SimpleDateFormat("dd-MM-yyyy");
                                Date dt = sd1.parse(jsonObject.getString("InvDate"));
                                SimpleDateFormat sd2 = new SimpleDateFormat("dd-MM-yyyy");
                                InvoiceNum = jsonObject.getString("InvId").toString();
                                InvoiceDate = sd2.format(dt);

                                Salesman = jsonObject.getString("SalesMan").toString();

                            }


                            CustomProcessbar.hideProcessBar();

                        } else {

                            CustomProcessbar.hideProcessBar();
                            ToastUtils.showErrorToast(PrintBt.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        CustomProcessbar.hideProcessBar();

                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(PrintBt.this, "Error ");
                    } catch (Exception e) {
                        CustomProcessbar.hideProcessBar();
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(PrintBt.this, "Error ");
                    }
                } else {
                    CustomProcessbar.hideProcessBar();
                    ToastUtils.showErrorToast(PrintBt.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();

        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
            super.onBackPressed();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(PrintBt.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(PrintBt.this, "Message", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            //closeSocket(mBluetoothSocket);
            mBluetoothAdapter.cancelDiscovery();
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(PrintBt.this, "DeviceConnected", Toast.LENGTH_LONG).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

}
