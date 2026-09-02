package com.doktorthe2nd.nyax.luaj;

import android.app.Activity;
import android.widget.TextView;

import com.doktorthe2nd.nyax.MainActivity;
import com.doktorthe2nd.nyax.R;

class LuajErrorScreen {
    public static void set(String error_text) {
        System.err.println("Luaj error: "+error_text);
        if (MainActivity.weakDead()) return;
        MainActivity.runOnUi.run(() -> {
            Activity activity = MainActivity.getWeak();
            activity.setContentView(R.layout.luaj_error);
            TextView text = activity.findViewById(R.id.luaj_error_text);
            text.setText(error_text);
        });
    }
}
