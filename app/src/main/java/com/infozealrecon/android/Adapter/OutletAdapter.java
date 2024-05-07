package com.infozealrecon.android.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.infozealrecon.android.OutletDetailsActivity;
import com.infozealrecon.android.R;
import com.infozealrecon.android.Validations.CheckValidate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    Context context;
    JSONArray jsonArray;


    // data is passed into the constructor
    public OutletAdapter(Context context, JSONArray jsonArray) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.jsonArray = jsonArray;
//        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.outlet_cell_design, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            holder.tvOutletID.setText(CheckValidate.checkemptyTV(jsonObject.getString("Party_Key")));
            holder.tvOutletName.setText(CheckValidate.checkemptyTV(jsonObject.getString("Party_Name")));
            holder.tvOutletNumber.setText(CheckValidate.checkemptyTV(jsonObject.getString("Party_Cp")) + "\n" + CheckValidate.checkemptyTV(jsonObject.getString("Party_Mobno")));
            holder.tvOutletArea.setText(CheckValidate.checkemptyTV(jsonObject.getString("Party_Area")) +"\n"  +CheckValidate.checkemptyTV(jsonObject.getString("Party_City")) + "\n"  + CheckValidate.checkemptyTV(jsonObject.getString("Party_Stat")));

            if(jsonObject.getString("Head_Code").contains("A031")){
                holder.mainLayout.setBackgroundColor(Color.parseColor("#FFF8E649"));

            }else{

                if(jsonObject.getString("Party_Status").toUpperCase().contains("NOT USING SOFTWARE")){
                    holder.mainLayout.setBackgroundColor(Color.parseColor("#FFFF5722"));

                }else{
                    holder.mainLayout.setBackgroundColor(Color.parseColor("#FF00BCD4"));
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, OutletDetailsActivity.class);
                try {
                    i.putExtra("Party_Key", jsonArray.getJSONObject(position).getString("Party_Key"));
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
        TextView tvOutletID, tvOutletName, tvOutletNumber, tvOutletArea;
        LinearLayout mainLayout;
        ViewHolder(View itemView) {
            super(itemView);

            tvOutletID = itemView.findViewById(R.id.tvOutletID);
            tvOutletName = itemView.findViewById(R.id.tvOutletName);
            tvOutletNumber = itemView.findViewById(R.id.tvMobileNumber);
            tvOutletArea = itemView.findViewById(R.id.tvOutletArea);
            mainLayout = itemView.findViewById(R.id.mainLayout);

        }


    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }


}