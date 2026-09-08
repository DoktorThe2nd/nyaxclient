package com.doktorthe2nd.nyax.luajobjs;

import android.content.Context;
import android.widget.LinearLayout;

/** Adds some convenience functions. */
public class UILinearLayout extends LinearLayout {
    public UILinearLayout(Context context) {
        super(context);
    }

    public UILinearLayout gravity(int value) {
        this.setGravity(value);
        return this;
    }

    public UILinearLayout wrapContent() {
        UIBuilder.setWrapContent(this);
        return this;
    }
}
