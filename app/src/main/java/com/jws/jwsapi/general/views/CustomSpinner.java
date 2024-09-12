package com.jws.jwsapi.general.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;

public class CustomSpinner extends androidx.appcompat.widget.AppCompatSpinner {

    public CustomSpinner(Context context) {
        super(context);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter);
        // No necesita hacer nada más aquí
    }

    @Override
    public boolean performClick() {
        // Mostrar la ventana emergente personalizada al hacer clic en el Spinner
        showPopupWindow();
        return true;
    }

    private void showPopupWindow() {
        // Crear una ventana emergente con el ListView personalizado
        PopupWindow popupWindow = new PopupWindow(getContext());
        CustomSpinnerListView listView = new CustomSpinnerListView(getContext());

        // Convertir el adaptador del Spinner a ListAdapter y asignarlo al ListView
        ListAdapter listAdapter = (ListAdapter) getAdapter();
        listView.setAdapter(listAdapter);

        // Configurar el ListView y la ventana emergente
        listView.setOnItemClickListener((parent, view, position, id) -> {
            setSelection(position);
            popupWindow.dismiss();
        });

        popupWindow.setContentView(listView);
        popupWindow.setWidth(getWidth());
        popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(this);
    }
}