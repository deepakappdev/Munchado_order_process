package com.munchado.orderprocess.model.archiveorder;

import java.util.ArrayList;

/**
 * Created by munchado on 24/6/17.
 */

public class AllOrderItem {
    public String id;
    public String fname;
    public String lname;
    public String email;
    public String phone;
    public String restaurant_id;
    public String restaurant_name;
    public String is_restaurant_exists;
    public String order_type1;
    public String order_type;
    public String delivery_date;
    public String order_date;
    public int menu_status;
    public int is_reviewed;
    public int review_id;
    public String status;
    public String total_amount;
    public ArrayList<ItemList> item_list = new ArrayList<>();
    public boolean inProgress;
}
