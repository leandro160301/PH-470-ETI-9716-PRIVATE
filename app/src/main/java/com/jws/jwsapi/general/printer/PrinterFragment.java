package com.jws.jwsapi.general.printer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jws.jwsapi.general.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.general.printer.preferences.PreferencesPrinterManager;
import com.jws.jwsapi.general.user.UsersManager;
import com.jws.jwsapi.general.label.LabelManager;
import com.jws.jwsapi.general.data.PreferencesManager;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PrinterFragment extends Fragment{
    @Inject
    PreferencesManager preferencesManager;
    @Inject
    LabelManager labelManager;
    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    TextView tv_ipimpresora;
    Spinner sp_impresora;
    PrinterManager imprimirStandar;
    PreferencesPrinterManager preferencesPrinterManager;
    @Inject
    UsersManager usersManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_impresoras,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        configuracionBotones();
        tv_ipimpresora=view.findViewById(R.id.tv_ipimpresora);
        sp_impresora= view.findViewById(R.id.sp_impresora);
        imprimirStandar=new PrinterManager(getContext(),mainActivity,usersManager,preferencesManager,labelManager);
        preferencesPrinterManager= new PreferencesPrinterManager(mainActivity);
        tv_ipimpresora.setText(preferencesPrinterManager.consultaIP());
        tv_ipimpresora.setOnClickListener(view1 -> Teclado(tv_ipimpresora,"Ingrese IP de Impresora"));


        String[] Lote_arr = getResources().getStringArray(R.array.ImpresoraModo);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lote_arr);
        adapter3.setDropDownViewResource(R.layout.item_spinner);
        sp_impresora.setAdapter(adapter3);
        sp_impresora.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_impresora.setSelection(preferencesPrinterManager.consultaModo());
        sp_impresora.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=2){
                    preferencesPrinterManager.setModo(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void configuracionBotones() {
        if (buttonProvider != null) {
            bt_home = buttonProvider.getButtonHome();
            bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            bt_3 = buttonProvider.getButton3();
            bt_4 = buttonProvider.getButton4();
            bt_5 = buttonProvider.getButton5();
            bt_6 = buttonProvider.getButton6();
            buttonProvider.getTitulo().setText("CONFIGURACION DE IMPRESORAS");

            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }
    public void Teclado(TextView View,String texto){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());

        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopcionespuntos, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        TextView textView=mView.findViewById(R.id.textViewt);
        textView.setText(texto);
        userInput.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        userInput.setKeyListener(DigitsKeyListener.getInstance(".0123456789"));

        userInput.setOnLongClickListener(v -> true);

        userInput.requestFocus();

        if(!View.getText().toString().equals("") && !View.getText().toString().equals("-")){
            userInput.setSelection(userInput.getText().length());
        }
        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            if(!userInput.getText().toString().equals("")) {
                preferencesPrinterManager.setIP(userInput.getText().toString());
                View.setText(userInput.getText().toString());
            }
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }
}


