package customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class TextViewNew extends TextView {

    public TextViewNew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewNew(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/montserrat-hairline.ttf");
            setTypeface(tf);
        }
    }

}