package com.infozealrecon.android;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutusFragment extends Fragment {

    private Handler handler;


    TextView tvAboutUs, tvCompanyDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aboutus, container, false);

        handler = new Handler();
        tvAboutUs = view.findViewById(R.id.tvAboutUs);
        String AboutUs = "<ul>" +
                "<li><p>Since 2008, we are delivering full cycle software development services to customers in over 30 countries worldwide. </p></li>"+
                "<li><p>We have developed software in various business categories like Travel Industries, FMCG, Automobile Industries, Customer Relationship Management, Wholesale Agencies, Retail Outlet, POS Systems Payroll and Financial Accounting Systems. More than 1200 + customers are presently associated with us. <br/> For more Info. visit website: <a href=\\\"http://www.infozeal.co.in\\\">www.infozeal.co.in</a></p> </li>"+
                " </ul>";
        tvAboutUs.setText(Html.fromHtml(AboutUs));

        tvCompanyDetails = view.findViewById(R.id.tvCompanyDetails);

        String ContactUs =getString(R.string.contact_info);
        tvCompanyDetails.setText(Html.fromHtml(ContactUs));


        //End of code Return View
        return view;
    }


}
