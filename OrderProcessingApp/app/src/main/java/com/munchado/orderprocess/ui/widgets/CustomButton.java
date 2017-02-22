package com.munchado.orderprocess.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.utils.StringUtils;


/**
 * Created by android on 1/12/16.
 */

public class CustomButton extends AppCompatButton {
    public CustomButton(Context context) {
        super(context);

    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.borderlessButtonStyle);
        initStyle(attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(attrs);
    }

    private void initStyle(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomButton,
                0, 0);
        int mTextStyle = a.getInteger(R.styleable.CustomButton_textStyle, -1);
        String fontFile = getFontFileName(mTextStyle);
        if (StringUtils.isNullOrEmpty(fontFile))
            fontFile = "Roboto-Medium.ttf";
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
