package com.munchado.orderprocess.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.utils.StringUtils;

/**
 * Created by munchado on 23/2/17.
 */
public class CustomEditText extends TextView {
    public static String TAG = "CustomEditText";

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStyle(attrs);
    }
    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
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
        int mTextStyle = a.getInteger(R.styleable.CustomEditText_textStyle, -1);
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
        }
        return fontFile;
    }


}
