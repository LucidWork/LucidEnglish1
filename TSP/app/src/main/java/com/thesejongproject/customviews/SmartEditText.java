package com.thesejongproject.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.thesejongproject.R;
import com.thesejongproject.smart.SmartApplication;

public class SmartEditText extends AppCompatEditText {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public SmartEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context, attrs);
    }

    public SmartEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);

        Typeface customFont = selectTypeface(context, textStyle);

        setTypeface(customFont);
    }

    private Typeface selectTypeface(Context context, int textStyle) {
        if (textStyle == Typeface.BOLD) {
            if (SmartApplication.REF_SMART_APPLICATION.BOLDFONT != null) {

                return SmartApplication.REF_SMART_APPLICATION.BOLDFONT;
            } else {

                Typeface typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_bold));
                SmartApplication.REF_SMART_APPLICATION.BOLDFONT = typeface;
                return SmartApplication.REF_SMART_APPLICATION.BOLDFONT;
            }
        } else {
            if (SmartApplication.REF_SMART_APPLICATION.FONT != null) {

                return SmartApplication.REF_SMART_APPLICATION.FONT;
            } else {

                Typeface typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_normal));
                SmartApplication.REF_SMART_APPLICATION.FONT = typeface;
                return SmartApplication.REF_SMART_APPLICATION.FONT;
            }
        }
    }
}
