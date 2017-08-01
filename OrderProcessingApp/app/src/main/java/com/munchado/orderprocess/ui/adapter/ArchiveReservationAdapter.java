package com.munchado.orderprocess.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.model.reservation.Archive_reservation;
import com.munchado.orderprocess.ui.fragment.OnArchiveReservationClickListener;
import com.munchado.orderprocess.ui.widgets.CustomTextView;
import com.munchado.orderprocess.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by test on 25/7/17.
 */

public class ArchiveReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final OnArchiveReservationClickListener clickListener;
    private ArrayList<Archive_reservation> orderItems = new ArrayList<>();
    private Context context;
    public ArchiveReservationAdapter(Context ctx, OnArchiveReservationClickListener clickListener)
    {
        this.clickListener = clickListener;
        context = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_active_reservation, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).populateItem(orderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return null!=orderItems? orderItems.size() : 0;
    }

    public ArrayList<Archive_reservation> getAllItems() {
        return orderItems;
    }
    public void setList(ArrayList<Archive_reservation> list){
        orderItems=list;
        notifyDataSetChanged();
    }
    public void appendList(ArrayList<Archive_reservation> list){
        orderItems.addAll(list);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_customer_name;
        private final TextView tv_customer_new_old;
        private final CustomTextView tv_date;
        private final TextView tv_people_count;
        private final Button btnAction;
        private final ContentLoadingProgressBar progressBar;
        private Archive_reservation orderItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onReservationClick(orderItem);
                }
            });
            tv_customer_name = (TextView) itemView.findViewById(R.id.tv_customer_name);
            tv_customer_new_old = (TextView) itemView.findViewById(R.id.tv_customer_new_old);
            tv_date = (CustomTextView) itemView.findViewById(R.id.tv_date);
            tv_people_count = (TextView) itemView.findViewById(R.id.tv_people_count);
            progressBar = (ContentLoadingProgressBar) itemView.findViewById(R.id.progress_bar);
            tv_date.setTextStyle(Typeface.BOLD);
            btnAction = (Button) itemView.findViewById(R.id.btn_action);
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onReservationActionClick(orderItem);
                }
            });
        }

        public void populateItem(Archive_reservation orderItem) {
            this.orderItem = orderItem;
            tv_customer_name.setText(orderItem.getFirst_name() + (!StringUtils.isNullOrEmpty(orderItem.getLast_name()) ? orderItem.getLast_name() : ""));
            tv_people_count.setText(orderItem.getParty_size() + " People");
            tv_customer_new_old.setText(orderItem.getNewcustomer());
            tv_date.setText(orderItem.getToday_time());
//
                progressBar.setVisibility(View.INVISIBLE);
                btnAction.setVisibility(View.VISIBLE);

                    btnAction.setText("View");
                    btnAction.setBackgroundResource(R.drawable.grey_button);

        }
    }
}
