package com.infozealrecon.android;


import static com.infozealrecon.android.AddInvoiceActivity.DEFHEADDATA;
import static com.infozealrecon.android.MainActivity.AUTHKEY;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.infozealrecon.android.Constant.APIURL;
import com.infozealrecon.android.NavigationCustom.data.MenuItem;
import com.infozealrecon.android.NavigationCustom.widget.SNavigationDrawer;
import com.infozealrecon.android.asynctasks.CustomProcessbar;
import com.infozealrecon.android.asynctasks.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationDrawerActivity extends AppCompatActivity {

    SNavigationDrawer sNavigationDrawer;
    int color1 = 0;
    Class fragmentClass;
    public Fragment fragment;
    SharedPreferences preferencesUserAuthKey;
    SharedPreferences.Editor editorUserAuthKey;
    String AuthKey, SelectedValue, CompanyName, UserName;

    SharedPreferences preferencesDEFHEADDATA;
    SharedPreferences.Editor editorDEFHEADDATA;

/*
    @Override
    public void onResume() {
        super.onResume();

        // start playback here (if not playing already)
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();

        // start playback here (if not playing already)
    }*/

    @Override
    public void onBackPressed() {
        if (sNavigationDrawer.isDrawerOpen()) {
            sNavigationDrawer.closeDrawer();
        } else {

            // super.onBackPressed();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Confirmation..");
            builder.setMessage("Are you sure want to Exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    NavigationDrawerActivity.super.onBackPressed();
                    //overridePendingTransition(android.R.animator.fade_out, android.R.animator.fade_in);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        preferencesDEFHEADDATA = getSharedPreferences(DEFHEADDATA, MODE_PRIVATE);
        editorDEFHEADDATA = preferencesDEFHEADDATA.edit();
        preferencesUserAuthKey = getSharedPreferences(AUTHKEY, MODE_PRIVATE);
        editorUserAuthKey = preferencesUserAuthKey.edit();
        AuthKey = preferencesUserAuthKey.getString("auth", "");
        SelectedValue = preferencesUserAuthKey.getString("SELECTVALUE", "0");
        CompanyName = "INFOZEAL";
        UserName = preferencesUserAuthKey.getString("UserName", "");


        sNavigationDrawer = findViewById(R.id.navigationDrawer);
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Dashboard", R.drawable.button_background));
        menuItems.add(new MenuItem("Account Master", R.drawable.button_background));
        menuItems.add(new MenuItem("Inquiry", R.drawable.button_background));
        menuItems.add(new MenuItem("Customer Lock", R.drawable.button_background));
        /*menuItems.add(new MenuItem("About us", R.drawable.button_background));
        menuItems.add(new MenuItem("Logout", R.drawable.button_background));*/
        sNavigationDrawer.setMenuItemList(menuItems);

        if (SelectedValue.equals("4")) {

            fragmentClass = InvoiceGridFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                sNavigationDrawer.appbarTitleTV.setText("About Us");
                sNavigationDrawer.btnAdd.setVisibility(View.VISIBLE);
            }

        } else {

            fragmentClass = DashBoardFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                sNavigationDrawer.appbarTitleTV.setText(CompanyName);
            }
        }

        sNavigationDrawer.setOnMenuItemClickListener(new SNavigationDrawer.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClicked(int position) {
                System.out.println("Position " + position);

                switch (position) {
                    case 0: {
                        sNavigationDrawer.appbarTitleTV.setText(CompanyName);
                        fragmentClass = DashBoardFragment.class;
                        break;
                    }
                    case 1: {
                        fragmentClass = OutletFragment.class;
                        break;
                    }
                    case 2: {
                        fragmentClass = InquiryFragment.class;
                        break;
                    }
                    case 3: {

                        Intent i = new Intent(NavigationDrawerActivity.this, RegisterActivity.class);
                        startActivity(i);
                        finish();
                    }

                    case 4: {
                        fragmentClass = AboutusFragment.class;
                        break;
                    }
                    case 5: {
                        fragmentClass = AboutusFragment.class;
                        break;
                    }

                    case 6: {
                        fragmentClass = DashBoardFragment.class;
                        AlertDialog.Builder builder = new AlertDialog.Builder(NavigationDrawerActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Confirmation..");
                        builder.setMessage("Are you sure want to Logout?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", then he is allowed to exit from application

                                performLogout();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user select "No", just cancel this dialog and continue with app
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
                sNavigationDrawer.setDrawerListener(new SNavigationDrawer.DrawerListener() {

                    @Override
                    public void onDrawerOpened() {

                    }

                    @Override
                    public void onDrawerOpening() {

                    }

                    @Override
                    public void onDrawerClosing() {
                        System.out.println("Drawer closed");
                    }

                    @Override
                    public void onDrawerClosed() {

                        if (fragmentClass != null) {

                            if (fragmentClass == DashBoardFragment.class) {
                                sNavigationDrawer.appbarTitleTV.setText(CompanyName);
                            }

                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (fragment != null) {
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                            }
                        }
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        System.out.println("State " + newState);
                    }
                });
            }
        });


    }

    private void performLogout() {

        try {
            CustomProcessbar.showProcessBar(this, false, getString(R.string.please_wait));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AQuery aq;
        aq = new AQuery(this);
        String url = APIURL.BASE_URL + APIURL.LOGOUT;
        Map<String, String> params = new HashMap<String, String>();
        params.put("AuthKey", AuthKey);
        params.put("UserName", UserName);
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

                            if (jRootObject.getString("Status").equals("Success")) {

                                editorUserAuthKey.putString("auth", "");
                                editorUserAuthKey.putString("CompanyName", "");
                                editorUserAuthKey.putString("UserName", "");
                                editorUserAuthKey.putString("SELECTVALUE", "0");
                                editorUserAuthKey.apply();

                                editorDEFHEADDATA.putString("divison", "");
                                editorDEFHEADDATA.putString("sroute", "");
                                editorDEFHEADDATA.putString("ssalesman", "");
                                editorDEFHEADDATA.apply();


                                Intent i = new Intent(NavigationDrawerActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                ToastUtils.showErrorToast(NavigationDrawerActivity.this, "Error " + ErrorMessage);
                            }

                        } else {
                            ToastUtils.showErrorToast(NavigationDrawerActivity.this, "Error " + ErrorMessage);
                        }
                    } catch (JSONException e) {
                        Log.d("DEBUG", "Json Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(NavigationDrawerActivity.this, "Error ");
                    } catch (Exception e) {
                        Log.d("DEBUG", "Exception" + e.getMessage());
                        e.printStackTrace();
                        ToastUtils.showErrorToast(NavigationDrawerActivity.this, "Error ");
                    }
                } else {
                    ToastUtils.showErrorToast(NavigationDrawerActivity.this, "Error ");
                }
                super.callback(url, jRootObject, status);
            }

        });

    }


}
