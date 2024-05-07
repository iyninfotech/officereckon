package com.infozealrecon.android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.infozealrecon.android.InquiryDetailsActivity;
import com.infozealrecon.android.R;
import com.infozealrecon.android.Validations.CheckValidate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class InquiryAdapter extends RecyclerView.Adapter<InquiryAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    Context context;
    JSONArray jsonArray;

    // data is passed into the constructor
    public InquiryAdapter(Context context, JSONArray jsonArray) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.jsonArray = jsonArray;
//        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.inquiry_cell_design, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            holder.tvInquiryID.setText(CheckValidate.checkemptyTV(jsonObject.getString("Inq_No")));
            holder.tvInquiryName.setText(CheckValidate.checkemptyTV(jsonObject.getString("Inq_Name")));
            holder.tvInquiryNumber.setText(CheckValidate.checkemptyTV(jsonObject.getString("Inq_Contactperson")) + "\n" + CheckValidate.checkemptyTV(jsonObject.getString("Inq_Mobile")));
            holder.tvInquiryArea.setText(CheckValidate.checkemptyTV(jsonObject.getString("Inq_City")) + "\n"  + CheckValidate.checkemptyTV(jsonObject.getString("Inq_State")));

            if(jsonObject.getString("Inq_Status").contains("Pending")){
                holder.mainLayout.setBackgroundColor(Color.parseColor("#FFF8E649"));

            }else if(jsonObject.getString("Inq_Status").contains("Confirm")){
                holder.mainLayout.setBackgroundColor(Color.parseColor("#00BCD4"));

            }else{
                holder.mainLayout.setBackgroundColor(Color.parseColor("#FF4105"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, InquiryDetailsActivity.class);
                try {
                    i.putExtra("Inq_Code", jsonArray.getJSONObject(position).getString("Inq_Code"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                context.startActivity(i);

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
        TextView tvInquiryID, tvInquiryName, tvInquiryNumber, tvInquiryArea;
        LinearLayout mainLayout;
        ViewHolder(View itemView) {
            super(itemView);

            tvInquiryID = itemView.findViewById(R.id.tvInquiryID);
            tvInquiryName = itemView.findViewById(R.id.tvInquiryName);
            tvInquiryNumber = itemView.findViewById(R.id.tvMobileNumber);
            tvInquiryArea = itemView.findViewById(R.id.tvInquiryArea);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }


    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }


}