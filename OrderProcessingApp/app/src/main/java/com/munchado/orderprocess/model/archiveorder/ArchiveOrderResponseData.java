package com.munchado.orderprocess.model.archiveorder;

import com.munchado.orderprocess.model.update.Upgrade;

import java.util.ArrayList;

/**
 * Created by android on 22/2/17.
 */

public class ArchiveOrderResponseData {
    public ArrayList<OrderItem> archive_order;
    public int total_archive_records;

    public Upgrade fource_update;
}
