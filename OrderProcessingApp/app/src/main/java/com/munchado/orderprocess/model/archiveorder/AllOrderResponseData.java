package com.munchado.orderprocess.model.archiveorder;

import com.munchado.orderprocess.model.update.Upgrade;

import java.util.ArrayList;

/**
 * Created by munchado on 24/6/17.
 */

public class AllOrderResponseData {
    public ArrayList<AllOrderItem> orders;
    public int total_live_records;
    public Upgrade fource_update;
}
