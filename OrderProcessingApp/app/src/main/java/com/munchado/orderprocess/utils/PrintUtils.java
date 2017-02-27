package com.munchado.orderprocess.utils;

import com.munchado.orderprocess.model.orderdetail.MyItemList;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponseData;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by munchado on 24/2/17.
 */
public class PrintUtils {

    public static final int CONTENT_LENGTH = 40;
    private static final int NO_LENGTH = 4;
    public static String seperator="------------------------------------------\n";
    static DecimalFormat df = new DecimalFormat();

    public static String setPrintData(OrderDetailResponseData orderDetailResponseData) {
//        char[] chars = new char[CONTENT_LENGTH];
//        Arrays.fill(chars, '-');
//        seperator= new String(chars);
//        seperator+="\n";
        StringBuilder builder = new StringBuilder();
        builder.append(getCenterAlignedData(orderDetailResponseData.restaurant_name));
        builder.append(getCenterAlignedData(orderDetailResponseData.restaurant_address));
        builder.append(seperator);
//        LogUtils.d(builder.toString());
        int i = 1;
        for (MyItemList printItemModel : orderDetailResponseData.item_list) {
            builder.append(getItemPriceData(i, printItemModel.item_name, Integer.valueOf(printItemModel.item_qty) * Float.valueOf(printItemModel.unit_price) + ""));
            i++;
        }

        builder.append(seperator);
        builder.append(getAmountCalculation("Subtotal", "$" + orderDetailResponseData.order_amount_calculation.subtotal));
        builder.append(getAmountCalculation("Discount", "$" + orderDetailResponseData.order_amount_calculation.discount));
        builder.append(getAmountCalculation("Delivery", "$" + orderDetailResponseData.order_amount_calculation.delivery_charge));
        builder.append(getAmountCalculation("Tax", "$" + orderDetailResponseData.order_amount_calculation.tax_amount));
        builder.append(getAmountCalculation("Tip", "$" + orderDetailResponseData.order_amount_calculation.tip_amount));
//        builder.append(getAmountCalculation("Total", "$" + orderDetailResponseData.order_amount_calculation.total_order_price));
//        builder.append(seperator).append("\n\n");
//        builder.append(getCenterAlignedData("See you soon!!"));
        LogUtils.d(builder.toString());
        return builder.toString();
    }

    public static String getAmountCalculation(String string, String amount) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!amount.contains("."))
            amount = amount + ".00";
        int remainingspace = CONTENT_LENGTH - string.length() - amount.length();
        if (remainingspace > 0) {
            char[] chars = new char[remainingspace];
            Arrays.fill(chars, ' ');
            stringBuilder.append(string).append(chars).append(amount).append("\n");
        } else
            stringBuilder.append(string).append(amount).append("\n");
        return stringBuilder.toString();
    }

    public static String getItemPriceData(int serialNo, String name, String price) {
        name = name.replaceAll("&amp;", "&");
        StringBuilder stringBuilder = new StringBuilder();
        df.setMaximumFractionDigits(2);
        String p = df.format(Float.valueOf(price));
        if (!p.contains("."))
            p = p + ".00";
        if (serialNo > 9)
            stringBuilder.append(serialNo + ". ");
        else
            stringBuilder.append("0" + serialNo + ". ");
        int spaceforitemname = CONTENT_LENGTH - (" $" + p).length() - NO_LENGTH;

        if (name.length() > spaceforitemname) {
            int notimes = name.length() / spaceforitemname;
            int rem = name.length() % spaceforitemname;
            for (int i = 0; i < notimes; i++) {
                if (i == 0)
                    stringBuilder.append(name.substring(0, spaceforitemname)).append(" $" + p).append("\n");
                else
                    stringBuilder.append("    ").append(name.substring(i * (spaceforitemname), (i + 1) * spaceforitemname)).append("\n");
            }
            int remainingspace = spaceforitemname - rem;
            if (remainingspace > 1) {
                char[] chars = new char[remainingspace];
                Arrays.fill(chars, ' ');
                stringBuilder.append("    ").append(name.substring(spaceforitemname * notimes)).append("\n");
            } else
                stringBuilder.append("    ").append(name).append("\n");
        } else {
            int remainingspace = spaceforitemname - name.length();
            if (remainingspace > 1) {
                char[] chars = new char[remainingspace];
                Arrays.fill(chars, ' ');
                stringBuilder.append(name).append(chars).append(" $" + p).append("\n");
            } else
                stringBuilder.append(name).append("\n");
        }


        return stringBuilder.toString();
    }

    public static String getCenterAlignedData(String str) {
        str = str.replaceAll("&amp;", "&");
        StringBuilder stringBuilder = new StringBuilder();
        if (str.length() > CONTENT_LENGTH) {
            int notimes = str.length() / CONTENT_LENGTH;
            int rem = str.length() % CONTENT_LENGTH;
            for (int i = 0; i < notimes; i++) {
                if (i == 0)
                    stringBuilder.append(str.substring(0, CONTENT_LENGTH)).append("\n");
                else
                    stringBuilder.append(str.substring(i * (CONTENT_LENGTH), (i + 1) * CONTENT_LENGTH)).append("\n");
            }
            int remainingspace = CONTENT_LENGTH - rem;
            if (remainingspace > 1) {
                char[] chars = new char[remainingspace / 2];
                Arrays.fill(chars, ' ');
                stringBuilder.append(chars).append(str.substring(CONTENT_LENGTH * notimes)).append(chars).append("\n");
            } else
                stringBuilder.append(str).append("\n");
        } else {
            int remainingspace = CONTENT_LENGTH - str.length();
            if (remainingspace > 1) {
                char[] chars = new char[remainingspace / 2];
                Arrays.fill(chars, ' ');
                stringBuilder.append(chars).append(str).append(chars).append("\n");
            } else
                stringBuilder.append(str).append("\n");
        }
        return stringBuilder.toString();
    }
}
