package com.munchado.orderprocess.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.model.archiveorder.ItemList;
import com.munchado.orderprocess.model.archiveorder.OrderItem;

import java.util.List;

/**
 * Created by android on 22/2/17.
 */
public class ArchiveOrderAdapter extends RecyclerView.Adapter<ArchiveOrderAdapter.MyViewHolder>{

    private List<OrderItem> orderItems;

    public void setResults(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_archive_order, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.populateItem(orderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView textOrderType;
        private final TextView textOrderItem;
        private final TextView textOrderAmount;
        private final TextView textDelayTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            textOrderType = (TextView)itemView.findViewById(R.id.text_order_type);
            textOrderItem = (TextView)itemView.findViewById(R.id.text_order_item);
            textOrderAmount = (TextView)itemView.findViewById(R.id.text_order_amount);
            textDelayTime = (TextView)itemView.findViewById(R.id.text_delay_time);

        }

        public void populateItem(OrderItem orderItem) {
            textOrderType.setText(orderItem.order_type);
            StringBuilder stringBuilder = new StringBuilder();
            for(ItemList itemList:orderItem.item_list) {
                stringBuilder.append(itemList.item_qty + " " + itemList.item_name + ", ");
            }
            textOrderItem.setText(stringBuilder.toString());
            textOrderAmount.setText("$ " + orderItem.total_amount);
            textDelayTime.setText(orderItem.delivery_date);
        }
    }
}
