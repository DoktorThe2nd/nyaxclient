package com.doktorthe2nd.min.luajobjs;

import android.content.Context;
import android.graphics.Insets;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doktorthe2nd.min.MainActivity;
import com.doktorthe2nd.min.types.stored.StoredFloat;

public class UIBuilder {
    //public Typeface font = null; // null = default
    public final StoredFloat textSize = new StoredFloat("ui_textSize", 16f);

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
