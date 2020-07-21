package com.score3s.android.Adapter;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.score3s.android.OutletDetailsActivity;
import com.score3s.android.R;
import com.score3s.android.Validations.CheckValidate;

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
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            holder.tvOutletID.setText(CheckValidate.checkemptyTV(jsonObject.getString("OutletId")));
            holder.tvOutletName.setText(CheckValidate.checkemptyTV(jsonObject.getString("OutletName")));
            holder.tvOutletArea.setText(CheckValidate.checkemptyTV(jsonObject.getString("Area")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,OutletDetailsActivity.class);
                try {
                    i.putExtra("id",jsonArray.getJSONObject(position).getString("OutletId"));
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
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView tvOutletID,tvOutletName,tvOutletArea;

        ViewHolder(View itemView) {
            super(itemView);

            tvOutletID = itemView.findViewById(R.id.tvOutletID);
            tvOutletName = itemView.findViewById(R.id.tvOutletName);
            tvOutletArea = itemView.findViewById(R.id.tvOutletArea);

        }



    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }


}