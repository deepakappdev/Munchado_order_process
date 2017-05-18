package com.munchado.orderprocess.ui.adapter;

import android.content.Context;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.listener.OnDineinClickListener;
import com.munchado.orderprocess.model.dinein.UpcomingReservation;
import com.munchado.orderprocess.ui.widgets.CustomTextView;
import com.munchado.orderprocess.utils.Constants;
import com.munchado.orderprocess.utils.DateTimeUtils;
import com.munchado.orderprocess.utils.LogUtils;
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

    public void updateResult(UpcomingReservation selectedItem) {

        int position = getItemPosition(selectedItem.booking_id);
        LogUtils.d("====== position of this booking : " + position);
        if (position >= 0) {
            orderItems.set(position, selectedItem);
            notifyDataSetChanged();
        }
    }

    private int getItemPosition(String orderId) {
        int position = -1;
        boolean found = false;
        for (UpcomingReservation orderItem : this.orderItems) {
            position++;
            if (orderItem.booking_id.equalsIgnoreCase(orderId)) {
                found = true;
                break;
            }
        }
        if (found)
            return position;
        else
            return -1;
    }

    public void moveToArchive(UpcomingReservation item) {

        int pos = getPosition(item);
        if (pos > -1) {
            orderItems.remove(pos);
//            notifyItemRemoved(pos);
            notifyDataSetChanged();

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
        if (position < orderItems.size())
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
        private final ContentLoadingProgressBar progressBar;
        private final RelativeLayout mlayout;
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
            mlayout = (RelativeLayout) itemView.findViewById(R.id.ll_actionbtn);
            progressBar = (ContentLoadingProgressBar) itemView.findViewById(R.id.progress_bar);
            btnAction = (Button) itemView.findViewById(R.id.btn_action);
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (btnAction.getText().toString().equalsIgnoreCase("MOVE TO ARCHIVE")) {
                        mOnDineinClickListener.onMoveToArchive(mUpcomingReservation);
//
                    } else
                        mOnDineinClickListener.onDineItemClick(mUpcomingReservation);
                }
            });
        }

        public void updateData(UpcomingReservation mUpcomingReservation) {
            this.mUpcomingReservation = mUpcomingReservation;
            text_name.setText(mUpcomingReservation.first_name + (!StringUtils.isNullOrEmpty(mUpcomingReservation.last_name) ? " " + mUpcomingReservation.last_name : ""));
//            text_order_time.setText("00:00 AM");
            text_order_time.setText(DateTimeUtils.getFormattedDate(mUpcomingReservation.reservation_date, DateTimeUtils.FORMAT_HH_MM_A));
            if(!StringUtils.isNullOrEmpty(mUpcomingReservation.seats) && Integer.parseInt(mUpcomingReservation.seats)>=21)
            text_noofpeople.setText(mUpcomingReservation.seats + "+ People");
            else
                text_noofpeople.setText(mUpcomingReservation.seats + " People");


            if (mUpcomingReservation.inProgress) {
                progressBar.setVisibility(View.VISIBLE);
                btnAction.setVisibility(View.INVISIBLE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                btnAction.setVisibility(View.VISIBLE);
                //0=new,1=confirm,2=reject,3=alternate time,4=not respond,5=cancel,6=user confirm,7=archive
                switch (mUpcomingReservation.status) {
                    case Constants.NEW_ORDER://"0"
//                    text_hold_time.setText("(" + mUpcomingReservation.hold_time + " min)");
                        setTime(text_hold_time, mUpcomingReservation.hold_time);
                        text_msg.setVisibility(View.GONE);
                        mlayout.setVisibility(View.VISIBLE);
                        btnAction.setText("VIEW");
                        break;
                    case Constants.CONFIRM://"1"
//                    text_hold_time.setText("(" + mUpcomingReservation.hold_time + " min)");
                        text_msg.setVisibility(View.GONE);
                        setTime(text_hold_time, mUpcomingReservation.hold_time);
                        mlayout.setVisibility(View.VISIBLE);
                        btnAction.setText("VIEW");
                        break;
                    case Constants.REJECT://"2"
//                    text_hold_time.setText("(" + mUpcomingReservation.hold_time + " min)");
                        setTime(text_hold_time, mUpcomingReservation.hold_time);
                        text_msg.setVisibility(View.GONE);
                        mlayout.setVisibility(View.VISIBLE);
                        btnAction.setText("VIEW");
                        break;
                    case Constants.ALTERNATE_TIME://"3"
                        text_msg.setVisibility(View.VISIBLE);
                        text_msg.setText("Waiting for user to confirm");
                        text_hold_time.setText("(Your Offered Time)");
                        btnAction.setVisibility(View.GONE);
                        mlayout.setVisibility(View.GONE);
                        break;
                    case Constants.NOT_RESPOND://"4"
//                    text_hold_time.setText("(" + mUpcomingReservation.hold_time + " min)");
                        setTime(text_hold_time, mUpcomingReservation.hold_time);
                        text_msg.setVisibility(View.GONE);
                        mlayout.setVisibility(View.VISIBLE);
                        btnAction.setText("VIEW");
                        break;
                    case Constants.CANCEL://"5"
                        text_msg.setVisibility(View.VISIBLE);
                        text_msg.setText("Sorry, the user opted out");
                        text_hold_time.setText("(Your Offered Time)");
                        btnAction.setText("REMOVE");
                        mlayout.setVisibility(View.VISIBLE);
                        break;
                    case Constants.USER_CONFIRM://"6"
                        text_msg.setVisibility(View.VISIBLE);
                        text_msg.setText("Confirmed by the user");
                        text_hold_time.setText("(Your Offered Time)");
                        btnAction.setText("MOVE TO ARCHIVE");
                        mlayout.setVisibility(View.VISIBLE);
                        break;
                    case Constants.ARCHIVE://"7"
//                    text_hold_time.setText("(" + mUpcomingReservation.hold_time + " min)");
                        setTime(text_hold_time, mUpcomingReservation.hold_time);
                        text_msg.setVisibility(View.GONE);
                        mlayout.setVisibility(View.VISIBLE);
                        btnAction.setText("VIEW");
                        break;
                }
            }

            LogUtils.d("============= is progress : " + mUpcomingReservation.inProgress + "===== " + btnAction.getVisibility() + "===== " + mUpcomingReservation.status);
        }
    }

    private void setTime(CustomTextView textView, String time) {
        if (time.equalsIgnoreCase("0"))
            textView.setText("(Right Now)");
        else
            textView.setText("(" + time + " min)");
    }


}
