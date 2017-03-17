package com.munchado.orderprocess.print;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt.CharacterCode;

public abstract class ILocalizeReceipts {
    private int mPaperSize;
    private int mLanguage;

    protected String mLanguageCode;
    private String mPaperSizeStr;
    private String mScalePaperSizeStr;
    protected CharacterCode mCharacterCode;

    public static ILocalizeReceipts createLocalizeReceipts(int language, int paperSize) {
        ILocalizeReceipts localizeReceipts ;

                localizeReceipts = new EnglishReceiptsImpl();

        switch (paperSize) {
            case PrinterSetting.PAPER_SIZE_TWO_INCH:
                localizeReceipts.setPaperSizeStr("2\"");
                localizeReceipts.setScalePaperSizeStr("3\"");   // 3inch -> 2inch
                break;
        }

        localizeReceipts.setLanguage(language);
        localizeReceipts.setPaperSize(paperSize);

        return localizeReceipts;
    }

    public void appendTextReceiptData(ICommandBuilder builder,String printdata, boolean utf8) {
        switch (mPaperSize) {
            case PrinterSetting.PAPER_SIZE_TWO_INCH:
                append2inchTextReceiptData(builder,printdata, utf8);
                break;
            default:
                appendDotImpact3inchTextReceiptData(builder, utf8);
                break;
        }
    }

    public Bitmap createRasterReceiptImage(Resources resources) {
        Bitmap image=null;

        switch (mPaperSize) {
            case PrinterSetting.PAPER_SIZE_TWO_INCH:
                image = create2inchRasterReceiptImage();
                break;
        }

        return image;
    }

    public Bitmap createScaleRasterReceiptImage(Resources resources) {
        Bitmap image;

        switch (mPaperSize) {
            case PrinterSetting.PAPER_SIZE_TWO_INCH:
                image = create3inchRasterReceiptImage();      // 3inch -> 2inch
                break;
            default                                :
                image = createCouponImage(resources);
                break;
        }

        return image;
    }

    public int getLanguage() {
        return mLanguage;
    }

    public void setLanguage(int language) {
        mLanguage = language;
    }

    public void setPaperSize(int paperSize) {
        mPaperSize = paperSize;
    }

    public String getLanguageCode() {
        return mLanguageCode;
    }

    public String getPaperSizeStr() {
        return mPaperSizeStr;
    }

    public void setPaperSizeStr(String paperSizeStr){
        mPaperSizeStr = paperSizeStr;
    }

    public String getScalePaperSizeStr() {
        return mScalePaperSizeStr;
    }

    public void setScalePaperSizeStr(String scalePaperSizeStr){
        mScalePaperSizeStr = scalePaperSizeStr;
    }

    public CharacterCode getCharacterCode() {
        return mCharacterCode;
    }

    public abstract void append2inchTextReceiptData(ICommandBuilder builder,String printdata, boolean utf8);

    public abstract void append3inchTextReceiptData(ICommandBuilder builder, boolean utf8);

    public abstract void  append4inchTextReceiptData(ICommandBuilder builder, boolean utf8);

    public abstract Bitmap create2inchRasterReceiptImage();

    public abstract Bitmap create3inchRasterReceiptImage();

    public abstract Bitmap create4inchRasterReceiptImage();

    public abstract Bitmap createCouponImage(Resources resources);

    public abstract Bitmap createEscPos3inchRasterReceiptImage();

    public abstract void appendEscPos3inchTextReceiptData(ICommandBuilder builder, boolean utf8);

    public abstract void appendDotImpact3inchTextReceiptData(ICommandBuilder builder, boolean utf8);

    static public Bitmap createBitmapFromText(String printText, int textSize, int printWidth, Typeface typeface) {
        Paint paint = new Paint();
        Bitmap bitmap;
        Canvas canvas;

        paint.setTextSize(textSize);
        paint.setTypeface(typeface);

        paint.getTextBounds(printText, 0, printText.length(), new Rect());

        TextPaint textPaint = new TextPaint(paint);
        android.text.StaticLayout staticLayout = new StaticLayout(printText, textPaint, printWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);

        // Create bitmap
        bitmap = Bitmap.createBitmap(staticLayout.getWidth(), staticLayout.getHeight(), Bitmap.Config.ARGB_8888);

        // Create canvas
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.translate(0, 0);
        staticLayout.draw(canvas);

        return bitmap;
    }
}
