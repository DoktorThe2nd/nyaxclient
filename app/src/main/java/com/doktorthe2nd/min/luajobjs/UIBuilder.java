package com.doktorthe2nd.min.luajobjs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doktorthe2nd.min.MainActivity;
import com.doktorthe2nd.min.types.Stored;

import org.luaj.vm2.Varargs;

public class UIBuilder {
    public static class UISettings {
        UISettings() {}

        //public Typeface font = null; // null = default
        public final Stored<Float> textSize = new Stored<>("ui_textSize", 16f);
        public final Stored<Integer> buttonFillColor = new Stored<>("ui_buttonFillColor", Color.BLUE);
        public final Stored<Integer> buttonStrokeColor = new Stored<>("ui_buttonStrokeColor", Color.DKGRAY);
        public final Stored<Integer> buttonStrokeWidth = new Stored<>("ui_buttonStrokeWidth", 5);
        public final Stored<Float> buttonCornerRadius = new Stored<>("ui_buttonCornerRadius", 30f);
        public final Stored<Integer> containerPadding = new Stored<>("ui_containerPadding", 10);

        public void apply(View v) {
            if (v instanceof TextView) {
                TextView textView = (TextView)v;
                //if (font != null) textView.setTypeface(font);
                textView.setTextSize(textSize.get());
            }
            if (v instanceof Button) {
                GradientDrawable bg = new GradientDrawable();
                bg.setColor(buttonFillColor.get());
                bg.setShape(GradientDrawable.RECTANGLE);
                bg.setCornerRadius(buttonCornerRadius.get());
                bg.setStroke(buttonStrokeWidth.get(), buttonStrokeColor.get());
                v.setBackground(bg);
            }
            if (v instanceof ViewGroup) {
                v.setPadding(containerPadding.get(),
                        containerPadding.get(),
                        containerPadding.get(),
                        containerPadding.get());
            }
        }
    }
    public static UISettings settings = new UISettings();

    private static int getStatusBarHeight(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            WindowMetrics metrics = wm.getCurrentWindowMetrics();
            Insets insets = metrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.statusBars());
            result = insets.top;
        }
        return result;
    }

    public static boolean setContentView(View view) {
        if (MainActivity.weakDead()) return false;
        view.setPadding(settings.containerPadding.get(),
                getStatusBarHeight(MainActivity.appContext),
                settings.containerPadding.get(),
                settings.containerPadding.get());
        MainActivity.getWeak().setContentView(view);
        return true;
    }

    public static LinearLayout makeLayout(boolean horizontal) {
        LinearLayout lay = new LinearLayout(MainActivity.appContext);
        settings.apply(lay);
        if (horizontal) lay.setOrientation(LinearLayout.HORIZONTAL);
        else lay.setOrientation(LinearLayout.VERTICAL);
        return lay;
    }

    public static Button makeButton(String label, String onClickEvent, Varargs args) {
        Button btn = new Button(MainActivity.appContext);
        settings.apply(btn);
        btn.setText(label);
        btn.setOnClickListener(v -> MainActivity.luajThread.callEvent(onClickEvent, args));
        return btn;
    }

    public static TextView makeText(String text) {
        TextView tv = new TextView(MainActivity.appContext);
        settings.apply(tv);
        tv.setText(text);
        return tv;
    }
}
