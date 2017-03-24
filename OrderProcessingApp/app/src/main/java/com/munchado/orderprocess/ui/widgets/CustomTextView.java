package com.munchado.orderprocess.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.utils.StringUtils;

/**
 * Created by android on 1/12/16.
 */

public class CustomTextView extends TextView {
    public static String TAG = "CustomTextView";

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStyle(attrs);
    }
    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(attrs);
    }

    private void initStyle(AttributeSet attrs) {
        if (isInEditMode())
            return;
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomTextView,
                0, 0);
        int mTextStyle = a.getInteger(R.styleable.CustomTextView_textStyle, -1);
        String fontFile = getFontFileName(mTextStyle);
        if(StringUtils.isNullOrEmpty(fontFile))
            fontFile = "Roboto-Light.ttf";
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
