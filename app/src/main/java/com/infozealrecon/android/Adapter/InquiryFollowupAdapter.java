package com.infozealrecon.android.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.infozealrecon.android.R;
import com.infozealrecon.android.Validations.CheckValidate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class InquiryFollowupAdapter extends RecyclerView.Adapter<InquiryFollowupAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    Context context;
    JSONArray jsonArray;

    // data is passed into the constructor
    public InquiryFollowupAdapter(Context context, JSONArray jsonArray) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.jsonArray = jsonArray;
//        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.inquiryfollowp_cell_design, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);

            holder.tvInquiryID.setText("Sr No :: " + CheckValidate.checkemptyTV(jsonObject.getString("Inq_Srno")) + "\n"
                    + "Date :: " + CheckValidate.checkemptyTV(jsonObject.getString("Inq_Date")) + "\n"
                    + "Action :: " + CheckValidate.checkemptyTV(jsonObject.getString("Inq_Action")) + "\n"
                    + "Respond::" + CheckValidate.checkemptyTV(jsonObject.getString("Inq_Respond")) + "\n"
                    + "Next ::" + CheckValidate.checkemptyTV(jsonObject.getString("Inq_Next")) + "\n"
                    + "Time ::" +CheckValidate.checkemptyTV(jsonObject.getString("Inq_Time")) + "\n"
                    + "Follow-up By ::" + CheckValidate.checkemptyTV(jsonObject.getString("Inq_By"))
            );



        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

//        String animal = mData.get(position);
//        holder.myTextView.setText(animal);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInquiryID;
        LinearLayout mainLayout;
        ViewHolder(View itemView) {
            super(itemView);

            tvInquiryID = itemView.findViewById(R.id.tvInquiryID);
            mainLayout = itemView.findViewById(R.id.mainLayoutfollowp);
        }


    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }


}