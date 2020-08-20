package com.score3s.android.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.score3s.android.InVoiceDetailsActivity;
import com.score3s.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class InVoiceAdapter extends RecyclerView.Adapter<InVoiceAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    Context context;
    JSONArray jsonArray;

    public InVoiceAdapter(Context context, JSONArray jsonArray) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.invoice_cell_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
//            JSONObject jsonObject = jsonArray;
            holder.tvInvoiceID.setText(jsonObject.getString("InvId"));
            holder.tvOrderNo.setText(jsonObject.getString("InvNo"));
            holder.tvClientName.setText(jsonObject.getString("ClientName"));
            holder.tvAmount.setText(jsonObject.getString("Amount"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.mnuView:

                                Intent iview = new Intent(context, InVoiceDetailsActivity.class);
                                try {
                                    iview.putExtra("id", jsonArray.getJSONObject(position).getString("InvId"));
                                    iview.putExtra("btnType", "ViewOnly");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(iview);

                                break;

                            /*case R.id.mnuPrint:

                                Intent iprint = new Intent(context, PrintBt.class);
                                try {
                                    iprint.putExtra("id",jsonArray.getJSONObject(position).getString("InvId"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(iprint);

                                break;*/

                            case R.id.mnuEdit:

                                Intent i = new Intent(context, InVoiceDetailsActivity.class);
                                try {
                                    i.putExtra("id", jsonArray.getJSONObject(position).getString("InvId"));
                                    i.putExtra("btnType", "Edit");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                context.startActivity(i);

                                break;

                            case R.id.mnuDelete:
                                Toast.makeText(view.getContext(), "Invoice Delete not allow from Mobile Application!", Toast.LENGTH_LONG).show();
                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });

       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, InVoiceDetailsActivity.class);
                try {
                    i.putExtra("id",jsonArray.getJSONObject(position).getString("InvId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                context.startActivity(i);

            }
        });*/

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setCancelable(false);
                builder.setTitle("Alert..");
                builder.setMessage("Are you sure want to Edit Invoice?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application

                        Intent onLongPress = new Intent(context, InVoiceDetailsActivity.class);
                        try {
                            onLongPress.putExtra("id", jsonArray.getJSONObject(position).getString("InvId"));
                            onLongPress.putExtra("btnType", "Edit");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        context.startActivity(onLongPress);

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

                return true;


            }
        });
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvInvoiceID, tvOrderNo, tvClientName, tvAmount;
        public TextView buttonViewOption;

        ViewHolder(View itemView) {
            super(itemView);

            tvInvoiceID = itemView.findViewById(R.id.tvInvoiceID);
            tvOrderNo = itemView.findViewById(R.id.tvOrderNumber);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvAmount = itemView.findViewById(R.id.tvAmount);

            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);

        }

    }

    String getItem(int id) {
        return "0";
    }


}