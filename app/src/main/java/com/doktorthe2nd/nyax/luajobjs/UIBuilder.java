package com.doktorthe2nd.nyax.luajobjs;

import android.content.Context;
import android.graphics.Insets;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doktorthe2nd.nyax.MainActivity;
import com.doktorthe2nd.nyax.types.stored.Stored;

public class UIBuilder {
    //public static Typeface font = null; // null = default
    public static final Stored<Float> TEXT_SIZE = Stored.makeFloat("ui_textSize", 16f);

    public static int getStatusBarHeight() {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowManager wm = (WindowManager) MainActivity.appContext.getSystemService(Context.WINDOW_SERVICE);
            WindowMetrics metrics = wm.getCurrentWindowMetrics();
            Insets insets = metrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.statusBars());
            result = insets.top;
        }
        return result;
    }

    public static boolean setContentView(View view) {
        if (MainActivity.weakDead()) return false;
        MainActivity.runOnUi.run(() -> MainActivity.getWeak().setContentView(view));
        return true;
    }

    public static GradientDrawable newGradientDrawable() {
        return new GradientDrawable();
    }

    public static void setWrapContent(View view) {
        var params = view.getLayoutParams();
        if (params == null) {
            view.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return;
        }
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }
    public static void setMargin(View view, int left, int top, int right, int bottom) {
        var params = view.getLayoutParams();
        if (params == null) {
            ViewGroup.MarginLayoutParams marginParams =
                    new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginParams.setMargins(left, top, right, bottom);
            view.setLayoutParams(marginParams);
            return;
        }
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams)params;
            marginParams.setMargins(left, top, right, bottom);
        }
    }

    public static LinearLayout makeLayout(boolean horizontal) {
        LinearLayout lay = new LinearLayout(MainActivity.appContext);
        if (horizontal) lay.setOrientation(LinearLayout.HORIZONTAL);
        else lay.setOrientation(LinearLayout.VERTICAL);
        return lay;
    }

    public static Button makeButton(String label) {
        Button btn = new Button(MainActivity.appContext);
        btn.setText(label);
        return btn;
    }

    public static TextView makeText(String text) {
        TextView tv = new TextView(MainActivity.appContext);
        tv.setText(text);
        return tv;
    }
}
