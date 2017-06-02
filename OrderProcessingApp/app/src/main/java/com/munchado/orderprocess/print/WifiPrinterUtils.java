package com.munchado.orderprocess.print;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.util.Log;

import com.hp.mss.hpprint.model.PDFPrintItem;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.model.asset.PDFAsset;
import com.hp.mss.hpprint.util.PrintUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.munchado.orderprocess.utils.Constants;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.PrefUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by munchado on 23/5/17.
 */
public class WifiPrinterUtils {
    //================== DECLARATION FOR WIFI ==================
    public static String CONTENT_TYPE_PDF = "PDF";
    public static String MIME_TYPE_PDF = "application/pdf";

    String contentType;
    PrintItem.ScaleType scaleType;
    PrintAttributes.Margins margins;
    PrintJobData printJobData;
    static final int PICKFILE_RESULT_CODE = 1;
    private Uri userPickedUri;
    PrintAttributes.MediaSize mediaSize5x7;
    Activity ctx;

    public void startPrint(Activity ctx, String printmatter) {
        this.ctx = ctx;
        createPrintJobData(printmatter);
        PrintUtil.setPrintJobData(printJobData);
        PrintUtil.print(this.ctx);
        PrefUtil.putPrinterType(Constants.WIFI);
    }

    public void startPrint(Activity ctx, File file, String jobName) {
        this.ctx = ctx;
        createPrintJobData(file, jobName);
        PrintUtil.setPrintJobData(printJobData);
        PrintUtil.print(this.ctx, Constants.totalPage);
        PrefUtil.putPrinterType(Constants.WIFI);
    }

    public void sendToPrint(Activity ctx, final File file, String jobName) {

        LogUtils.e("==================  $$$$ sendToPrint =============");

        PrintDocumentAdapter pda = new PrintDocumentAdapter() {

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                InputStream input = null;
                OutputStream output = null;

                try {

                    input = new FileInputStream(file);
                    output = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buf = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = input.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }

                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                } catch (FileNotFoundException ee) {
                    //Catch exception
                } catch (Exception e) {
                    //Catch exception
                } finally {
                    try {
                        input.close();
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }


                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder(file.getName()).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

                callback.onLayoutFinished(pdi, true);
            }
        };
        PrintManager printManager = (PrintManager) ctx.getSystemService(Context.PRINT_SERVICE);
        printManager.print(jobName, pda, null);
    }

    private void createPrintJobData(File file1, String jobName) {
        if (file1 != null)
            userPickedUri = Uri.fromFile(file1);
        createUserSelectedPDFJobData();

        //Giving the print job a name.
        printJobData.setJobName(jobName);

        //Optionally include print attributes.
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
                .build();
        printJobData.setPrintDialogOptions(printAttributes);

    }

    private void createPrintJobData(String printData) {
        LogUtils.d("========== print matter : " + printData);
        File file1 = createandDisplayPdf(printData);
        if (file1 != null)
            userPickedUri = Uri.fromFile(file1);
        createUserSelectedPDFJobData();

        //Giving the print job a name.
        printJobData.setJobName("Example");

        //Optionally include print attributes.
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
                .build();
        printJobData.setPrintDialogOptions(printAttributes);

    }


    //Method for creating a pdf file from text, saving it then opening it for display
    public File createandDisplayPdf(String text) {

        Document doc = new Document();
        File file = null;
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            file = new File(dir, "newFile_order.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            Paragraph p1 = new Paragraph(text);
            Font paraFont = new Font(Font.FontFamily.HELVETICA);
//            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);

            //add paragraph to document
            doc.add(p1);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }
        return file;
    }

    private void createUserSelectedPDFJobData() {
        PDFAsset pdfAsset = new PDFAsset(userPickedUri, false);
        PrintItem printItem3x5 = new PDFPrintItem(PrintAttributes.MediaSize.NA_INDEX_3X5, margins, scaleType, pdfAsset);
        printJobData = new PrintJobData(ctx, printItem3x5);
    }


}
