package helper;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.thesejongproject.R;


/**
 * Created by lgvalle on 07/02/15.
 */
public class SpannableEnglishWord implements SpannableText {

    private static final String TAG = SpannableText.class.getSimpleName();
    private final float marginSize;
    private float fontSize;
    private ContentValues value;

    public SpannableEnglishWord(Context ctx, ContentValues value) {
        this.value = value;
        this.fontSize = ctx.getResources().getDimension(R.dimen.font_size);
        this.marginSize = ctx.getResources().getDimension(R.dimen.text_margin);
    }

    @Override
    public float autoSpan() {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Rect bounds = new Rect();
        paint.getTextBounds(value.getAsString("EnglishWord"), 0, value.getAsString("EnglishWord").length(), bounds);
        float textMeasure = bounds.width();
        Log.d(TAG, "Measured: " + textMeasure);
        Log.d(TAG, "Margin:" + marginSize);
        return textMeasure + marginSize;
    }

    @Override
    public ContentValues getValue() {
        return value;
    }

    @Override
    public void setValue(ContentValues value) {
        this.value = value;
    }
}
