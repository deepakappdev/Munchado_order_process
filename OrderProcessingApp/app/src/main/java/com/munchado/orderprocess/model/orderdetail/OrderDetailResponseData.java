package com.munchado.orderprocess.model.orderdetail;

import java.util.ArrayList;

/**
 * Created by android on 22/2/17.
 */
public class OrderDetailResponseData {
    public String id;
    public String customer_first_name;
    public String customer_last_name;
    public String email;
    public String order_date;
    public String status;
    public String order_type1;
    public String order_type2;
    public String delivery_date;
    public String order_type;
    public String special_instruction;
    public String restaurant_id;
    public String tip_percent;
    public String redeem_point;
    public String pay_via_point;
    public String pay_via_card;
    public String total_amount;
    public String payment_receipt;
    public int is_reviewed;
    public int review_id;
    public int cod;
    public int pay_via_cash;
    public String restaurant_name;
    public String restaurant_address;
    public String rest_code;
    public String restaurant_image_name;
    public MyPaymentDetail my_payment_details;
    public MyDeliveryDetail my_delivery_detail;
    public OrderAmountCalculation order_amount_calculation;
    public ArrayList<MyItemList> item_list = new ArrayList<>();
    public UserActivity user_activity;
}
