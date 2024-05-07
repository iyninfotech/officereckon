package com.infozealrecon.android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.infozealrecon.android.MRPDetailsActivity;
import com.infozealrecon.android.R;
import com.infozealrecon.android.Validations.CheckValidate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MRPAdapter extends RecyclerView.Adapter<MRPAdapter.ViewHolder> {

    Context context;
    private LayoutInflater mInflater;
    JSONArray jsonArray;

    public MRPAdapter(Context context, JSONArray jsonArray) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.mrp_cell_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            holder.tvItemId.setText(CheckValidate.checkemptyTV(jsonObject.getString("ItemId")));
            holder.tvMRPId.setText(CheckValidate.checkemptyTV(jsonObject.getString("MRPId")));
            holder.tvMRPItemName.setText(CheckValidate.checkemptyTV(jsonObject.getString("ItemName")));
            holder.tvMRP.setText(CheckValidate.checkemptyTV(jsonObject.getString("ItemMRP")));
            holder.tvMRPStock.setText(CheckValidate.checkemptyTV(jsonObject.getString("Stock")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MRPDetailsActivity.class);
                try {
                    i.putExtra("id", jsonArray.getJSONObject(position).getString("ItemId"));
                    i.putExtra("MRPId", jsonArray.getJSONObject(position).getString("MRPId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                context.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMRPId, tvItemId, tvMRPItemName, tvMRP, tvMRPStock;

        ViewHolder(View itemView) {
            super(itemView);

            tvMRPId = itemView.findViewById(R.id.tvMRPId);
            tvItemId = itemView.findViewById(R.id.tvItemId);
            tvMRPItemName = itemView.findViewById(R.id.tvMRPItemName);
            tvMRP = itemView.findViewById(R.id.tvMRP);
            tvMRPStock = itemView.findViewById(R.id.tvMRPStock);
        }
    }

    String getItem(int id) {
        return "0";
    }
}
