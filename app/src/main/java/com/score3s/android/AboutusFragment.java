package com.score3s.android;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutusFragment extends Fragment {

    private Handler handler;


    TextView tvVersionDetails, tvCompanyDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aboutus, container, false);

        handler = new Handler();

        tvVersionDetails = view.findViewById(R.id.tvVersionDetails);
        tvCompanyDetails = view.findViewById(R.id.tvCompanyDetails);
        tvCompanyDetails.setText(String.format("Phone No. = +91 (79) 268-80901 \nEmail Id = support@infozeal.co.in \nWebsite = www.infozeal.co.in"));
        getVersionInfo();

        //End of code Return View
        return view;
    }

    //get the current version number and name
    private void getVersionInfo() {
        String versionName = "";
        int versionCode = -1;
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersionDetails.setText(String.format("Version code = %d  \nVersion name = %s", versionCode, versionName));
    }

}
