package com.munchado.orderprocess.utils;

import com.munchado.orderprocess.model.archiveorder.ActiveOrderResponseData;
import com.munchado.orderprocess.model.archiveorder.ItemList;
import com.munchado.orderprocess.model.archiveorder.OrderItem;
import com.munchado.orderprocess.model.print.PrintHeaderModel;
import com.munchado.orderprocess.model.print.PrintItemModel;
import com.munchado.orderprocess.model.print.PrintModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by munchado on 24/2/17.
 */
public class PrintUtils {


    public void setPrintData(ActiveOrderResponseData data){
        List<OrderItem> orderItemsList = data.live_order;
        PrintModel mPrintModel=new PrintModel();
        mPrintModel.mPrintItemList.clear();
        for(OrderItem orderItem : orderItemsList){
            PrintHeaderModel mPrintHeaderModel=new PrintHeaderModel();
            mPrintHeaderModel.store_name = orderItem.restaurant_name;
            mPrintHeaderModel.total = Float.valueOf(orderItem.total_amount);
            int i=1;
            for(ItemList mItemModel : orderItem.item_list){
                PrintItemModel printItemModel=new PrintItemModel();
                printItemModel.serial_no=i;
                printItemModel.item_name = mItemModel.item_name;
                printItemModel.quantity = Integer.parseInt(mItemModel.item_qty);

                mPrintModel.mPrintItemList.add(printItemModel);
                i++;
            }
            mPrintModel.mPrintHeaderModel = mPrintHeaderModel;
        }

        LogUtils.d(mPrintModel.mPrintHeaderModel.store_name);
        LogUtils.d("$ "+mPrintModel.mPrintHeaderModel.total);
        LogUtils.d(mPrintModel.mPrintHeaderModel.seperator);
        for(PrintItemModel printItemModel : mPrintModel.mPrintItemList){
            LogUtils.d(printItemModel.serial_no+" "+printItemModel.item_name+" "+printItemModel.quantity);
        }
    }

    public void printSampleText(){
        ArrayList<String> itemList=new ArrayList<>();
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
