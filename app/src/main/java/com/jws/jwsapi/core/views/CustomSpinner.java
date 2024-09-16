package com.jws.jwsapi.core.views;

import android.annotation.SuppressLint;
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
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean performClick() {
        showPopupWindow();
        return true;
    }

    private void showPopupWindow() {
        PopupWindow popupWindow = new PopupWindow(getContext());
        CustomSpinnerListView listView = new CustomSpinnerListView(getContext());

        ListAdapter listAdapter = (ListAdapter) getAdapter();
        listView.setAdapter(listAdapter);
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