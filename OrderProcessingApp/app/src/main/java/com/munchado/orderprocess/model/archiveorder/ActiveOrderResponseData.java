package com.munchado.orderprocess.model.archiveorder;

import com.munchado.orderprocess.model.update.Upgrade;

import java.util.ArrayList;

/**
 * Created by android on 22/2/17.
 */

public class ActiveOrderResponseData {
    public ArrayList<OrderItem> live_order;
    public int total_live_records;
    public Upgrade fource_update;
}
