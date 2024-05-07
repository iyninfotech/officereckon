package com.infozealrecon.android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.infozealrecon.android.R;
import com.infozealrecon.android.Validations.CheckValidate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InVoiceUpdateDetailsAdapter extends RecyclerView.Adapter<InVoiceUpdateDetailsAdapter.ViewHolder> {

    LayoutInflater mInflater;
    Context context;
    JSONArray jsonArray;

    public InVoiceUpdateDetailsAdapter(Context context, JSONArray jsonArray) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_invoice_p_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.myTextView.setText(String.valueOf(position + 1));

        try {
            JSONObject jobj = jsonArray.getJSONObject(position);
            holder.tvItem.setText(jobj.getString("ItemName"));
            holder.tvMRP.setText(jobj.getString("ItemMRP"));
            holder.tvAlternetQtyUnit.setText(jobj.getString("AlternetUnit"));
            holder.tvAlternetQty.setText(jobj.getString("AlternetUnitQty"));
            holder.tvPrimaryQtyUnit.setText(jobj.getString("PrimaryUnit"));
            holder.tvPrimaryQty.setText(jobj.getString("PrimaryUnitQty"));
            holder.tvTotalQty.setText(jobj.getString("TotalQty"));
            holder.tvFreeQty.setText(CheckValidate.checkemptyTV(jobj.getString("FreeQty")));
            holder.tvRate.setText(CheckValidate.checkempty(jobj.getString("Rate")));
            holder.tvGross.setText(CheckValidate.checkemptyTV(jobj.getString("Gross")));
            holder.tvDiscPer.setText(CheckValidate.checkemptyTV(jobj.getString("DiscountPer")));
            holder.tvDisc.setText(CheckValidate.checkemptyTV(jobj.getString("Discount")));
            holder.tvDiscIIPer.setText(CheckValidate.checkemptyTV(jobj.getString("DiscountIIPer")));
            holder.tvDiscII.setText(CheckValidate.checkemptyTV(jobj.getString("DiscountII")));
            holder.tvDiscIIIPer.setText(CheckValidate.checkemptyTV(jobj.getString("DiscountIIIPer")));
            holder.tvDiscIII.setText(CheckValidate.checkemptyTV(jobj.getString("DiscountIII")));
            holder.tvOtherPer.setText(CheckValidate.checkemptyTV(jobj.getString("OtherPer")));
            holder.tvOther.setText(CheckValidate.checkemptyTV(jobj.getString("Other")));
            holder.tvOtherIIPer.setText(CheckValidate.checkemptyTV(jobj.getString("OtherIIPer")));
            holder.tvOtherII.setText(CheckValidate.checkemptyTV(jobj.getString("OtherII")));
            holder.tvCGSTACID.setText(CheckValidate.checkemptyTV(jobj.getString("CGSTAccountID")));
            holder.tvCGSTP.setText(CheckValidate.checkemptyTV(jobj.getString("CGSTPer")));
            holder.tvCGST.setText(CheckValidate.checkemptyTV(jobj.getString("CGSTAmt")));
            holder.tvSGSTACID.setText(CheckValidate.checkemptyTV(jobj.getString("SGSTAccountID")));
            holder.tvSGSTP.setText(CheckValidate.checkemptyTV(jobj.getString("SGSTPer")));
            holder.tvSGST.setText(CheckValidate.checkemptyTV(jobj.getString("SGSTAmt")));
            holder.tvIGSTACID.setText(CheckValidate.checkemptyTV(jobj.getString("IGSTAccountID")));
            holder.tvIGSTP.setText(CheckValidate.checkemptyTV(jobj.getString("IGSTPer")));
            holder.tvIGST.setText(CheckValidate.checkemptyTV(jobj.getString("IGSTAmt")));
            holder.tvAmount.setText(jobj.getString("NetAmount"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    String getItem(int id) {
        return "0";
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView myTextView, tvItem, tvMRP, tvAlternetQtyUnit, tvAlternetQty, tvPrimaryQtyUnit, tvPrimaryQty, tvTotalQty, tvFreeQty, tvRate, tvGross, tvCGSTACID, tvCGSTP, tvCGST, tvSGSTACID, tvSGSTP, tvSGST, tvIGSTACID, tvIGSTP, tvIGST, tvAmount;
        TextView tvDiscPer, tvDisc, tvDiscIIPer, tvDiscII, tvDiscIIIPer, tvDiscIII, tvOtherPer, tvOther, tvOtherIIPer, tvOtherII;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.title);
            tvItem = itemView.findViewById(R.id.tvItem);
            tvMRP = itemView.findViewById(R.id.tvMRP);
            tvAlternetQtyUnit = itemView.findViewById(R.id.tvAlternetQtyUnit);
            tvAlternetQty = itemView.findViewById(R.id.tvAlternetQty);
            tvPrimaryQtyUnit = itemView.findViewById(R.id.tvPrimaryQtyUnit);
            tvPrimaryQty = itemView.findViewById(R.id.tvPrimaryQty);
            tvTotalQty = itemView.findViewById(R.id.tvTotalQty);
            tvFreeQty = itemView.findViewById(R.id.tvFreeQty);
            tvRate = itemView.findViewById(R.id.tvRate);
            tvGross = itemView.findViewById(R.id.tvGross);
            tvDiscPer = itemView.findViewById(R.id.tvDiscPer);
            tvDisc = itemView.findViewById(R.id.tvDisc);
            tvDiscIIPer = itemView.findViewById(R.id.tvDiscIIPer);
            tvDiscII = itemView.findViewById(R.id.tvDiscII);
            tvDiscIIIPer = itemView.findViewById(R.id.tvDiscIIIPer);
            tvDiscIII = itemView.findViewById(R.id.tvDiscIII);
            tvOtherPer = itemView.findViewById(R.id.tvOtherPer);
            tvOther = itemView.findViewById(R.id.tvOther);
            tvOtherIIPer = itemView.findViewById(R.id.tvOtherIIPer);
            tvOtherII = itemView.findViewById(R.id.tvOtherII);
            tvCGSTACID = itemView.findViewById(R.id.tvCGSTACID);
            tvCGSTP = itemView.findViewById(R.id.tvCGSTP);
            tvCGST = itemView.findViewById(R.id.tvCGST);
            tvSGSTACID = itemView.findViewById(R.id.tvSGSTACID);
            tvSGSTP = itemView.findViewById(R.id.tvSGSTP);
            tvSGST = itemView.findViewById(R.id.tvSGST);
            tvIGSTACID = itemView.findViewById(R.id.tvIGSTACID);
            tvIGSTP = itemView.findViewById(R.id.tvIGSTP);
            tvIGST = itemView.findViewById(R.id.tvIGST);
            tvAmount = itemView.findViewById(R.id.tvAmount);

        }

    }

}