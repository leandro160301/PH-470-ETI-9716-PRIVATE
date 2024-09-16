package com.jws.jwsapi.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.jws.jwsapi.R;
import java.util.List;

public class SpinnerHelper {
    public static void setupSpinner(Spinner spinner, Context context, List<String> lista) {
        spinner.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                R.layout.item_spinner,
                lista
        );
        spinner.setAdapter(adapter);
    }
}
