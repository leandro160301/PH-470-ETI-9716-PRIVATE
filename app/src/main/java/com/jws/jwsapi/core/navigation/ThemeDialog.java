package com.jws.jwsapi.core.navigation;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.ToastHelper;

public class ThemeDialog {
    private final Context context;
    private final ThemeInterface themeInterface;

    public ThemeDialog(Context context, ThemeInterface themeInterface) {
        this.context = context;
        this.themeInterface = themeInterface;
    }
    public void showDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context.getApplicationContext(), R.style.AlertDialogCustom);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialogo_temas, null);

        Button Cancelar =  mView.findViewById(R.id.buttonc);
        TextView tvTema1 =  mView.findViewById(R.id.tvTema1);
        TextView tvTema2 =  mView.findViewById(R.id.tvTema2);
        TextView tvTema3 =  mView.findViewById(R.id.tvTema3);

        if(themeInterface.getPreferencesManagerBaseTheme() ==R.style.AppTheme_NoActionBar){
            setupTextTheme(tvTema1, context.getString(R.string.dialog_theme_rojo));

        }
        if(themeInterface.getPreferencesManagerBaseTheme() ==R.style.AppTheme2_NoActionBar){
            setupTextTheme(tvTema2, context.getString(R.string.dialog_theme_azul));

        }
        if(themeInterface.getPreferencesManagerBaseTheme() ==R.style.AppTheme4_NoActionBar){
            setupTextTheme(tvTema3, context.getString(R.string.dialog_theme_negro));
        }

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        tvTema1.setOnClickListener(view -> {
            themeInterface.setupTheme(R.style.AppTheme_NoActionBar);
            setupThemeUi(tvTema1, tvTema2, tvTema3);
        });
        tvTema2.setOnClickListener(view -> {
            themeInterface.setupTheme(R.style.AppTheme2_NoActionBar);
            setupThemeUi(tvTema2, tvTema1, tvTema3);
        });
        tvTema3.setOnClickListener(view -> {
            themeInterface.setupTheme(R.style.AppTheme4_NoActionBar);
            setupThemeUi(tvTema3, tvTema2, tvTema1);
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    private void setupTextTheme(TextView tvTema1, String text) {
        tvTema1.setText(text);
        tvTema1.setBackgroundResource(R.drawable.fondoinfoprincipal);
    }

    private void setupThemeUi(TextView tvTema1, TextView tvTema2, TextView tvTema3) {
        ToastHelper.message(context.getString(R.string.toast_theme_change),R.layout.item_customtoast,context);
        tvTema1.setBackgroundResource(R.drawable.fondoinfoprincipal);
        tvTema2.setBackgroundResource(R.drawable.stylekeycor3);
        tvTema3.setBackgroundResource(R.drawable.stylekeycor3);
    }

}
