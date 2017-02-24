package com.munchado.orderprocess.model.print;

/**
 * Created by munchado on 24/2/17.
 */
public class PrintHeaderModel {

    public String store_name;
    public String contact_no;
    public String store_director;
    public final String seperator ="------------------------------\n";

    public float subtotal;
    public float tax;
    public float total;
}
