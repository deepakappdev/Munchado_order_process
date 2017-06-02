package com.munchado.orderprocess.utils;

import android.app.Activity;
import android.os.Environment;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.munchado.orderprocess.model.orderdetail.AddonsList;
import com.munchado.orderprocess.model.orderdetail.MyItemList;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponseData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by munchado on 23/5/17.
 */
public class WifiPrintReceiptFormatUtils {

    private final float OUTER_WIDTH = 272.13f;
    private final float INNER_WIDTH = 265.0f;
    Font contentFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    Font contentBoldFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    Font subitemFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
    Font contentLargeFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
    Font topHeadingFont = new Font(Font.FontFamily.HELVETICA, 19, Font.BOLD);
    Font foodHeadingFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
    Font contentLargeBoldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

    public File createPDF(OrderDetailResponseData orderDetailResponseData, String fileName, Activity context) {
        File file;
        final String APPLICATION_PACKAGE_NAME = context.getPackageName();
        File path = new File(Environment.getExternalStorageDirectory(), APPLICATION_PACKAGE_NAME);
        if (!path.exists()) {
            path.mkdir();
        }
        file = new File(path, fileName);

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            PdfPTable outerTable = new PdfPTable(1);
            setDefaultTableProperty(outerTable, OUTER_WIDTH);
            PdfPCell outerCell = new PdfPCell();

            PdfPTable deliveryDateTimeTable = new PdfPTable(1);
            setDefaultTableProperty(deliveryDateTimeTable, INNER_WIDTH);

            // DATA FOR TIME & ORDER TYPE : 10:06 DELIVERY
            PdfPCell deliveryDateTimeCell = null;
            String time = DateTimeUtils.getFormattedDate(orderDetailResponseData.delivery_date, DateTimeUtils.FORMAT_HH_MM_A);
            String date = DateTimeUtils.getFormattedDate(orderDetailResponseData.delivery_date, DateTimeUtils.FORMAT_MMM_DD_YYYY);
            if (orderDetailResponseData.order_type.equalsIgnoreCase("takeout")) {
                deliveryDateTimeCell = getGenericTableCell(time + " PICKUP", Element.ALIGN_CENTER, topHeadingFont);
            } else {
                deliveryDateTimeCell = getGenericTableCell(time + " DELIVERY", Element.ALIGN_CENTER, topHeadingFont);
            }
            deliveryDateTimeTable.addCell(deliveryDateTimeCell);

            // DATA FOR DATE & TIME : Prepare for delivery before 10:06 AM on May 22, 2017
            PdfPCell prepareCell = new PdfPCell();
            if (orderDetailResponseData.order_type.equalsIgnoreCase("takeout")) {
                Phrase phrase = new Phrase();
                phrase.add(new Chunk("Prepare for pickup before ", contentFont));
                phrase.add(new Chunk(time, contentBoldFont));
                phrase.add(new Chunk(" on \n", contentFont));
                phrase.add(new Chunk(date, contentBoldFont));
                Paragraph paragraph = new Paragraph(phrase);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                prepareCell.addElement(paragraph);
            } else {
                Phrase phrase = new Phrase();
                phrase.add(new Chunk("Prepare for delivery before ", contentFont));
                phrase.add(new Chunk(time, contentBoldFont));
                phrase.add(new Chunk(" on \n", contentFont));
                phrase.add(new Chunk(date, contentBoldFont));
                Paragraph paragraph = new Paragraph(phrase);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                prepareCell.addElement(paragraph);
            }
            prepareCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            prepareCell.setBorder(Rectangle.NO_BORDER);
            deliveryDateTimeTable.addCell(prepareCell);

            // ADD A SEPERATOR
            PdfPCell blankCell = new PdfPCell();
            blankCell.addElement(Chunk.NEWLINE);
            blankCell.setBorder(Rectangle.NO_BORDER);
            blankCell.setPaddingBottom(-10.0f);
            deliveryDateTimeTable.addCell(blankCell);
            deliveryDateTimeTable.addCell(new PdfPCell());

            // DATA FOR RESTAURANT NAME : EL Original TXMX
            PdfPCell restaurantCell = getGenericTableCell(Utils.decodeHtml(orderDetailResponseData.restaurant_name) + "\nORDER #" + orderDetailResponseData.payment_receipt + "\n ", Element.ALIGN_CENTER, contentLargeFont);
            restaurantCell.setPaddingBottom(-7f);
            deliveryDateTimeTable.addCell(restaurantCell);

            // ADD A SEPERATOR
            PdfPCell blankCell2 = new PdfPCell();
            deliveryDateTimeTable.addCell(blankCell2);

            // ADD ITEM TABLE HEADING
            PdfPCell foodheadingcell = new PdfPCell();
            PdfPTable foodHeadingTable = new PdfPTable(4);
            setDefaultTableProperty(foodHeadingTable, INNER_WIDTH);
            PdfPCell foodheadingcell1 = getTableHeadingCell("Food Item(s)", Element.ALIGN_LEFT);
            foodheadingcell1.setColspan(2);
            PdfPCell foodheadingcell2 = getTableHeadingCell("Quantity", Element.ALIGN_LEFT);
            PdfPCell foodheadingcell3 = getTableHeadingCell("Price", Element.ALIGN_RIGHT);
            foodHeadingTable.addCell(foodheadingcell1);
            foodHeadingTable.addCell(foodheadingcell2);
            foodHeadingTable.addCell(foodheadingcell3);
            foodHeadingTable.setKeepTogether(true);
            foodheadingcell.addElement(foodHeadingTable);
            foodheadingcell.setBorder(Rectangle.NO_BORDER);
            deliveryDateTimeTable.addCell(foodheadingcell);

            // ADD ITEMS
            NumberFormat formatter = new DecimalFormat("#0.00");
            for (MyItemList printItemModel : orderDetailResponseData.item_list) {

                PdfPCell foodheadingcell_1 = new PdfPCell();
                PdfPTable foodHeadingTable_1 = new PdfPTable(4);
                setDefaultTableProperty(foodHeadingTable_1, INNER_WIDTH);
                PdfPCell foodheadingcell1_1 = getTableCell(Utils.decodeHtml(printItemModel.item_name), Element.ALIGN_LEFT);
                foodheadingcell1_1.setColspan(2);
                PdfPCell foodheadingcell2_1 = getTableCell("   " + printItemModel.item_qty, Element.ALIGN_LEFT);
                PdfPCell foodheadingcell3_1 = getTableCell("$ " + formatter.format(Integer.valueOf(printItemModel.item_qty) * Float.valueOf(printItemModel.unit_price)), Element.ALIGN_RIGHT);
                foodHeadingTable_1.addCell(foodheadingcell1_1);
                foodHeadingTable_1.addCell(foodheadingcell2_1);
                foodHeadingTable_1.addCell(foodheadingcell3_1);
                foodHeadingTable_1.setKeepTogether(true);
                foodheadingcell_1.addElement(foodHeadingTable_1);
                foodheadingcell_1.setBorder(Rectangle.NO_BORDER);
                foodheadingcell_1.setHorizontalAlignment(Element.ALIGN_LEFT);
                deliveryDateTimeTable.addCell(foodheadingcell_1);
                if (!StringUtils.isNullOrEmpty(printItemModel.item_special_instruction)) {
                    // DATA FOR RESTAURANT NAME : EL Original TXMX
                    PdfPCell restaurantCell_2 = getTableHeadingCell("Special Instructions: \n" + Utils.decodeHtml(printItemModel.item_special_instruction), Element.ALIGN_LEFT);//new PdfPCell();
                    deliveryDateTimeTable.addCell(restaurantCell_2);
                }

                for (AddonsList addonsListModel : printItemModel.addons_list) {
//                    builder.append(getSubItemPriceData(j, Utils.decodeHtml(addonsListModel.addon_name), addonsListModel.addon_quantity, Integer.valueOf(addonsListModel.addon_quantity) * Float.valueOf(addonsListModel.addon_price) + ""));
                    PdfPCell foodheadingcell_3 = new PdfPCell();
                    PdfPTable foodHeadingTable_3 = new PdfPTable(4);
                    setDefaultTableProperty(foodHeadingTable_3, INNER_WIDTH);

                    PdfPCell foodheadingcell1_3 = getTableSubitemCell("  + " + Utils.decodeHtml(addonsListModel.addon_name), Element.ALIGN_LEFT);
                    foodheadingcell1_3.setColspan(2);
                    PdfPCell foodheadingcell2_3 = getTableSubitemCell("    " + Utils.decodeHtml(addonsListModel.addon_quantity), Element.ALIGN_LEFT);//= new PdfPCell();
                    PdfPCell foodheadingcell3_3 = getTableSubitemCell("$ " + formatter.format(Integer.valueOf(addonsListModel.addon_quantity) * Float.valueOf(addonsListModel.addon_price)), Element.ALIGN_RIGHT);//= new PdfPCell();
                    foodHeadingTable_3.addCell(foodheadingcell1_3);
                    foodHeadingTable_3.addCell(foodheadingcell2_3);
                    foodHeadingTable_3.addCell(foodheadingcell3_3);
                    foodheadingcell_3.addElement(foodHeadingTable_3);
                    foodHeadingTable_3.setKeepTogether(true);
                    foodheadingcell_3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    foodheadingcell_3.setBorder(Rectangle.NO_BORDER);
                    deliveryDateTimeTable.addCell(foodheadingcell_3);
                }
            }

            if (!StringUtils.isNullOrEmpty(orderDetailResponseData.special_instruction)) {
                // ADD A SEPERATOR FOR SPECIAL INSTRUCTIONS
                deliveryDateTimeTable.addCell(blankCell);
                deliveryDateTimeTable.addCell(new PdfPCell());

                // DATA FOR SPECIAL INSTRUCTIONS :  HEADING
                PdfPCell specialinstructionsCell = null;
                Paragraph specialinstructionsparagraph = null;
                if (orderDetailResponseData.order_type.equalsIgnoreCase("takeout")) {
                    specialinstructionsCell = getGenericTableCell("Special Instructions (for Pickup):", Element.ALIGN_LEFT, contentLargeBoldFont);
                } else {
                    specialinstructionsCell = getGenericTableCell("Special Instructions (for Delivery):", Element.ALIGN_LEFT, contentLargeBoldFont);
                }
                deliveryDateTimeTable.addCell(specialinstructionsCell);

                // DATA FOR SPECIAL INSTRUCTIONS VALUE
                PdfPCell specialinstructionsCell_1 = getGenericTableCell(Utils.decodeHtml(orderDetailResponseData.special_instruction), Element.ALIGN_LEFT, subitemFont);//= new PdfPCell();
                deliveryDateTimeTable.addCell(specialinstructionsCell_1);

                // ADD A SEPERATOR FOR SPECIAL INSTRUCTIONS
                deliveryDateTimeTable.addCell(blankCell);
                deliveryDateTimeTable.addCell(new PdfPCell());
            }

            // DATA FOR SUBTOTAL
            PdfPCell subTotalCell = new PdfPCell();
            Paragraph subTotalParagraph = new Paragraph("SUBTOTAL: $" + orderDetailResponseData.order_amount_calculation.subtotal + "\n", contentLargeBoldFont);
            subTotalParagraph.setAlignment(Element.ALIGN_RIGHT);
            subTotalCell.addElement(subTotalParagraph);
            subTotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            subTotalCell.setBorder(Rectangle.NO_BORDER);
            subTotalCell.setPaddingBottom(10.0f);
            deliveryDateTimeTable.addCell(subTotalCell);

            // PAYMENT COLLECTED BY MUNCH ADO
            PdfPCell paymentCell = new PdfPCell();
            Paragraph paymentParagraph = new Paragraph("Payment Collected by\nMUNCH ADO", contentLargeBoldFont);
            paymentParagraph.setAlignment(Element.ALIGN_CENTER);
            paymentCell.addElement(paymentParagraph);
            paymentCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            paymentCell.setPaddingBottom(10.0f);
            deliveryDateTimeTable.addCell(paymentCell);

            // DATA FOR SUBTOTAL 2
            PdfPCell subTotalCell_2 = new PdfPCell();
            Paragraph subTotalParagraph_2 = new Paragraph("SUBTOTAL: $" + orderDetailResponseData.order_amount_calculation.subtotal, contentLargeFont);
            subTotalParagraph_2.setAlignment(Element.ALIGN_RIGHT);
            subTotalCell_2.addElement(subTotalParagraph_2);
            subTotalCell_2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            subTotalCell_2.setBorder(Rectangle.NO_BORDER);
            deliveryDateTimeTable.addCell(subTotalCell_2);

            // DATA FOR DELIVERY FEE
            if (!orderDetailResponseData.order_type.equalsIgnoreCase("takeout")) {
                PdfPCell deliveryCell_2 = new PdfPCell();
                Paragraph deliveryParagraph_2 = new Paragraph("Subtotal: $" + orderDetailResponseData.order_amount_calculation.delivery_charge, contentLargeFont);
                deliveryParagraph_2.setAlignment(Element.ALIGN_RIGHT);
                deliveryCell_2.addElement(deliveryParagraph_2);
                deliveryCell_2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                deliveryCell_2.setBorder(Rectangle.NO_BORDER);
                deliveryDateTimeTable.addCell(deliveryCell_2);
            }

            // DATA FOR Tax
            PdfPCell taxCell = new PdfPCell();
            Paragraph taxParagraph = new Paragraph("Tax: $" + orderDetailResponseData.order_amount_calculation.tax_amount, contentLargeFont);
            taxParagraph.setAlignment(Element.ALIGN_RIGHT);
            taxCell.addElement(taxParagraph);
            taxCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            taxCell.setBorder(Rectangle.NO_BORDER);
            deliveryDateTimeTable.addCell(taxCell);

            // DATA FOR Tip
            PdfPCell tipCell = new PdfPCell();
            Paragraph tipParagraph = new Paragraph("Tip: $" + orderDetailResponseData.order_amount_calculation.tip_amount, contentLargeFont);
            tipParagraph.setAlignment(Element.ALIGN_RIGHT);
            tipCell.addElement(tipParagraph);
            tipCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tipCell.setBorder(Rectangle.NO_BORDER);
            deliveryDateTimeTable.addCell(tipCell);

            // DATA FOR total
            PdfPCell totalCell = new PdfPCell();
            Paragraph totalParagraph = new Paragraph("Total: $" + orderDetailResponseData.order_amount_calculation.total_order_price, contentLargeFont);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            totalCell.addElement(totalParagraph);
            totalCell.setPaddingBottom(20.0f);
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalCell.setBorder(Rectangle.NO_BORDER);
            deliveryDateTimeTable.addCell(totalCell);

            // ADD A SEPERATOR FOR SPECIAL INSTRUCTIONS
            deliveryDateTimeTable.addCell(blankCell);
            deliveryDateTimeTable.addCell(blankCell);
            deliveryDateTimeTable.addCell(new PdfPCell());

            // DATA FOR customer signature
            PdfPCell customerCell = getGenericTableCell("Customer signature", Element.ALIGN_RIGHT, subitemFont);
            deliveryDateTimeTable.addCell(customerCell);

            // DATA FOR RESTAURANT NAME : EL Original TXMX
            PdfPCell restaurant2Cell = getGenericTableCell(Utils.decodeHtml(orderDetailResponseData.restaurant_name), Element.ALIGN_LEFT, contentLargeBoldFont);
            deliveryDateTimeTable.addCell(restaurant2Cell);

            // DATA FOR RESTAURANT NAME : EL Original TXMX
            PdfPCell receiptCell = getGenericTableCell("ORDER #" + orderDetailResponseData.payment_receipt + "\nPrepare before " + time + " on " + date + "\nTotal: $" + orderDetailResponseData.order_amount_calculation.total_order_price, Element.ALIGN_LEFT, contentLargeFont);
            deliveryDateTimeTable.addCell(receiptCell);
            deliveryDateTimeTable.addCell(blankCell);

            PdfPCell deliveryCell_2 = null;

            if (!orderDetailResponseData.order_type.equalsIgnoreCase("takeout")) {
                deliveryCell_2 = getGenericTableCell("Delivery order:", Element.ALIGN_LEFT, contentLargeBoldFont);
            } else
                deliveryCell_2 = getGenericTableCell("Pickup order:", Element.ALIGN_LEFT, contentLargeBoldFont);
            deliveryDateTimeTable.addCell(deliveryCell_2);

            // DATA FOR USER DETAILS : TOM
            PdfPCell userCell = getGenericTableCell(orderDetailResponseData.customer_first_name + (!StringUtils.isNullOrEmpty(orderDetailResponseData.customer_last_name) ? " " + orderDetailResponseData.customer_last_name : ""), Element.ALIGN_LEFT, contentLargeFont);
            deliveryDateTimeTable.addCell(userCell);

            // DATA FOR USER phone : TOM
            PdfPCell userphoneCell = getGenericTableCell(orderDetailResponseData.my_delivery_detail.phone, Element.ALIGN_LEFT, contentLargeBoldFont);
            userphoneCell.setBorder(Rectangle.NO_BORDER);


            if (!orderDetailResponseData.order_type.equalsIgnoreCase("takeout")) {
                deliveryDateTimeTable.addCell(userphoneCell);
                // DATA FOR USER address : TOM
                PdfPCell useraddressCell = getGenericTableCell(orderDetailResponseData.my_delivery_detail.address + "\n" + orderDetailResponseData.my_delivery_detail.city + " - " + orderDetailResponseData.my_delivery_detail.zipcode, Element.ALIGN_LEFT, contentLargeFont);
                useraddressCell.setPaddingBottom(25.0f);
                deliveryDateTimeTable.addCell(useraddressCell);
                deliveryDateTimeTable.addCell(blankCell);
            } else {
                userphoneCell.setPaddingBottom(25.0f);
                deliveryDateTimeTable.addCell(userphoneCell);
            }

            // Questions
            PdfPCell questionCell = new PdfPCell();
            Paragraph questionParagraph = new Paragraph("QUESTIONS?\nCustomer service is available", contentLargeFont);
            questionParagraph.setAlignment(Element.ALIGN_CENTER);
            questionCell.addElement(questionParagraph);
            Paragraph questionParagraph2 = new Paragraph("1-888-345-1303\n", contentLargeBoldFont);
            questionParagraph2.setAlignment(Element.ALIGN_CENTER);
            questionCell.addElement(questionParagraph2);
            questionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            deliveryDateTimeTable.addCell(questionCell);
            deliveryDateTimeTable.setKeepTogether(true);
            outerCell.addElement(deliveryDateTimeTable);
            outerCell.setPadding(3.0f);
            outerTable.addCell(outerCell);

            if (outerTable.getRowHeight(0) > 770.0f) {
                int totalpage = (int) outerTable.getRowHeight(0) / 770;
                Constants.totalPage = totalpage + 1;
            }
            LogUtils.e("==== totalPage : " + Constants.totalPage);
            document.add(outerTable);

            document.close();
            return file;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setDefaultTableProperty(PdfPTable outerTable, float width) {
        outerTable.setTotalWidth(width);
        outerTable.setSpacingAfter(0);
        outerTable.setSpacingBefore(0);
        outerTable.setLockedWidth(true);
        outerTable.setSplitLate(false);
    }

    private PdfPCell getTableHeadingCell(String details, int alignment) {
        PdfPCell tableCell = new PdfPCell();
        Paragraph paragraph1 = new Paragraph(details, foodHeadingFont);
        paragraph1.setAlignment(alignment);
        tableCell.addElement(paragraph1);
        tableCell.setBorder(Rectangle.NO_BORDER);
        tableCell.setHorizontalAlignment(alignment);
        return tableCell;
    }

    private PdfPCell getTableCell(String details, int alignment) {
        PdfPCell tableCell = new PdfPCell();
        Paragraph paragraph1 = new Paragraph(details, contentFont);
        paragraph1.setAlignment(alignment);
        tableCell.addElement(paragraph1);
        tableCell.setBorder(Rectangle.NO_BORDER);
        tableCell.setHorizontalAlignment(alignment);
        return tableCell;
    }

    private PdfPCell getTableSubitemCell(String details, int alignment) {
        PdfPCell tableCell = new PdfPCell();
        Paragraph paragraph1 = new Paragraph(details, subitemFont);
        paragraph1.setAlignment(alignment);
        tableCell.addElement(paragraph1);
        tableCell.setBorder(Rectangle.NO_BORDER);
        tableCell.setHorizontalAlignment(alignment);
        return tableCell;
    }

    private PdfPCell getGenericTableCell(String details, int alignment, Font font) {
        PdfPCell tableCell = new PdfPCell();
        Paragraph paragraph1 = new Paragraph(details, font);
        paragraph1.setAlignment(alignment);
        tableCell.addElement(paragraph1);
        tableCell.setBorder(Rectangle.NO_BORDER);
        tableCell.setHorizontalAlignment(alignment);
        return tableCell;
    }
}
