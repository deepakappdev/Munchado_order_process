package com.munchado.orderprocess.listener;

import com.munchado.orderprocess.model.archiveorder.OrderItem;

/**
 * Created by android on 23/2/17.
 */

public interface OnOrderClickListener {
    void onClickOrderItem(OrderItem orderItem);
    void onClickOrderAction(OrderItem orderItem);
}
