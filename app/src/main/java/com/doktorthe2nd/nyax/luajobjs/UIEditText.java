package com.doktorthe2nd.nyax.luajobjs;

import android.content.Context;
import android.text.InputFilter;
import android.widget.EditText;

public class UIEditText extends EditText {
    public UIEditText(Context context) {
        super(context);
    }

    public UIEditText addFilter(InputFilter filter) {
        InputFilter[] filters = this.getFilters();
        InputFilter[] newFilters = new InputFilter[filters.length+1];
        System.arraycopy(filters, 0, newFilters, 0, filters.length);
        newFilters[filters.length] = filter;
        this.setFilters(newFilters);
        return this;
    }

    public UIEditText setMaxLength(int chars) {
        return addFilter(new InputFilter.LengthFilter(chars));
    }
}
