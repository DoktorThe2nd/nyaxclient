package com.doktorthe2nd.nyax.luajobjs;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.widget.TextView;

/** Adds some convenience functions. */
public class UITextView extends TextView {
    public UITextView(Context context) {
        super(context);
    }

    public UITextView align(int value) {
        this.setTextAlignment(value);
        return this;
    }

    public UITextView size(float value) {
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, value);
        return this;
    }

    public UITextView scale(float value) {
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.getTextSize()*value);
        return this;
    }
}
