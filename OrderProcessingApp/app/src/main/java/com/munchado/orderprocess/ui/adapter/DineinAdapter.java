package com.munchado.orderprocess.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.listener.OnDineinClickListener;
import com.munchado.orderprocess.model.dinein.UpcomingReservation;
import com.munchado.orderprocess.ui.widgets.CustomTextView;
import com.munchado.orderprocess.utils.DateTimeUtils;
import com.munchado.orderprocess.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by munchado on 28/4/17.
 */
public class DineinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UpcomingReservation> orderItems = new ArrayList<>();
    private Context context;
    private OnDineinClickListener mOnDineinClickListener;

    public DineinAdapter(Context ctx, OnDineinClickListener mOnDineinClickListener) {
        this.mOnDineinClickListener = mOnDineinClickListener;
        context = ctx;
    }

    public void setData(List<UpcomingReservation> orderItems) {
        this.orderItems = orderItems;
        notifyDataSetChanged();
    }

    public void moveToArchive(UpcomingReservation item) {

        int pos = getPosition(item);
        if (pos > -1) {
            orderItems.remove(pos);
            notifyItemRemoved(pos);

        }
    }

    private int getPosition(UpcomingReservation item) {
        for (int i = 0; i < orderItems.size(); i++) {
            if (item.booking_id.equalsIgnoreCase(orderItems.get(i).booking_id)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dinein, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).updateData(orderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public List<UpcomingReservation> getAllItems() {
        return orderItems;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView text_name;
        private final TextView text_order_time;
        private final CustomTextView text_hold_time;
        private final TextView text_noofpeople;
        private final TextView text_msg;
        private final Button btnAction;
        private UpcomingReservation mUpcomingReservation;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnDineinClickListener.onDineItemClick(mUpcomingReservation);
                }
            });
            text_name = (TextView) itemView.findViewById(R.id.text_name);
            text_order_time = (TextView) itemView.findViewById(R.id.text_order_time);
            text_hold_time = (CustomTextView) itemView.findViewById(R.id.text_hold_time);
            text_noofpeople = (TextView) itemView.findViewById(R.id.text_noofpeople);
            text_msg = (TextView) itemView.findViewById(R.id.text_msg);
            btnAction = (Button) itemView.findViewById(R.id.btn_action);
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnDineinClickListener.onDineItemClick(mUpcomingReservation);
                }
            });
        }

        public void updateData(UpcomingReservation mUpcomingReservation) {
            this.mUpcomingReservation = mUpcomingReservation;
            text_name.setText(mUpcomingReservation.first_name + (!StringUtils.isNullOrEmpty(mUpcomingReservation.last_name) ? " " + mUpcomingReservation.last_name : ""));
//            text_order_time.setText("00:00 AM");
            text_order_time.setText(DateTimeUtils.getFormattedDate(mUpcomingReservation.reservation_date, DateTimeUtils.FORMAT_HH_MM_A));
            text_noofpeople.setText(mUpcomingReservation.seats + " People");
            text_hold_time.setText("(" + mUpcomingReservation.hold_time + " min)");

            //0=new,1=confirm,2=reject,3=alternate time,4=not respond,5=cancel,6=user confirm,7=archive
            switch (mUpcomingReservation.status) {
                case "0":
                    break;
                case "1":
                    break;
                case "2":
                    break;
                case "3":
                    text_msg.setVisibility(View.VISIBLE);
                    text_msg.setText("Waiting for user to confirm");
                    text_hold_time.setText("(Your Offered Time)");
                    btnAction.setVisibility(View.GONE);
                    break;
                case "4":
                    break;
                case "5":
                    text_msg.setVisibility(View.VISIBLE);
                    text_msg.setText("Sorry, the user opted out");
                    text_hold_time.setText("(Your Offered Time)");
                    btnAction.setText("REMOVE");
                    break;
                case "6":
                    text_msg.setVisibility(View.VISIBLE);
                    text_msg.setText("Confirmed by the user");
                    text_hold_time.setText("(Your Offered Time)");
                    btnAction.setText("MOVE TO ARCHIVE");
                    break;
                case "8":
                    break;
            }
        }
    }
}
