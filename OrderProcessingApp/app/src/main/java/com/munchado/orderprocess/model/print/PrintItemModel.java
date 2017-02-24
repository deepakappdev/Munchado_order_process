package com.munchado.orderprocess.model.print;

import java.util.List;

/**
 * Created by munchado on 24/2/17.
 */
public class PrintItemModel {


    public int serial_no;
    public String item_name;
    public float item_price;
    public int quantity;

    public List<PrintSubItemModel> mPrintSubItemModelList;

}
