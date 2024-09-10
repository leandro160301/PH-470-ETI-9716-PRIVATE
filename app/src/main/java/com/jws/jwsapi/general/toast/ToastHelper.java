package com.jws.jwsapi.general.toast;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jws.jwsapi.R;

public class ToastHelper {
    private static Toast currentToast;

    public static void message(String texto, int layoutResId, Context context) {
        if (currentToast != null) {
            currentToast.cancel();
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(layoutResId, null);

        TextView text = layout.findViewById(R.id.text);
        text.setText(texto);
        currentToast = new android.widget.Toast(context);
        currentToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        currentToast.setDuration(android.widget.Toast.LENGTH_LONG);
        currentToast.setView(layout);
        currentToast.show();
    }
}
