package com.munchado.orderprocess.utils;

import com.munchado.orderprocess.model.orderdetail.MyItemList;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponseData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by munchado on 24/2/17.
 */
public class PrintUtils {

    private static final int CONTENT_LENGTH = 30;
    private static final int NO_LENGTH = 4;
    private static final int ITEM_NAME_PRICE_LENGTH = 26;
    DecimalFormat df = new DecimalFormat();
    public void setPrintData(OrderDetailResponseData orderDetailResponseData) {
        final String seperator ="------------------------------\n";
//        PrintModel mPrintModel = new PrintModel();
//        mPrintModel.mPrintItemList.clear();
//        {
//            PrintHeaderModel mPrintHeaderModel = new PrintHeaderModel();
//            mPrintHeaderModel.store_name = orderItem.restaurant_name;
//            mPrintHeaderModel.total = Float.valueOf(orderItem.total_amount);
//            int i = 1;
//            for (ItemList mItemModel : orderItem.item_list) {
//                PrintItemModel printItemModel = new PrintItemModel();
//                printItemModel.serial_no = i;
//                printItemModel.item_name = mItemModel.item_name;
//                printItemModel.quantity = Integer.parseInt(mItemModel.item_qty);
//
//                mPrintModel.mPrintItemList.add(printItemModel);
//                i++;
//            }
//            mPrintModel.mPrintHeaderModel = mPrintHeaderModel;
//        }

        StringBuilder builder=new StringBuilder();
        builder.append(getCenterAlignedData(orderDetailResponseData.restaurant_name));
        builder.append(getCenterAlignedData(orderDetailResponseData.restaurant_address));
        builder.append(seperator);
//        LogUtils.d(getCenterAlignedData(orderDetailResponseData.restaurant_name));
//        LogUtils.d(getCenterAlignedData(orderDetailResponseData.restaurant_address));
//        LogUtils.d(seperator);

        int i = 1;
        for (MyItemList printItemModel : orderDetailResponseData.item_list) {
//           LogUtils.d(getItemPriceData(1,printItemModel.item_name,Integer.valueOf(printItemModel.item_qty)*Float.valueOf(printItemModel.unit_price)+""));
            builder.append(getItemPriceData(1,printItemModel.item_name,Integer.valueOf(printItemModel.item_qty)*Float.valueOf(printItemModel.unit_price)+""));
            i++;
        }
//        LogUtils.d(seperator);
        builder.append(seperator);
        builder.append(getAmountCalculation("Subtotal","$"+orderDetailResponseData.order_amount_calculation.subtotal));
        builder.append(getAmountCalculation("Discount","$"+orderDetailResponseData.order_amount_calculation.discount));
        builder.append(getAmountCalculation("Delivery","$"+orderDetailResponseData.order_amount_calculation.delivery_charge));
        builder.append(getAmountCalculation("Tax","$"+orderDetailResponseData.order_amount_calculation.tax_amount));
        builder.append(getAmountCalculation("Tip","$"+orderDetailResponseData.order_amount_calculation.tip_amount));
        builder.append(getAmountCalculation("Total","$"+orderDetailResponseData.order_amount_calculation.total_order_price));
        builder.append(seperator);
        builder.append(getCenterAlignedData("See you soon!!"));
        LogUtils.d(builder.toString());
    }

    public String getAmountCalculation(String string, String amount){
        StringBuilder stringBuilder = new StringBuilder();
        int remainingspace=CONTENT_LENGTH - string.length()-amount.length();
        if (remainingspace > 0) {
            char[] chars = new char[remainingspace];
            Arrays.fill(chars, ' ');
            stringBuilder.append(string).append(chars).append(amount).append("\n");
        } else
            stringBuilder.append(string).append(amount).append("\n");
        return stringBuilder.toString();
    }
    public String getItemPriceData(int serialNo,String name,String price){
        StringBuilder stringBuilder = new StringBuilder();
        df.setMaximumFractionDigits(2);
        String p=df.format(Float.valueOf(price));
        if(serialNo>9)
            stringBuilder.append(serialNo+". ");
        else
            stringBuilder.append("0"+serialNo+". ");
        int spaceforitemname=CONTENT_LENGTH-(" $"+p).length()-NO_LENGTH;

        if (name.length() > spaceforitemname) {
            int notimes = name.length() / spaceforitemname;
            int rem = name.length() % spaceforitemname;
            for (int i = 0; i < notimes; i++) {
                if (i == 0)
                    stringBuilder.append(name.substring(0, spaceforitemname)).append(" $"+p).append("\n");
                else
                    stringBuilder.append("    ").append(name.substring(i * (spaceforitemname), (i + 1) * spaceforitemname)).append("\n");
            }
            int remainingspace = spaceforitemname - rem ;
            if (remainingspace > 1) {
                char[] chars = new char[remainingspace];
                Arrays.fill(chars, ' ');
                stringBuilder.append("    ").append(name.substring(spaceforitemname * notimes)).append("\n");
            } else
                stringBuilder.append("    ").append(name).append("\n");
        } else {
            int remainingspace = spaceforitemname - name.length() ;
            if (remainingspace > 1) {
                char[] chars = new char[remainingspace];
                Arrays.fill(chars, ' ');
                stringBuilder.append(name).append(chars).append(" $"+p).append("\n");
            } else
                stringBuilder.append(name).append("\n");
        }


        return stringBuilder.toString();
    }

    public String getCenterAlignedData(String str) {
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

    public void printSampleText() {
        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("OHEIDA 3PK SPRINGF");
        itemList.add("3 CUP BLK TEAPOT");
        itemList.add("EMERIL GRIDDLE/PAN");
        itemList.add("CANDYMAKER ASSORT");
        itemList.add("TRIPOD");
        itemList.add("AQUA MICROTERRY SC");
        itemList.add("30L BLK FF DRESS");
        itemList.add("LEVITATING DESKTOP");
        itemList.add("**Blue Overprint P");
        itemList.add("REPOSE 4PCPM CHOC");
        itemList.add("REPOSE 4PCPM CHOC");
        itemList.add("WESTGATE BLACK 25");


        ;
//
//        textData.append("400 OHEIDA 3PK SPRINGF  9.99 R\n");
//        textData.append("410 3 CUP BLK TEAPOT    9.99 R\n");
//        textData.append("445 EMERIL GRIDDLE/PAN 17.99 R\n");
//        textData.append("438 CANDYMAKER ASSORT   4.99 R\n");
//        textData.append("474 TRIPOD              8.99 R\n");
//        textData.append("433 BLK LOGO PRNTED ZO  7.99 R\n");
//        textData.append("458 AQUA MICROTERRY SC  6.99 R\n");
//        textData.append("493 30L BLK FF DRESS   16.99 R\n");
//        textData.append("407 LEVITATING DESKTOP  7.99 R\n");
//        textData.append("441 **Blue Overprint P  2.99 R\n");
//        textData.append("476 REPOSE 4PCPM CHOC   5.49 R\n");
//        textData.append("461 WESTGATE BLACK 25  59.99 R\n");
    }
}
