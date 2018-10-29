package helper;


import android.content.ContentValues;

import com.thesejongproject.customviews.AutoSpannable;


/**
 * Created by lgvalle on 08/02/15.
 */
public interface SpannableText extends AutoSpannable {
    ContentValues getValue();

    void setValue(ContentValues value);
}
