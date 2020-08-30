package com.score3s.android;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class HelpFragment extends Fragment {

    private Handler handler;


    TextView  tvCompanyDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        handler = new Handler();

        tvCompanyDetails = view.findViewById(R.id.tvCompanyDetails);

        String ContactUs =getString(R.string.contact_info);
        tvCompanyDetails.setText(Html.fromHtml(ContactUs));


        //End of code Return View
        return view;
    }


}
