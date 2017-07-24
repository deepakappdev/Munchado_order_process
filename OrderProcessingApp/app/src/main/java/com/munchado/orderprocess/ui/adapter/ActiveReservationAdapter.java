package com.munchado.orderprocess.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.listener.OnReservationClickListener;
import com.munchado.orderprocess.model.reservation.UpcomingReservationModelResponse;
import com.munchado.orderprocess.ui.widgets.CustomTextView;
import com.munchado.orderprocess.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by munchado on 21/7/17.
 */

public class ActiveReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final OnReservationClickListener clickListener;
    private List<UpcomingReservationModelResponse.Incoming_reservations> orderItems = new ArrayList<>();
    private Context context;

    public static final int ORDER_TYPE_TODAY = 0;
    public static final int ORDER_TYPE_PAST_FUTURE = 1;

    public ActiveReservationAdapter(Context ctx, OnReservationClickListener clickListener) {
        this.clickListener = clickListener;
        context = ctx;
    }

    public void updateResult(List<UpcomingReservationModelResponse.Incoming_reservations> orderItems) {
        if (this.orderItems.size() == 0) {
            this.orderItems.addAll(orderItems);
//            notifyDataSetChanged();
        } else
            for (UpcomingReservationModelResponse.Incoming_reservations orderItem : orderItems) {
//                LogUtils.d("========= updateResult status : " + orderItem.status);
                int position = getItemPosition(orderItem.id);
                if (position == -1) {
                    this.orderItems.add(0, orderItem);
                    notifyItemInserted(0);
                    notifyItemRangeChanged(0, getItemCount());
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(context, notification);
                    r.play();
                }
            }
        notifyDataSetChanged();
    }

    public void updateResult(UpcomingReservationModelResponse.Incoming_reservations selectedItem) {
        int position = getItemPosition(selectedItem.id);
        if (position >= 0) {

            orderItems.remove(position);
            if (selectedItem.status.equalsIgnoreCase("cancelled")) {
                notifyItemChanged(position);
                notifyItemRangeChanged(position, getItemCount());
            } else if (!selectedItem.status.equalsIgnoreCase("archived")) {
                orderItems.add(position, selectedItem);
                notifyItemChanged(position);
                notifyItemRangeChanged(position, getItemCount());
            } else notifyDataSetChanged();
        }
    }

    public void updateStatusResult(UpcomingReservationModelResponse.Incoming_reservations selectedItem) {
        int position = getItemPosition(selectedItem.id);
        if (position >= 0) {
            orderItems.set(position, selectedItem);
            notifyItemChanged(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_active_reservation, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder)
            ((MyViewHolder) holder).populateItem(orderItems.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public void removeOrder(String orderId) {
        int position = getItemPosition(orderId);
        if (position > -1) {
            orderItems.remove(position);
//            notifyItemRemoved(position);
//            notifyItemRangeChanged(position, getItemCount());
            notifyDataSetChanged();
        }
    }

    private int getItemPosition(String orderId) {
        int position = -1;
        boolean found = false;
        for (UpcomingReservationModelResponse.Incoming_reservations orderItem : this.orderItems) {
            position++;
            if (orderItem.id.equalsIgnoreCase(orderId)) {
                found = true;
                break;
            }
        }
        if (found)
            return position;
        else
            return -1;
    }

    public void confirmOrder(String orderId, String status) {
//        LogUtils.d("======confirmOrder=== status : " + status);
        int position = getItemPosition(orderId);
        if (position > -1) {
            if (status.equalsIgnoreCase("arrived"))
                orderItems.get(position).status = "arrived";
            else
                orderItems.get(position).status = "confirmed";
            orderItems.get(position).inProgress = false;
            notifyItemChanged(position);
            notifyItemRangeChanged(position, getItemCount());
            notifyDataSetChanged();
        }
    }

    public List<UpcomingReservationModelResponse.Incoming_reservations> getAllItems() {
        return orderItems;
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (DateTimeUtils.isToday(orderItems.get(position).delivery_date)) {
//            return ORDER_TYPE_TODAY;
//        } else
//            return ORDER_TYPE_PAST_FUTURE;
//    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_customer_name;
        private final TextView tv_customer_new_old;
        private final CustomTextView tv_date;
        private final TextView tv_people_count;
        private final Button btnAction;
        private final ContentLoadingProgressBar progressBar;
        private UpcomingReservationModelResponse.Incoming_reservations orderItem;

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

        public void populateItem(UpcomingReservationModelResponse.Incoming_reservations orderItem) {
            this.orderItem = orderItem;
            tv_customer_name.setText(orderItem.first_name + (!StringUtils.isNullOrEmpty(orderItem.last_name) ? orderItem.last_name : ""));
            tv_people_count.setText(orderItem.party_size + " People");
            tv_customer_new_old.setText(orderItem.newcustomer);
            tv_date.setText(orderItem.today_time);
//            tv_date.setText(DateTimeUtils.getFormattedDate(orderItem.today_time, DateTimeUtils.FORMAT_MMM_DD_YYYY));
            if (orderItem.inProgress) {
                progressBar.setVisibility(View.VISIBLE);
                btnAction.setVisibility(View.INVISIBLE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                btnAction.setVisibility(View.VISIBLE);
                if (orderItem.statusClass.equalsIgnoreCase("rejected") || orderItem.statusClass.equalsIgnoreCase("archived") ) {
                        btnAction.setText("View");
                        btnAction.setBackgroundResource(R.drawable.grey_button);
                }
//                else if (orderItem.status.equalsIgnoreCase("placed")) {
//                    btnAction.setText("CONFIRM");
//                    btnAction.setBackgroundResource(R.drawable.green_button);
//                } else if (orderItem.status.equalsIgnoreCase("arrived") || orderItem.status.equalsIgnoreCase("delivered")) {
//                    btnAction.setText("ARCHIVE");
//                    btnAction.setBackgroundResource(R.drawable.grey_button);
//                } else {
//                    btnAction.setText("ARCHIVE");
//                    btnAction.setBackgroundResource(R.drawable.grey_button);
//                }
            }
        }
    }

}
