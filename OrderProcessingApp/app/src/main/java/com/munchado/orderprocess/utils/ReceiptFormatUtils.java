package com.munchado.orderprocess.utils;

import com.munchado.orderprocess.model.orderdetail.AddonsList;
import com.munchado.orderprocess.model.orderdetail.MyItemList;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponseData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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

//        builder.append(getCenterSplitArray(orderDetailResponseData.restaurant_name, CONTENT_LENGTH));
//        builder.append(getCenterSplitArray(orderDetailResponseData.restaurant_address, CONTENT_LENGTH)).append("\n");
        builder.append(getCenterSplitArray(Utils.decodeHtml(orderDetailResponseData.restaurant_name), CONTENT_LENGTH));
        builder.append(getCenterSplitArray(Utils.decodeHtml(orderDetailResponseData.restaurant_address), CONTENT_LENGTH)).append("\n");
        builder.append(getLeftNRightAlignedString("Receipt No.: " + orderDetailResponseData.payment_receipt, ""));
        builder.append(getLeftNRightAlignedString("Order Id: " + orderDetailResponseData.id, ""));
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

        String strDate = sdf.format(cal.getTime());
        String strDate2 = sdf2.format(cal.getTime());
        builder.append(getLeftNRightAlignedString("Date: " + strDate, "Time: " + strDate2));

        builder.append(seperator);
//        LogUtils.d(builder.toString());
        int i = 1;
        for (MyItemList printItemModel : orderDetailResponseData.item_list) {
            if (!StringUtils.isNullOrEmpty(printItemModel.item_price_desc))
                builder.append(getItemPriceData(i, Utils.decodeHtml(printItemModel.item_name) + " (" + Utils.decodeHtml(printItemModel.item_price_desc) + ")", printItemModel.item_qty, Integer.valueOf(printItemModel.item_qty) * Float.valueOf(printItemModel.unit_price) + ""));
            else
                builder.append(getItemPriceData(i, Utils.decodeHtml(printItemModel.item_name), printItemModel.item_qty, Integer.valueOf(printItemModel.item_qty) * Float.valueOf(printItemModel.unit_price) + ""));
            int j = 1;
            for (AddonsList addonsListModel : printItemModel.addons_list) {
                builder.append(getSubItemPriceData(j, Utils.decodeHtml(addonsListModel.addon_name), addonsListModel.addon_quantity, Integer.valueOf(addonsListModel.addon_quantity) * Float.valueOf(addonsListModel.addon_price) + ""));
                j++;
            }
            i++;
        }

        builder.append(seperator);
        builder.append(getAmountCalculation("Subtotal", orderDetailResponseData.order_amount_calculation.subtotal));
        builder.append(getAmountCalculation("Discount", orderDetailResponseData.order_amount_calculation.discount));

        if (StringUtils.isNullOrEmpty(orderDetailResponseData.order_amount_calculation.promocode_discount) || orderDetailResponseData.order_amount_calculation.promocode_discount.equalsIgnoreCase("0") || orderDetailResponseData.order_amount_calculation.promocode_discount.equalsIgnoreCase("0.00")) {
        } else {
            builder.append(getAmountCalculation("Promocode Discount", orderDetailResponseData.order_amount_calculation.promocode_discount));
        }

        if (!orderDetailResponseData.order_type.equalsIgnoreCase("takeout"))
            builder.append(getAmountCalculation("Delivery", orderDetailResponseData.order_amount_calculation.delivery_charge));
        builder.append(getAmountCalculation("Tax", orderDetailResponseData.order_amount_calculation.tax_amount));
        if (!orderDetailResponseData.order_type.equalsIgnoreCase("takeout"))
            builder.append(getAmountCalculation("Tip", orderDetailResponseData.order_amount_calculation.tip_amount));
        builder.append(seperator);
        builder.append(getAmountCalculation("Total", orderDetailResponseData.order_amount_calculation.total_order_price));
        builder.append(seperator);
        builder.append(getCenterAlignedData("See you soon!!")).append("\n");
        return builder.toString();
    }

    public static String getAmountCalculation(String string, String amount) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!amount.contains(".")) {
            amount = amount + ".00";
            float value = Float.valueOf(amount);
            if ((int) value == 0) {
                amount = "00.00";
            }
        } else {
            float value = Float.valueOf(amount);
            if ((int) value < 10) {
                amount = "0" + amount;
            }

            if ((int) value == 0) {

                int retval = Float.compare(value, 0.00f);
                if (retval > 0) {
                } else
                    amount = "00.00";
            }
        }
        amount = "$" + amount;
        int remainingspace = CONTENT_LENGTH - string.length() - amount.length();
        if (remainingspace > 0) {
            char[] chars = new char[remainingspace];
            Arrays.fill(chars, ' ');
            stringBuilder.append(string).append(chars).append(amount).append("\n");
        } else
            stringBuilder.append(string).append(amount).append("\n");
        return stringBuilder.toString();
    }

    public static String getSubItemPriceData(int serialNo, String name, String qty, String price) {
        name = name.replaceAll("&amp;", "&");
        StringBuilder stringBuilder = new StringBuilder();
        price += "00";
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        String p = df.format(Float.valueOf(price));
        if (!p.contains("."))
            p = p + ".00";
        if (p.contains(","))
            p = p.replaceAll(",", "");
        float value = Float.valueOf(p);
        if ((int) value < 10) {
            p = "0" + p;
        }
        if (Integer.parseInt(qty) < 10)
            qty = " " + qty;
//        if (serialNo > 9)
//            stringBuilder.append(" " + serialNo + ". ");
//        else
        stringBuilder.append(" - ");
        int spaceforitemname = CONTENT_LENGTH - ("      $" + p).length() - 3;
        spaceforitemname = 15;
        if (name.length() > spaceforitemname) {

            List<String> list = new ArrayList<>();
            list.addAll(getSplitArray(name, spaceforitemname));
            for (int j = 0; j < list.size(); j++) {
                if (j == 0) {

                    int remainingspace = spaceforitemname - list.get(j).length();
                    char[] chars = new char[remainingspace + 2];
                    Arrays.fill(chars, ' ');

                    String prc = Float.valueOf(p) >= 100 ? (" $" + p) : ("  $" + p);
                    stringBuilder.append(list.get(j)).append(chars).append(qty).append(prc).append("\n");
                } else
                    stringBuilder.append(list.get(j)).append("\n");
            }

        } else {

            int remainingspace = spaceforitemname - name.length();
//            int remainingspace = 15 - name.length();
//            LogUtils.d("===== subitem : else spaceforitemname : "+spaceforitemname+"== name.length():"+name.length()+"=== remainingspace : "+remainingspace);
//            if (remainingspace >= 1) {
            char[] chars = new char[remainingspace];
            Arrays.fill(chars, ' ');
            String prc = Float.valueOf(p) >= 100 ? (" $" + p) : ("  $" + p);
            stringBuilder.append(name).append(chars).append("  ").append(qty).append(prc).append("\n");
//                stringBuilder.append(name).append(chars).append("  $" + p).append("\n");
//            } else
//                stringBuilder.append("   ").append(name).append("\n");
        }


        return stringBuilder.toString();
    }

    public static String getItemPriceData(int serialNo, String name, String qty, String price) {
        name = name.replaceAll("&amp;", "&");
        StringBuilder stringBuilder = new StringBuilder();
        price += "00";

        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        String p = df.format(Float.valueOf(price));
        if (!p.contains("."))
            p = p + ".00";
        if (p.contains(","))
            p = p.replaceAll(",", "");
        float value = Float.valueOf(p);
        if ((int) value < 10) {
            p = "0" + p;
        }
        if (Integer.parseInt(qty) < 10)
            qty = " " + qty;
//        else
//            stringBuilder.append("0" + serialNo + ". ");
        int spaceforitemname = CONTENT_LENGTH - ("      $" + p).length() - 3;
        spaceforitemname = 18;
//        stringBuilder.append(displayMultilineString(serialNo,name,spaceforitemname,p));
        if (name.length() >= spaceforitemname) {

            List<String> list = new ArrayList<>();
            list.addAll(getSplitArray(name, spaceforitemname));
//            LogUtils.d("===== name: "+name+"==== length: "+list.get(0).length());
            for (int j = 0; j < list.size(); j++) {
                if (j == 0) {

                    int remainingspace = spaceforitemname - list.get(j).length();

                    char[] chars = new char[remainingspace + 2];
                    Arrays.fill(chars, ' ');

                    String prc = Float.valueOf(p) >= 100 ? (" $" + p) : ("  $" + p);
                    stringBuilder.append(list.get(j)).append(chars).append(qty).append(prc).append("\n");
                } else
                    stringBuilder.append(list.get(j)).append("\n");
            }

        } else {
            int remainingspace = spaceforitemname - name.length();
//            LogUtils.d("######## name: "+name+"==== length: "+name.length()+"==== remaining space : "+remainingspace);
            if (remainingspace > 0) {
                char[] chars = new char[remainingspace];
                Arrays.fill(chars, ' ');
                String prc = Float.valueOf(p) >= 100 ? (" $" + p) : ("  $" + p);
                stringBuilder.append(name).append(chars).append("  ").append(qty).append(prc).append("\n");
//                stringBuilder.append(name).append(chars).append("  $" + p).append("\n");
            } else
                stringBuilder.append(name).append("\n");
        }


        return stringBuilder.toString();
    }

    public static String getLeftNRightAlignedString(String left, String right) {
        StringBuilder stringBuilder = new StringBuilder();
        int remainingspace = CONTENT_LENGTH - left.length() - right.length();
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
            if (count == 1) {
//                if(no>0)
                sb.replace(i, i + 1, "$" + price + "\n");
//                else sb.replace(i, i + 1, "$" + price + "\n");
            } else
                sb.replace(i, i + 1, "\n");
        }
        return sb.toString();
    }

    static ArrayList<String> getSplitArray(String itemName, int maxLength) {
        itemName = itemName.replaceAll("&amp;", "&");
        String[] splitString = itemName.split(" ");
        int nameLength = splitString.length;
        ArrayList<String> stringArray = new ArrayList<>();
        int index = 0;
        while (index < nameLength) {
            String finalLine = splitString[index];

            while (index < nameLength - 1) {
                String testLine = finalLine + " " + splitString[index + 1];
                if (testLine.length() <= maxLength) {
                    finalLine = testLine;
                    index++;
                } else {
                    break;
                }
            }
            stringArray.add(finalLine);
            index++;
        }
        return stringArray;
    }

    static String getCenterSplitArray(String itemName, int maxLength) {
        itemName = itemName.replaceAll("&amp;", "&");
        String[] splitString = itemName.split(" ");
        int nameLength = splitString.length;
//        ArrayList<String> stringArray = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        while (index < nameLength) {
            String finalLine = splitString[index];

            while (index < nameLength - 1) {
                String testLine = finalLine + " " + splitString[index + 1];
                if (testLine.length() <= maxLength) {
                    finalLine = testLine;
                    index++;
                } else {
                    break;
                }
            }
            int spaceCount = (maxLength - finalLine.length()) / 2;
            stringBuilder.append(getSpaceString(spaceCount) + finalLine).append("\n");
            index++;
        }
        return stringBuilder.toString();
    }

    private static String getSpaceString(int spaceCount) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < spaceCount; index++) {
            builder.append(" ");
        }
        return builder.toString();

    }
}


