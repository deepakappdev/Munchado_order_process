package com.munchado.orderprocess.ui.adapter;

import android.content.Context;
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
import com.munchado.orderprocess.listener.OnOrderClickListener;
import com.munchado.orderprocess.model.archiveorder.ItemList;
import com.munchado.orderprocess.model.archiveorder.OrderItem;
import com.munchado.orderprocess.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 22/2/17.
 */
public class ActiveOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final OnOrderClickListener clickListener;
    private List<OrderItem> orderItems = new ArrayList<>();
    private Context context;

    public static final int ORDER_TYPE_TODAY = 0;
    public static final int ORDER_TYPE_PAST_FUTURE = 1;

    public ActiveOrderAdapter(Context ctx, OnOrderClickListener clickListener) {
        this.clickListener = clickListener;
        context = ctx;
    }

    public void updateResult(List<OrderItem> orderItems) {
        if (this.orderItems.size() == 0) {
            this.orderItems.addAll(orderItems);
        } else
            for (OrderItem orderItem : orderItems) {
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
    }

    public void updateResult(OrderItem selectedItem) {
        int position = getItemPosition(selectedItem.id);
        if (position >= 0) {
            orderItems.remove(position);
            orderItems.add(position, selectedItem);
            notifyItemChanged(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ORDER_TYPE_TODAY)
            return new MyViewTodayOrderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_active_order_with_time, parent, false));
        else
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_active_order, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder)
            ((MyViewHolder) holder).populateItem(orderItems.get(position));
        else
            ((MyViewTodayOrderHolder) holder).populateItem(orderItems.get(position));
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
        for (OrderItem orderItem : this.orderItems) {
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

    public void confirmOrder(String orderId) {
        int position = getItemPosition(orderId);
        if (position > -1) {
            orderItems.get(position).status = "confirmed";
            orderItems.get(position).inProgress = false;
            notifyItemChanged(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }

    public List<OrderItem> getAllItems() {
        return orderItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (DateTimeUtils.isToday(orderItems.get(position).delivery_date)) {
            return ORDER_TYPE_TODAY;
        } else
            return ORDER_TYPE_PAST_FUTURE;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView textOrderType;
        private final TextView textOrderItem;
        private final TextView textOrderAmount;
        private final TextView textDelayTime;
        private final Button btnAction;
        private final ContentLoadingProgressBar progressBar;
        private OrderItem orderItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClickOrderItem(orderItem);
                }
            });
            textOrderType = (TextView) itemView.findViewById(R.id.text_order_type);
            textOrderItem = (TextView) itemView.findViewById(R.id.text_order_item);
            textOrderAmount = (TextView) itemView.findViewById(R.id.text_order_amount);
            textDelayTime = (TextView) itemView.findViewById(R.id.text_delay_time);
            progressBar = (ContentLoadingProgressBar) itemView.findViewById(R.id.progress_bar);
            btnAction = (Button) itemView.findViewById(R.id.btn_action);
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClickOrderAction(orderItem);
                }
            });
        }

        public void populateItem(OrderItem orderItem) {
            this.orderItem = orderItem;
            textOrderType.setText(orderItem.order_type);
            StringBuilder stringBuilder = new StringBuilder();
            for (ItemList itemList : orderItem.item_list) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append(", ");
                stringBuilder.append(itemList.item_qty).append("  ").append(itemList.item_name);
            }
            textOrderItem.setText(stringBuilder.toString());
            textOrderAmount.setText("$" + orderItem.total_amount);
//            if(orderItem.delivery_date.contains("00:00"))
//                orderItem.delivery_date.replaceAll("00:00","01:02");
            textDelayTime.setText(DateTimeUtils.getFormattedDate(orderItem.delivery_date, DateTimeUtils.FORMAT_MMM_DD_YYYY));
            if (orderItem.inProgress) {
                progressBar.setVisibility(View.VISIBLE);
                btnAction.setVisibility(View.INVISIBLE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                btnAction.setVisibility(View.VISIBLE);
                if (orderItem.status.equalsIgnoreCase("confirmed")) {
                    if (orderItem.order_type.equalsIgnoreCase("takeout")) {
//                        btnAction.setText("Picked Up");
                        btnAction.setText("Archive");
                    } else if (orderItem.order_type.equalsIgnoreCase("delivery")) {
//                        btnAction.setText("Sent");
                        btnAction.setText("Archive");
                    }
                    btnAction.setBackgroundResource(R.drawable.grey_button);

                } else if (orderItem.status.equalsIgnoreCase("placed")) {
                    btnAction.setText("Confirm");
//                    btnAction.setText("Archive");
                    btnAction.setBackgroundResource(R.drawable.green_button);
                }
            }
        }
    }


    class MyViewTodayOrderHolder extends RecyclerView.ViewHolder {

        private final TextView textOrderType;
        private final TextView textOrderItem;
        private final TextView textOrderAmount;
        private final TextView textDelayTime;
        private final TextView textDelayTimeHourMinute;
        private final Button btnAction;
        private final ContentLoadingProgressBar progressBar;
        private OrderItem orderItem;

        public MyViewTodayOrderHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClickOrderItem(orderItem);
                }
            });
            textOrderType = (TextView) itemView.findViewById(R.id.text_order_type);
            textOrderItem = (TextView) itemView.findViewById(R.id.text_order_item);
            textOrderAmount = (TextView) itemView.findViewById(R.id.text_order_amount);
            textDelayTime = (TextView) itemView.findViewById(R.id.text_delay_time);
            textDelayTimeHourMinute = (TextView) itemView.findViewById(R.id.text_delay_time_1);
            progressBar = (ContentLoadingProgressBar) itemView.findViewById(R.id.progress_bar);
            btnAction = (Button) itemView.findViewById(R.id.btn_action);
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClickOrderAction(orderItem);
                }
            });
        }

        public void populateItem(OrderItem orderItem) {
            this.orderItem = orderItem;
            textOrderType.setText(orderItem.order_type);
            StringBuilder stringBuilder = new StringBuilder();
            for (ItemList itemList : orderItem.item_list) {
                if (stringBuilder.length() > 0)
                    stringBuilder.append(", ");
                stringBuilder.append(itemList.item_qty).append("  ").append(itemList.item_name);
            }
            textOrderItem.setText(stringBuilder.toString());
            textOrderAmount.setText("$" + orderItem.total_amount);
            textDelayTime.setText(DateTimeUtils.getFormattedDate(orderItem.delivery_date, DateTimeUtils.FORMAT_MMM_DD_YYYY));
            textDelayTimeHourMinute.setText(DateTimeUtils.getFormattedDate(orderItem.delivery_date, DateTimeUtils.FORMAT_HH_MM_A));
            if (orderItem.inProgress) {
                progressBar.setVisibility(View.VISIBLE);
                btnAction.setVisibility(View.INVISIBLE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                btnAction.setVisibility(View.VISIBLE);
                if (orderItem.status.equalsIgnoreCase("confirmed")) {
                    if (orderItem.order_type.equalsIgnoreCase("takeout")) {
//                        btnAction.setText("Picked Up");
                        btnAction.setText("Archive");
                    } else if (orderItem.order_type.equalsIgnoreCase("delivery")) {
//                        btnAction.setText("Sent");
                        btnAction.setText("Archive");
                    }
                    btnAction.setBackgroundResource(R.drawable.grey_button);

                } else if (orderItem.status.equalsIgnoreCase("placed")) {
                    btnAction.setText("Confirm");
//                    btnAction.setText("Archive");
                    btnAction.setBackgroundResource(R.drawable.green_button);
                }
            }
        }
    }
}
