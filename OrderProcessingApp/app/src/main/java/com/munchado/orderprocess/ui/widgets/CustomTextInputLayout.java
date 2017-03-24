package com.munchado.orderprocess.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.EditText;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.utils.StringUtils;

import java.lang.reflect.Field;

public class CustomTextInputLayout extends TextInputLayout {

    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStyle(context,attrs);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(context,attrs);
    }


    private void initStyle(Context context,AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomTextInputLayout,
                0, 0);
        int mTextStyle = a.getInteger(R.styleable.CustomTextInputLayout_textStyle, -1);
        String fontFile = getFontFileName(mTextStyle);
        if (StringUtils.isNullOrEmpty(fontFile))
            fontFile = "Roboto-Medium.ttf";

        final Typeface typeface = Typeface.createFromAsset(
                context.getAssets(), fontFile);

        EditText editText = getEditText();
        if (editText != null) {
            editText.setTypeface(typeface);
        }
        try {
            // Retrieve the CollapsingTextHelper Field
            final Field cthf = TextInputLayout.class.getDeclaredField("mCollapsingTextHelper");
            cthf.setAccessible(true);

            // Retrieve an instance of CollapsingTextHelper and its TextPaint
            final Object cth = cthf.get(this);
            final Field tpf = cth.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);

            // Apply your Typeface to the CollapsingTextHelper TextPaint
            ((TextPaint) tpf.get(cth)).setTypeface(typeface);
        } catch (Exception ignored) {
            // Nothing to do
        }

        setTypeface(Typeface.createFromAsset(getContext().getAssets(), fontFile));
    }

    static String getFontFileName(int mTextStyle) {
        String fontFile = "";
        switch (mTextStyle) {
            case 0:
                fontFile = "Roboto.ttf";
                break;
            case 1:
                fontFile = "Roboto-Bold.ttf";
                break;
            case 2:
                fontFile = "Roboto-Light.ttf";
                break;
            case 3:
                fontFile = "Roboto-Regular.ttf";
                break;
            case 4:
                fontFile = "Roboto-Thin.ttf";
                break;
            case 5:
                fontFile = "Roboto-Medium.ttf";
                break;
            case 6:
                fontFile = "Baskerville.ttf";
                break;
            case 7:
                fontFile = "Avenir-Next-Medium.ttf";
                break;
            case 8:
                fontFile = "Avenir-Next-Demi-Bold.ttf";
                break;
            case 9:
                fontFile = "Avenir-Next-Bold.ttf";
                break;
            case 10:
                fontFile = "FuturaMedium.ttf";
                break;
            case 11:
                fontFile = "Futura_Book.ttf";
                break;
            case 12:
                fontFile = "Avenir_Light.ttf";

                break;
            case 13:
                fontFile = "Avenir_Medium.ttf";

                break;
            case 14:
                fontFile = "Avenir-Next-Condensed-Medium.ttf";
                break;
            case 15:
                fontFile = "Avenir-Next-Condensed-Italic.ttf";
                break;
            case 16:
                fontFile = "Avenir-Next-Regular.ttf";
                break;
            case 17:
                fontFile = "Avenir-Next-UltraLight.ttf";
                break;
        }
        return fontFile;
    }
}