package com.munchado.orderprocess.utils;

import com.munchado.orderprocess.model.orderdetail.AddonsList;
import com.munchado.orderprocess.model.orderdetail.MyItemList;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponseData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by munchado on 24/2/17.
 */
public class ReceiptFormatUtils {

    public static final int CONTENT_LENGTH = 30;
    private static final int NO_LENGTH = 0;
    public static String seperator = "------------------------------\n";
    static DecimalFormat df = new DecimalFormat();


    public static String setPrintData(OrderDetailResponseData orderDetailResponseData) {
        StringBuilder builder = new StringBuilder();
        builder.append(getCenterAlignedData(orderDetailResponseData.restaurant_name));
        builder.append(getCenterAlignedData(orderDetailResponseData.restaurant_address)).append("\n");
        builder.append(getLeftNRightAlignedString("Receipt No.: "+orderDetailResponseData.payment_receipt,""));
        builder.append(getLeftNRightAlignedString("Order Id: "+orderDetailResponseData.id,""));
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

        String strDate = sdf.format(cal.getTime());
        String strDate2 = sdf2.format(cal.getTime());
        builder.append(getLeftNRightAlignedString("Date: "+strDate,"Time: "+strDate2));

        builder.append(seperator);
//        LogUtils.d(builder.toString());
        int i = 1;
        for (MyItemList printItemModel : orderDetailResponseData.item_list) {
            if(printItemModel.addons_list!=null && printItemModel.addons_list.size()>0 && printItemModel.addons_list.get(0).addon_price.equalsIgnoreCase("0.00"))
            {

                builder.append(getItemPriceData(i, printItemModel.item_name+" ("+printItemModel.addons_list.get(0).addon_name+")",printItemModel.item_qty, Integer.valueOf(printItemModel.item_qty) * Float.valueOf(printItemModel.unit_price) + ""));
            }
            else
                builder.append(getItemPriceData(i, printItemModel.item_name,printItemModel.item_qty, Integer.valueOf(printItemModel.item_qty) * Float.valueOf(printItemModel.unit_price) + ""));
            int j = 1;
            for (AddonsList addonsListModel : printItemModel.addons_list) {
                if(addonsListModel.addon_price.equalsIgnoreCase("0.00"))
                    continue;
                builder.append(getSubItemPriceData(j, addonsListModel.addon_name,addonsListModel.addon_quantity, Integer.valueOf(addonsListModel.addon_quantity) * Float.valueOf(addonsListModel.addon_price) + ""));
                j++;
            }
            i++;
        }

        builder.append(seperator);
        builder.append(getAmountCalculation("Subtotal", "$" + orderDetailResponseData.order_amount_calculation.subtotal));
        builder.append(getAmountCalculation("Discount", "$" + orderDetailResponseData.order_amount_calculation.discount));
        builder.append(getAmountCalculation("Delivery", "$" + orderDetailResponseData.order_amount_calculation.delivery_charge));
        builder.append(getAmountCalculation("Tax", "$" + orderDetailResponseData.order_amount_calculation.tax_amount));
        builder.append(getAmountCalculation("Tip", "$" + orderDetailResponseData.order_amount_calculation.tip_amount));
        builder.append(seperator);
        builder.append(getAmountCalculation("Total", "$" + orderDetailResponseData.order_amount_calculation.total_order_price));
        builder.append(seperator);
        builder.append(getCenterAlignedData("See you soon!!")).append("\n");
//        builder.append(getAmountCalculation("Total", "$" + orderDetailResponseData.order_amount_calculation.total_order_price));
//        builder.append(seperator).append("\n\n");
//        builder.append(getCenterAlignedData("See you soon!!"));
//        LogUtils.d("==== Bill: ", builder.toString());
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

    public static String getSubItemPriceData(int serialNo, String name,String qty, String price) {
        name = name.replaceAll("&amp;", "&");
        StringBuilder stringBuilder = new StringBuilder();
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        String p = df.format(Float.valueOf(price));
        if (!p.contains("."))
            p = p + ".00";
        if (Integer.parseInt(qty)  < 10)
            qty=" "+qty;
//        if (serialNo > 9)
//            stringBuilder.append(" " + serialNo + ". ");
//        else
            stringBuilder.append(" - ");
        int spaceforitemname = CONTENT_LENGTH - ("      $" + p).length() - 3;

        if (name.length() > spaceforitemname) {
            LogUtils.d("===== subitem : if");
            int notimes = name.length() / spaceforitemname;
            int rem = name.length() % spaceforitemname;
            for (int i = 0; i < notimes; i++) {

                if (i == 0)
                {
                    String prc=Float.valueOf(p)>100?(" $" + p):(Float.valueOf(p)>10?("  $" + p):("   $" + p));
                    stringBuilder.append(name.substring(0, spaceforitemname)).append("  ").append(qty).append(prc).append("\n");
                }


//                if (i == 0)
//                    stringBuilder.append(name.substring(0, spaceforitemname)).append("  $" + p).append("\n");
                else
                    stringBuilder.append("     ").append(name.substring(i * (spaceforitemname), (i + 1) * spaceforitemname)).append("\n");//
            }
            int remainingspace = spaceforitemname - rem;
            if (remainingspace > 1) {
                char[] chars = new char[remainingspace];
                Arrays.fill(chars, ' ');
                stringBuilder.append("     ").append(name.substring(spaceforitemname * notimes)).append("\n");//.append("    ")
            } else
                stringBuilder.append("     ").append(name).append("\n");//.append("    ")
        } else {
            LogUtils.d("===== subitem : else");
            int remainingspace = spaceforitemname - name.length()-1;
            if (remainingspace > 1) {
                char[] chars = new char[remainingspace];
                Arrays.fill(chars, ' ');
                String prc=Float.valueOf(p)>100?(" $" + p):(Float.valueOf(p)>10?("  $" + p):("   $" + p));
                stringBuilder.append(name).append(chars).append("  ").append(qty).append(prc).append("\n");
//                stringBuilder.append(name).append(chars).append("  $" + p).append("\n");
            } else
                stringBuilder.append("     ").append(name).append("\n");
        }


        return stringBuilder.toString();
    }

    public static String getItemPriceData(int serialNo, String name,String qty, String price) {
        name = name.replaceAll("&amp;", "&");
        StringBuilder stringBuilder = new StringBuilder();
        df.setMaximumFractionDigits(2);
        String p = df.format(Float.valueOf(price));
        if (!p.contains("."))
            p = p + ".00";
        if (Integer.parseInt(qty)  < 10)
            qty=" "+qty;
//        else
//            stringBuilder.append("0" + serialNo + ". ");
        int spaceforitemname = CONTENT_LENGTH - ("      $" + p).length() - NO_LENGTH;

//        stringBuilder.append(displayMultilineString(serialNo,name,spaceforitemname,p));
        if (name.length() > spaceforitemname) {
            int notimes = name.length() / spaceforitemname;
            int rem = name.length() % spaceforitemname;
            for (int i = 0; i < notimes; i++) {
                if (i == 0)
                {
                    String prc=Float.valueOf(p)>100?(" $" + p):(Float.valueOf(p)>10?("  $" + p):("   $" + p));
                    stringBuilder.append(name.substring(0, spaceforitemname)).append("  ").append(qty).append(prc).append("\n");
                }
                else
                    stringBuilder.append(name.substring(i * (spaceforitemname), (i + 1) * spaceforitemname)).append("\n");//.append("    ")
            }
            int remainingspace = spaceforitemname - rem;
            if (remainingspace > 1) {
                char[] chars = new char[remainingspace];
                Arrays.fill(chars, ' ');
                stringBuilder.append(name.substring(spaceforitemname * notimes)).append("\n");//.append("    ")
            } else
                stringBuilder.append(name).append("\n");//.append("    ")
        } else {
            int remainingspace = spaceforitemname - name.length();
            if (remainingspace > 1) {
                char[] chars = new char[remainingspace];
                Arrays.fill(chars, ' ');
                String prc=Float.valueOf(p)>100?(" $" + p):(Float.valueOf(p)>10?("  $" + p):("   $" + p));
                stringBuilder.append(name).append(chars).append("  ").append(qty).append(prc).append("\n");
//                stringBuilder.append(name).append(chars).append("  $" + p).append("\n");
            } else
                stringBuilder.append(name).append("\n");
        }


        return stringBuilder.toString();
    }

    public static String getLeftNRightAlignedString(String left,String right){
        StringBuilder stringBuilder = new StringBuilder();
        int remainingspace=CONTENT_LENGTH-left.length()-right.length();
        char[] chars = new char[remainingspace];
        Arrays.fill(chars, ' ');
        stringBuilder.append(left).append(chars).append(right).append("\n");
        return stringBuilder.toString();
    }

    public static String getCenterAlignedData(String str) {
        str = str.replaceAll("&amp;", "&");
        StringBuilder stringBuilder = new StringBuilder();
        if (str.length() > CONTENT_LENGTH) {
//            boolean flag = true;
//            int index = 0;
//            while (flag) {
//                Model model = getString(str.substring(index, str.length()), CONTENT_LENGTH);
////                LogUtils.d("====== "+str.substring(index,str.length()));
//                index = model.index;
//                int noTimesSpace = (CONTENT_LENGTH - model.data.length()) / 2;
//                char[] chars = new char[noTimesSpace];
//                Arrays.fill(chars, ' ');
//                stringBuilder.append(chars).append(model.data).append(chars).append("\n");
//                if (model.index == -1) {
//                    flag = false;
//                    break;
//                }
//            }
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

    private static Model getString(String str, int length) {
        int lastIndex = -1;
        length -= 3;
        Model model = new Model();
        str = str.trim();
        str = str.trim();
        str = str.trim();
        int index = str.indexOf(" ", 1);
//        str+=" ";
//        LogUtils.d("=== String :"+str+"=== str length :"+str.length()+"=== available length :"+length);
        if (str.length() > length) {
            for (;
                 index <= str.length();
                 index = str.indexOf(" ", index + 1)) {
                if (index >= length || index == 0) {
//                    LogUtils.d("=== Index : "+index+"=== last index : "+lastIndex+"=== Index string 1: "+str.substring(0,index));
                    break;
                } else if (index == -1) {
//                    LogUtils.d("=== Index : "+index+"=== last index : "+lastIndex+"=== Index string 2 : "+str);
                    lastIndex = 0;
                    break;
                }
//                LogUtils.d("=== Index : "+index+"=== last index : "+lastIndex+"=== Index string 3 : "+str.substring(0,index));
                lastIndex = index;
            }
            if (lastIndex > -1) {
                model.data = str.substring(0, lastIndex);
                model.index = index;
            } else if (index == length) {
                model.data = str.substring(0, index);
                model.index = index;
            } else {
                model.data = str;
                model.index = lastIndex;
            }

        } else {
            model.data = str;
            model.index = -1;
//            LogUtils.d("=== Index string 4 : "+str);
        }
        return model;
    }

    static class Model {
        String data;
        int index;
    }

    private static String displayMultilineString(int no, String string, int length, String price) {
        StringBuilder sb = new StringBuilder(string);

        int i = 0;
        int count = 1;
        while ((i = sb.indexOf(" ", i + length)) != -1) {
            if (count == 1)
            {
//                if(no>0)
                sb.replace(i, i + 1, "$" + price + "\n");
//                else sb.replace(i, i + 1, "$" + price + "\n");
            }
            else
                sb.replace(i, i + 1, "\n");
        }
        return sb.toString();
    }
}


